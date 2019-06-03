package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanContactComponent;
import org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent;
import org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent;
import org.hl7.fhir.r4.model.Reference;

import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirInsurancePlan;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.ErrorReport;
import com.esacinc.spd.util.InsurancePlanFactory;
import com.esacinc.spd.util.ResourceFactory;

public class BulkInsurancePlanBuilder {
	
	
	/**
	 * uses the connection provided to get all VhDirInsurancePlans and then builds a list
	 * of Practitioner objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirInsurancePlan> getInsurancePlans(Connection connection) throws SQLException, ParseException {
		int cnt = 0;
		List<VhDirInsurancePlan> insurancePlans = new ArrayList<VhDirInsurancePlan>();
        ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_insurance_plan WHERE insurance_plan_id > " + BulkDataApp.FROM_ID_INSURANCEPLANS + " ORDER BY insurance_plan_id",null);
		while (resultSet.next() && BulkDataApp.okToProceed(cnt)) {
			VhDirInsurancePlan ip = new VhDirInsurancePlan();
		
			// set the id
			int ipId = resultSet.getInt("insurance_plan_id");
			ip.setId(resultSet.getString("insurance_plan_id"));
			ErrorReport.setCursor("VhDirInsurancePlan", ip.getId());
			
			ip.setText(ResourceFactory.makeNarrative("InsurancePlan (id: " + ipId + ")"));

			try {
				ip.setStatus(PublicationStatus.fromCode(resultSet.getString("status")));
			}
			catch (Exception e) {
				ip.setStatus(PublicationStatus.UNKNOWN);
				ErrorReport.writeWarning("VhDirInsurancePlan", ip.getId(), "Unrecognized status", e.getMessage());

			}
			ip.setName(resultSet.getString("name"));
			ip.setPeriod(ResourceFactory.makePeriod(resultSet.getDate("period_start"), resultSet.getDate("period_end")));
			ip.setOwnedBy(ResourceFactory.getResourceReference(resultSet.getInt("ownedBy_reference_id"),connection)); 
			ip.setAdministeredBy(ResourceFactory.getResourceReference(resultSet.getInt("administeredBy_reference_id"),connection)); 

			// Handle the restrictions
         	handleRestrictions(connection, ip, ipId);
 
         	handleIdentifiers(connection, ip, ipId);
         	
			handleTypes(connection, ip, ipId);
			
			handleAliases(resultSet, ip, ipId);

			handleCoverageAreas(connection, ip, ipId);

			handleContacts(connection, ip, ipId);

         	handleEndpoints(connection, ip, ipId);
			
			handleNetworks(connection, ip, ipId);
			
			handleCoverages(connection, ip, ipId);

			handlePlans(connection, ip, ipId);

			insurancePlans.add(ip);
			
			cnt++;
		}
		System.out.println("Made " + insurancePlans.size() + " insurancePlans");
		return insurancePlans;
	}

	

	/**
	 * Handles all the  Aliases  for InsurancePlans
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleAliases(ResultSet resultset, VhDirInsurancePlan ip, int ipId) throws SQLException {
		// For now, let's assume that all of the aliases are present in the db as a ; delimited list
		String aliases = resultset.getString("alias");
		if (aliases != null && !aliases.isEmpty()) {
			String[] als = aliases.split(";");
			for (String al : als) {
				ip.addAlias(al);
			}
		}
	}


	/**
	 * Handles all the  types  for InsurancePlans
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleTypes(Connection connection, VhDirInsurancePlan ip, int ipId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where insurance_plan_type_id = ?", ipId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			ip.addType(cc);
		}
	}

	/**
	 * Handles all the  Contacts  for InsurancePlans
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleContacts(Connection connection, VhDirInsurancePlan ip, int ipId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from contact where insurance_plan_id = ?", ipId);
		while(resultset.next()) {
			InsurancePlanContactComponent con = InsurancePlanFactory.getInsurancePlanContact(resultset,connection);
			ip.addContact(con);
		}
	}


	/**
	 * Handles all the  Coverage Areas  for InsurancePlans
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleCoverageAreas(Connection connection, VhDirInsurancePlan ip, int ipId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from resource_reference where insurance_plan_coverageArea_id = ?", ipId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			ip.addCoverageArea(ref);
		}
	}

	/**
	 * Handles all the  networks  for InsurancePlans
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleNetworks(Connection connection, VhDirInsurancePlan ip, int ipId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from resource_reference where insurance_plan_network_id = ?", ipId);
		while(resultset.next()) {
			Reference nw = ResourceFactory.getResourceReference(resultset,connection);
			ip.addNetwork(nw);
		}
	}

	/**
	 * Handles all the  Coverages  for InsurancePlans
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleCoverages(Connection connection, VhDirInsurancePlan ip, int ipId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from coverage where insurance_plan_id = ?", ipId);
		while(resultset.next()) {
			InsurancePlanCoverageComponent cov = InsurancePlanFactory.getCoverage(resultset, connection);
			ip.addCoverage(cov);
		}
	}

	/**
	 * Handles all the  Plans  for InsurancePlans
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handlePlans(Connection connection, VhDirInsurancePlan ip, int ipId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from plan where insurance_plan_id = ?", ipId);
		while(resultset.next()) {
			InsurancePlanPlanComponent plan = InsurancePlanFactory.getPlan(resultset, connection);
			ip.addPlan(plan);
		}
	}



	/**
	 * Handles all the elements of the identifiers for InsurancePlans
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirInsurancePlan ip, int ipId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from identifier where insurance_plan_id = ?", ipId);
		while(resultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(resultset);
			ip.addIdentifier(identifier);
		}
	}
	
	

	
	/**
	 * Handle the restrictions associated with the healthcare service 
	 * @param connection
	 * @param prac
	 * @param ipId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirInsurancePlan ip, int ipId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where healthcare_service_restriction_id = ?", ipId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			ip.addUsageRestriction(ref);
		}
	}
	
	/**
	 * Handle the endpoints associated with the healthcare service 
	 * @param connection
	 * @param prac
	 * @param ipId
	 * @throws SQLException
	 */
	private void handleEndpoints(Connection connection, VhDirInsurancePlan ip, int ipId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from vhdir_endpoint where insurance_plan_id = ?", ipId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("endpoint_id"), "VhDirEndpoint", null, "InsurancePlan Endpoint");
			ip.addEndpoint(ref);
		}
	}

}
