package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Consent.ConsentPolicyComponent;
import org.hl7.fhir.r4.model.Consent.ConsentProvisionType;
import org.hl7.fhir.r4.model.Consent.ConsentState;
import org.hl7.fhir.r4.model.Consent.provisionActorComponent;
import org.hl7.fhir.r4.model.Consent.provisionComponent;

import com.esacinc.spd.model.VhDirRestriction;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.ErrorReport;
import com.esacinc.spd.util.ResourceFactory;

public class BulkRestrictionBuilder {
	
	
	/**
	 * uses the connection provided to get all VhDirRestrictions and then builds a list
	 * of Practitioner objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirRestriction> getRestrictions(Connection connection) throws SQLException, ParseException {
		List<VhDirRestriction> restrictions = new ArrayList<VhDirRestriction>();
		int cnt = 0;
		String limit = (DatabaseUtil.GLOBAL_LIMIT > 0) ? " LIMIT " +DatabaseUtil.GLOBAL_LIMIT : "";
	    ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_restriction WHERE restriction_id > " + BulkDataApp.FROM_ID_RESTRICTIONS + " ORDER BY restriction_id " + limit,null);
		while (resultSet.next() && BulkDataApp.okToProceed(cnt)) {
			VhDirRestriction res = new VhDirRestriction();
		
			// set the id
			int resId = resultSet.getInt("restriction_id");
			res.setId(resultSet.getString("restriction_id"));
			ErrorReport.setCursor("VhDirRestriction", res.getId());

			res.setText(ResourceFactory.makeNarrative("Consent (id: " + resId + ")"));

			
			res.setDateTime(resultSet.getDate("date_time"));	
			
			handleScope(connection,resultSet, res);
			
			handleStatus(resultSet, res);
			
			handleCategories(connection, res, resId);

			handlePolicies(connection, resultSet, res,resId);
			
			handleProvision(connection, res, resultSet.getInt("provision_id"));
			
			restrictions.add(res);
			
			cnt++;
		}
		System.out.println("Made " + restrictions.size() + " restrictions");
		return restrictions;
	}

	private  void handleScope(Connection connection, ResultSet resultset, VhDirRestriction res) throws SQLException{
		int ccId = resultset.getInt("scope_cc_id");
		if (ccId != 0) {
			res.setScope(ResourceFactory.getCodeableConcept(resultset.getInt("scope_cc_id"),connection));
		} 
		// Has to be a scope. Make one up if needed.
		else {
			CodeableConcept cc = new CodeableConcept();
			cc.setText("privacy");
			Coding cdng = ResourceFactory.makeCoding("patient-privacy", "Privacy Consent", "http://terminology.hl7.org/CodeSystem/consentscope", false);
			cc.addCoding(cdng);
			res.setScope(cc);
		}
		
	}
	
	/**
	 * Handles a status  for Restrictions
	 * 
	 * @param connection
	 * @param val
	 * @param resId
	 * @throws SQLException
	 */
	private void handleStatus(ResultSet resultset, VhDirRestriction res){
		
		try {
			res.setStatus(ConsentState.fromCode(resultset.getString("status")));
		}
		catch (Exception e) {
			// Probably means status was not found in LocationStatus
			res.setStatus(ConsentState.NULL);
			ErrorReport.writeWarning("VhDirRestriction", res.getId(), "unrecognized status ", e.getMessage());

		}

	}

