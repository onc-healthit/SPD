package com.esacinc.spd.DataGenerator;

import com.esacinc.spd.model.*;
import com.esacinc.spd.model.complex_extensions.IDigitalCertificate;
import com.esacinc.spd.model.complex_extensions.IQualification;
import com.esacinc.spd.util.*;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Organization.OrganizationContactComponent;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BulkOrganizationBuilder implements IDigitalCertificate, IQualification {
	
	
	/**
	 * uses the connection provided to get all organization and then builds a list
	 * of Organization objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirOrganization> getOrganizations(Connection connection) throws SQLException, ParseException {
		List<VhDirOrganization> organizations = new ArrayList<VhDirOrganization>();
		
	
		int cnt = 0;
		int certCount = 0;
		int orgId = 0;
		String limit = (DatabaseUtil.GLOBAL_LIMIT > 0) ? " LIMIT " +DatabaseUtil.GLOBAL_LIMIT : "";
	    ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_organization WHERE organization_id > " + BulkDataApp.FROM_ID_ORGANIZATIONS + " ORDER BY organization_id " + limit,null);
		while (resultSet.next() && BulkDataApp.okToProceed(cnt)) {
			VhDirOrganization org = new VhDirOrganization();
			try {
			// set the id
			orgId = resultSet.getInt("organization_id");
			org.setId(resultSet.getString("organization_id"));
			ErrorReport.setCursor("VhDirOrganization", org.getId());

			System.out.println("OrgId: " + orgId);  // temporary debugging for now
			
			org.setText(ResourceFactory.makeNarrative("Organization (id: " + orgId + ")"));

		
			// Handle description
			org.setDescription(resultSet.getString("description"));
			
			// Add a digital certificate to the first 3 organizations
			if (certCount < DigitalCertificateFactory.MAX_CERTS) {
				// args are:  nthCert, type, use, trustFramework, standard, expirationDate
				System.out.println("Adding a cert");
				VhDirDigitalCertificate cert = DigitalCertificateFactory.makeDigitalCertificate(certCount++, "role", "auth", "other", "x.509v3", null);
				org.addDigitalcertficate(cert);
			}
			
        	handleQualifications(connection, org, orgId);
			
			// Handle the identifiers
			handleIdentifiers(connection, org, orgId);
			
			// 
			org.setActive(resultSet.getBoolean("active"));
			
			// TODO Hard coded type as provider for now
			CodeableConcept typeConcept = new CodeableConcept();
            typeConcept.addCoding(ResourceFactory.makeCoding("prov", "Healthcare Provider", "http://terminology.hl7.org/CodeSystem/organization-type", false));
            org.addType(typeConcept);
					
            // Handle the name
            org.setDescription(resultSet.getString("name"));
         			
			// Handle aliases
         	handleAliases(connection, resultSet, org, orgId);

         	// Now that we have aliases, we can handle the case where name isn't specified in the db
			String name = resultSet.getString("name");
			if (name == null || name.isEmpty()) {
				// If no name, the use the first alias, if there is one.
				List<StringType> names = org.getAlias();
				if (names != null && names.size() > 0) {
					name = names.get(0).asStringValue();
				}
				else name = "Not provided";
			}
			org.setName(name);


            // Handle the telecoms
         	handleTelecoms(connection, org, orgId);
         	
			// Handle the addresses
         	handleAddresses(connection, org, orgId);
			
			// Handle the partOf association reference
         	org.setPartOf(ResourceFactory.makeResourceReference(resultSet.getString("partOf_organization_id"), "vhdir_organization", null, "Part of Organization"));
			
			// Handle contacts
         	handleContacts(connection, org, orgId);
         	
         	// Handle endpoints
         	handleEndpoints(connection, org, orgId);

            // Handle the restrictions
         	handleRestrictions(connection, org, orgId);

			organizations.add(org);
			}
			catch (Exception e) {
				e.printStackTrace();
				ErrorReport.writeError("VhDirOrganization", String.valueOf(orgId), "getOrganizations", e.getMessage());

			}
			cnt++;
		}
		System.out.println("Made " + organizations.size() + " organizations");
		return organizations;
	}
	
	/**
	 * Handles all the elements of the identifiers for Organizations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from identifier where organization_id = ?", orgId);
		while(resultset.next()) {
				VhDirIdentifier identifier = ResourceFactory.getIdentifier(resultset);
				org.addIdentifier(identifier);
		}
	}
	
	/**
	 * Handles the addresses for the passed in organization ID
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleAddresses(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT address_id from address where organization_id = ?", orgId);
		while(resultset.next()) {
			VhDirAddress addr = ResourceFactory.getAddress(resultset.getInt("address_id"), connection);
			org.addAddress(addr);
		}
	}
	
	/**
	 * Handles the telecoms for the organization id passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleTelecoms(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from telecom where organization_id = ?", orgId);
		while(resultset.next()) {
			VhDirTelecom tele = ContactFactory.getTelecom(resultset,connection);
			if (!tele.hasAvailableTime()) {
				// Add a 24x7 available time for this telecom contact point
				tele.addAvailableTime(ContactFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat;sun", true, null, null));
			}
			org.addTelecom(tele);
		}
	}
	
	/**
	 * Handles the contacts for the organization ID passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleContacts(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
     	ResultSet resultset = DatabaseUtil.runQuery(connection, 
     			"SELECT n.name_id, n.use, n.prefix, n.family, n.given, n.suffix, n.period_start, n.period_end, " +
				"oc.contact_id " +
     			"FROM name as n, contact as oc " +
     			"WHERE n.name_id = oc.name_id AND oc.organization_id = ?", 
     			orgId);
		while(resultset.next()) {
			OrganizationContactComponent occ = ContactFactory.getOrganizationContact(resultset, connection);
			org.addContact(occ);
		}
	}
	
	/**
	 * Handles the aliases for an organization using the organization ID passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleAliases(Connection connection, ResultSet resultSet, VhDirOrganization org, int orgId) throws SQLException {
        ResultSet aliasResults = DatabaseUtil.runQuery(connection, "SELECT * FROM organization_alias WHERE organization_id = ?", 3);
        while(aliasResults.next()) {
			VhDirAlias alias = new VhDirAlias();
			
			// Set id
			alias.setId(aliasResults.getString("organization_alias_id"));
			alias.setPeriod(ResourceFactory.makePeriod(aliasResults.getDate("period_start"), aliasResults.getDate("period_end")));
			alias.setType(ResourceFactory.getCodeableConcept(aliasResults.getInt("alias_type_cc_id"), connection));
			alias.setValue(new StringType(aliasResults.getString("value")));
			org.addOrgAlias(alias);
			org.addAlias(aliasResults.getString("value")); // Add base profile alias type
		}
	}
	

	/**
	 * Handle the endpoint references associated with the organization 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleEndpoints(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from vhdir_endpoint where organization_id = ?", orgId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("endpoint_id"), "VhDirEndpoint", null, "Organization Endpoint");
			org.addEndpoint(ref);  
		}
	}

	/**
	 * Handle the restrictions associated with the organization 
	 * @param connection
	 * @param prac
	 * @param pracId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where organization_restriction_id = ?", orgId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			org.addUsageRestriction(ref);
		}
	}

	/**
	 * Handles all the elements of the qualifications for Organizations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleQualifications(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from qualification where organization_id = ?", orgId);
		while(resultset.next()) {
			VhDirQualification qu = ResourceFactory.getVhDirQualification(resultset, connection);
			org.addQualification(qu);
		}
	}
}
