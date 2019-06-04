package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Reference;


import com.esacinc.spd.model.VhDirTelecom;
import com.esacinc.spd.model.VhDirOrganizationAffiliation;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.util.ContactFactory;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.ErrorReport;
import com.esacinc.spd.util.ResourceFactory;

public class BulkOrganizationAffiliationBuilder {
	
	
	/**
	 * uses the connection provided to get all VhDirOrganizationAffiliations and then builds a list
	 * of Practitioner objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirOrganizationAffiliation> getOrganizationAffiliations(Connection connection) throws SQLException, ParseException {
		int cnt = 0;
		List<VhDirOrganizationAffiliation> organizationaffiliations = new ArrayList<VhDirOrganizationAffiliation>();
		String limit = (DatabaseUtil.GLOBAL_LIMIT > 0) ? " LIMIT " +DatabaseUtil.GLOBAL_LIMIT : "";
	    ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_organization_affiliation WHERE organization_affiliation_id > " + BulkDataApp.FROM_ID_ORGANIZATIONAFFILIATIONS + " ORDER BY organization_affiliation_id " + limit,null);
		while (resultSet.next() && BulkDataApp.okToProceed(cnt)) {
			//System.out.println("Creating organizationaffiliation for id " + resultSet.getInt("organization_affiliation_id"));
			VhDirOrganizationAffiliation of = new VhDirOrganizationAffiliation();
		
			// set the id
			int ofId = resultSet.getInt("organization_affiliation_id");
			of.setId(resultSet.getString("organization_affiliation_id"));
			ErrorReport.setCursor("VhDirOrganizationAffiliation", of.getId());

			of.setText(ResourceFactory.makeNarrative("OrganizationAffiliation (id: " + ofId + ")"));

			of.setActive(resultSet.getBoolean("active"));
			of.setPeriod(ResourceFactory.makePeriod(resultSet.getDate("period_start"), resultSet.getDate("period_end")));
			// TODO the following two should really be ids into resource reference table, but currently they
			//      are ids into the organzation table.  
			of.setOrganization(ResourceFactory.makeResourceReference(resultSet.getString("organization_id"), "VhDirOrganization", null, "Organization"));
			of.setParticipatingOrganization(ResourceFactory.makeResourceReference(resultSet.getString("participating_organization_id"), "VhdirOrganization", null, "Participating Organization"));

			// Handle the restrictions
         	handleRestrictions(connection, of, ofId);

        	handleIdentifiers(connection, of, ofId);

        	handleNetworks(connection, of, ofId);
          	
			handleCodes(connection, of, ofId);

			handleSpecialties(connection, of, ofId);
			
			handleLocations(connection, of, ofId);
			
			handleHealthcareServices(connection, of, ofId);

         	handleTelecoms(connection, of, ofId);

         	// Handle the end points
         	handleEndpoints(connection, of, ofId);

			organizationaffiliations.add(of);
			
			cnt++;
		}
		System.out.println("Made " + organizationaffiliations.size() + " organizationaffiliations");
		return organizationaffiliations;
	}

	


	/**
	 * Handles all the  codes  for OrganizationAffiliations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleCodes(Connection connection, VhDirOrganizationAffiliation of, int ofId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where organization_affiliation_code_id = ?", ofId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			of.addCode(cc);
		}
	}

	/**
	 * Handles all the  Specialties  for OrganizationAffiliations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleSpecialties(Connection connection, VhDirOrganizationAffiliation of, int ofId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where organization_affiliation_specialty_id = ?", ofId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			of.addSpecialty(cc);
		}
	}

	/**
	 * Handles all the  Locations  for OrganizationAffiliations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleLocations(Connection connection, VhDirOrganizationAffiliation of, int ofId) throws SQLException {
		// TODO The database has OrgAff foreign keys to location  directly as opposed to having them be resource references.  Should change that someday!
		//      For now, just make a resource reference using the location id.
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from vhdir_location where organization_affiliation_id = ?", ofId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("location_id"), "VhDirLocation", null, "Organization Affiliation Location");
			of.addLocation(ref);
		}
	}

	/**
	 * Handles all the  Networks  for OrganizationAffiliations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleNetworks(Connection connection, VhDirOrganizationAffiliation of, int ofId) throws SQLException {
		// TODO The database has OrgAff foreign keys to networks directly as opposed to having them be resource references.  Should change that someday!
		//      For now, just make a resource reference using the network id.
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from vhdir_network where organization_affiliation_id = ?", ofId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("network_id"), "VhDirNetwork", null, "Organization Affiliation Network");
			of.addNetwork(ref);
		}
	}

	/**
	 * Handles all the  Healthcare Services  for OrganizationAffiliations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleHealthcareServices(Connection connection, VhDirOrganizationAffiliation of, int ofId) throws SQLException {
		// TODO The database has OrgAff foreign keys to healthcare services directly as opposed to having them be resource references.  Should change that someday!
		//      For now, just make a resource reference using the hcservice id.
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from vhdir_healthcare_service where organization_affiliation_id = ?", ofId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("healtcare_service_id"), "VhDirHealthcareService", null, "Organization Affiliation Healthcare Service");
			of.addHealthcareService(ref);
		}
	}

	


	/**
	 * Handles all the elements of the identifiers for OrganizationAffiliations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirOrganizationAffiliation of, int ofId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from identifier where organization_affiliation_id = ?", ofId);
		while(resultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(resultset);
			of.addIdentifier(identifier);
		}
	}
	
	
	/**
	 * Handles the telecoms for the practitioner id passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleTelecoms(Connection connection, VhDirOrganizationAffiliation of, int ofId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from telecom where organization_affiliation_id = ?", ofId);
		while(resultset.next()) {
				VhDirTelecom tele = ContactFactory.getTelecom(resultset,connection);
				tele.setId(resultset.getString("telecom_id"));
				if (!tele.hasAvailableTime()) {
					// Add 9:00-4:30 any day, available time for this telecom contact point
					tele.addAvailableTime(ContactFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
				}
				of.addTelecom(tele);
		}
	}
	
	
	
	/**
	 * Handle the restrictions associated with the healthcare service 
	 * @param connection
	 * @param prac
	 * @param ofId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirOrganizationAffiliation of, int ofId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where organization_affiliation_restriction_id = ?", ofId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			of.addUsageRestriction(ref);
		}
	}
	
	/**
	 * Handle the endpoints associated with the healthcare service 
	 * @param connection
	 * @param prac
	 * @param ofId
	 * @throws SQLException
	 */
	private void handleEndpoints(Connection connection, VhDirOrganizationAffiliation of, int ofId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from vhdir_endpoint where organization_affiliation_id = ?", ofId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("endpoint_id"), "VhDirEndpoint", null, "OrganizationAffiliation Endpoint");
			of.addEndpoint(ref);
		}
	}

}