	/**
	 * Handles all the elements of the Categories for Restrictions
	 * 
	 * @param connection
	 * @param val
	 * @param resId
	 * @throws SQLException
	 */
	private void handleCategories(Connection connection, VhDirRestriction res, int resId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from fhir_codeable_concept where restriction_category_id = ?", resId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			res.addCategory(cc);
		}
		// Needs at least one category. Make one up if needed.
		if (res.getCategory() == null ||res.getCategory().isEmpty()) {
			CodeableConcept cc = new CodeableConcept();
			cc.setText("Information Access");
			Coding cdng = ResourceFactory.makeCoding("INFA", "information access", "http://terminology.hl7.org/CodeSystem/v3-ActCode", false);
			cc.addCoding(cdng);
			res.addCategory(cc);
			
		}
	}

	/**
	 * Handle Policies for Restrictions
	 * 
	 * @param connection
	 * @param val
	 * @param resId
	 * @throws SQLException
	 */
	private void handlePolicies(Connection connection, ResultSet resultSet, VhDirRestriction res, int resId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from policy where restriction_id = ?", resId);
		while(resultset.next()) {
			ConsentPolicyComponent cc = new ConsentPolicyComponent();
			cc.setId(resultset.getString("policy_id"));
			cc.setUri(resultset.getString("uri"));
			res.addPolicy(cc);
		}
		// Needs at least one policy. Make one up if needed
		if (res.getPolicy() == null ||res.getPolicy().isEmpty()) {
			ConsentPolicyComponent cc = new ConsentPolicyComponent();
			cc.setAuthority("policy");
			cc.setUri("http://www.acmc.gov/omnibus-policy.html");
			res.addPolicy(cc);
			
		}

	}

	/**
	 * Handles all the Provision  for Restrictions
	 * 
	 * @param connection
	 * @param val
	 * @param resId
	 * @throws SQLException
	 */
	private void handleProvision(Connection connection, VhDirRestriction res, int provId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from provision where provision_id = ?", provId);
		while(resultset.next()) {
			provisionComponent cc = new provisionComponent();
			cc.setId(resultset.getString("provision_id"));
			try {
				cc.setType(ConsentProvisionType.fromCode(resultset.getString("type")));
			}
			catch (Exception e) {
				cc.setType(ConsentProvisionType.PERMIT);
				ErrorReport.writeWarning("VhDirRestriction", res.getId(), "unrecognized type ", e.getMessage());

			}
			if (cc.getType() == null) {
				cc.setType(ConsentProvisionType.PERMIT);
			}
			// TODO this is a problem since the vhdir resource says action is a singleton (0..), while the standard has it as a list.
			CodeableConcept action = ResourceFactory.getCodeableConcept(resultset.getInt("action_cc_id"),connection);
			cc.addAction(action);
			
			ResultSet actorResultSet = DatabaseUtil.runQuery(connection,"select * from actor where provision_id = ?", resultset.getInt("provision_id"));
			while (actorResultSet.next()){
				provisionActorComponent actor = new provisionActorComponent();
				actor.setRole(ResourceFactory.getCodeableConcept(actorResultSet.getInt("role_cc_id"),connection));
				actor.setReference(ResourceFactory.getResourceReference(actorResultSet.getInt("reference_resource_id"), connection));
				cc.addActor(actor);
			}
			
			String securityLabels = resultset.getString("security_label"); // assume this is a ";" delimited string of security labels of the form code,display
			if (securityLabels != null && !securityLabels.isEmpty()) {
				String[] labels = securityLabels.split(";");
				for (String label : labels) {
					String [] tokens = label.split(",");
					String code = "";
					String display = "";
					if (tokens.length > 0) {
						code = tokens[0];
					} 
					else {
						ErrorReport.writeWarning("VhDirRestriction", res.getId(), "Security_labels: " + labels, "Security labels expected to be a ';' delimited list of code,display pairs such as 'U;unrestricted'");
					}
					if (tokens.length > 1) {
						display = tokens[1];
					}
					else {
						display = code;
						ErrorReport.writeWarning("VhDirRestriction", res.getId(), "Security_labels: " + labels, "Security labels expected to be a ';' delimited list of code,display pairs such as 'M;moderate'");
					}
					Coding labelCode = ResourceFactory.makeCoding(code, display , "http://hl7.org/fhir/ValueSet/security-labels", false);
					cc.addSecurityLabel(labelCode);
				}
			}

			String purposes = resultset.getString("purpose"); // assume this is a ";" delimited string of purposes
			if (purposes != null && !purposes.isEmpty()) {
				String[] purps = purposes.split(";");
				for (String purpose : purps) {
					String [] tokens = purpose.split(",");
					String code = "";
					String display = "";
					if (tokens.length > 0) {
						code = tokens[0];
					}
					else {
						ErrorReport.writeWarning("VhDirRestriction", res.getId(), "Purposes: " + purposes, "Purposes expected to be a ';' delimited list of code,display pairs such as 'HMARKT;healtcare marketing'");
					}
					if (tokens.length > 1) {
						display = tokens[1];
					}
					else {
						display = code;
						ErrorReport.writeWarning("VhDirRestriction", res.getId(), "Purposes: " + purposes, "Purposes expected to be a ';' delimited list of code,display pairs such as 'CAREMGT;care management'");
					}
					Coding purposeCode = ResourceFactory.makeCoding(code, display , "http://terminology.hl7.org/ValueSet/v3-PurposeOfUse", false);
					cc.addPurpose(purposeCode);
				}
			}

			while (actorResultSet.next()){
				provisionActorComponent actor = new provisionActorComponent();
				actor.setRole(ResourceFactory.getCodeableConcept(actorResultSet.getInt("role_cc_id"),connection));
				actor.setReference(ResourceFactory.getResourceReference(actorResultSet.getInt("reference_resource_id"), connection));
				cc.addActor(actor);
			}
			if (cc.getActor() == null || cc.getActor().isEmpty()) {
				provisionActorComponent actor = new provisionActorComponent();
				CodeableConcept ccncpt = new CodeableConcept();
				Coding cdng = ResourceFactory.makeCoding("IRCP","information recipient","http://terminology.hl7.org/CodeSystem/v3-ParticipationType",false);
				ccncpt.addCoding(cdng);
				actor.setRole(ccncpt);
				cc.addActor(actor);
			}
			res.setProvision(cc);
			return; // we are only expecting one provision
		}
		
		
		
	}


}
