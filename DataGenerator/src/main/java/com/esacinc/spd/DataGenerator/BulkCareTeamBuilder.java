package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CareTeam.CareTeamParticipantComponent;
import org.hl7.fhir.r4.model.CareTeam.CareTeamStatus;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

import com.esacinc.spd.model.VhDirCareTeam;
import com.esacinc.spd.model.VhDirTelecom;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirNote;
import com.esacinc.spd.util.ContactFactory;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.ErrorReport;
import com.esacinc.spd.util.ResourceFactory;

public class BulkCareTeamBuilder {
	
	
	/**
	 * uses the connection provided to get all careteams and then builds a list
	 * of Practitioner objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirCareTeam> getCareTeams(Connection connection) throws SQLException, ParseException {
		List<VhDirCareTeam> careteams = new ArrayList<VhDirCareTeam>();
		
		int cnt = 0;
        ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_careteam", null);
		while (resultSet.next() && BulkDataApp.okToProceed(cnt)) {
			VhDirCareTeam ct = new VhDirCareTeam();
		
			// set the id
			int ctId = resultSet.getInt("careteam_id");
			ct.setId(resultSet.getString("careteam_id"));
			
			ct.setName(resultSet.getString("name"));
			ct.setEncounter(ResourceFactory.getResourceReference(resultSet.getInt("encounter_id"), connection));
			ct.setPeriod(ResourceFactory.makePeriod(resultSet.getDate("period_start"), resultSet.getDate("period_end")));
			//TODO  Note that the base CareTeam resource has managingOrganization 0..*,  wherease VhDir has it as 0..1. Using base for now.
			ct.setManagingOrganization(ResourceFactory.getResourceReference(resultSet.getInt("managing_organization_id"), connection));

	        // Handle the status
	        handleStatus(resultSet,ct);
			
	        // Handle the restrictions
	        handleRestrictions(connection, ct, ctId);
						
			// Handle the aliases
			handleAliases(connection, ct, ctId);
			
			// Handle the locations
			handleLocations(connection, ct, ctId);
			
			// Handle the healthcare services
			handleServices(connection, ct, ctId);

         	// Handle the endpoints
         	handleEndpoints(connection, ct, ctId);

			// Handle the identifiers
			handleIdentifiers(connection, ct, ctId);
					
			// Handle the categories
			handleCategories(connection, ct, ctId);

			// Handle the participants
			handleParticipants(connection, ct, ctId);

            // Handle the telecoms
         	handleTelecoms(connection, ct, ctId);
         	
			// Handle the notes
         	handleNotes(connection, ct, ctId);
         	
          	
         	// Handle the endpoints
         	handleEndpoints(connection, ct, ctId);
			
			careteams.add(ct);
			
			cnt++;
		}
		System.out.println("Made " + careteams.size() + " care teams");
		return careteams;
	}

	/**
	 * Handles a status  for careteam
	 * 
	 * @param connection
	 * @param ct
	 * @param ctId
	 * @throws SQLException
	 */
	private void handleStatus(ResultSet resultset, VhDirCareTeam ct){
		try {
			ct.setStatus(CareTeamStatus.fromCode(resultset.getString("status")));
		}
		catch (Exception e) {
			// Probably means status was not found in LocationStatus
			ErrorReport.writeWarning("VhDirCareteam", ct.getId(), "Invalid status value ", "");
			ct.setStatus(CareTeamStatus.NULL);
		}
	}

	/**
	 * Handle the endpoint references associated with the CareTeam 
	 * @param connection
	 * @param ct
	 * @param ctId
	 * @throws SQLException
	 */
	private void handleEndpoints(Connection connection, VhDirCareTeam ct, int ctId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from vhdir_endpoint where careteam_id = ?", ctId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("endpoint_id"), "VhDirEndpoint", null, "Organization Endpoint");
			ct.addEndpointReference(ref);  
		}
	}


	/**
	 * Handles all the elements of the identifiers for CareTeam
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirCareTeam ct, int ctId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from identifier where careteam_id = ?", ctId);
		while(resultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(resultset);
			ct.addIdentifier(identifier);
		}
	}
	
	
	/**
	 * Handles the telecoms for the CareTeam id passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleTelecoms(Connection connection, VhDirCareTeam ct, int ctId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from telecom where careteam_id = ?", ctId);
		while(resultset.next()) {
			VhDirTelecom tele = ContactFactory.getTelecom(resultset,connection);
			if (!tele.hasAvailableTime()) {
				// Add 9:00-4:30 any day, available time for this telecom contact point
				tele.addAvailableTime(ContactFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
			}
			ct.addTelecom(tele);
		}
	}
	
	
	
	/**
	 * Handle the restrictions associated with the CareTeam
	 * @param connection
	 * @param ct
	 * @param ctId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirCareTeam ct, int ctId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where careteam_restriction_id = ?", ctId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			ct.addUsageRestriction(ref);
		}
	}

	/**
	 * Handle the locations associated with the CareTeam 
	 * @param connection
	 * @param ct
	 * @param ctId
	 * @throws SQLException
	 */
	private void handleLocations(Connection connection, VhDirCareTeam ct, int ctId) throws SQLException {
		// TODO This should really retrieve resource references where careteam_location_id = ?,  but the model uses vhdir_location fk when it should use resource_reference fk
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from vhdir_location where careteam_id = ?", ctId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("location_id"), "VhDirLocation", null, "Location Endpoint");
			ct.addEndpointReference(ref);  
		}
	}

	/**
	 * Handle the healthcare services associated with the CareTeam 
	 * @param connection
	 * @param ct
	 * @param ctId
	 * @throws SQLException
	 */
	private void handleServices(Connection connection, VhDirCareTeam ct, int ctId) throws SQLException {
		// TODO This should really retrieve resource references where careteam_location_id = ?,  but the model uses vhdir_healthcare_service fk when it should use resource_reference fk
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from vhdir_healthcare_service where careteam_id = ?", ctId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("healthcare_service_id"), "VhDirHealthcareSevice", null, "Healthcare Service");
			ct.addHealthcareServiceReference(ref);  
		}
	}

	/**
	 * Handle the aliases associated with the CareTeam 
	 * @param connection
	 * @param ct
	 * @param ctId
	 * @throws SQLException
	 */
	private void handleAliases(Connection connection, VhDirCareTeam ct, int ctId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from care_team_alias where careteam_id = ?", ctId);
		while(resultset.next()) {
			ct.addCareteamAlias(new StringType(resultset.getString("name")));  
		}
	}
	
	/**
	 * Handle the categories associated with the CareTeam 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleCategories(Connection connection, VhDirCareTeam ct, int ctId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from fhir_codeable_concept where careteam_category_id = ?", ctId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			ct.addCategory(cc);  
		}
	}

	/**
	 * Handle the notes associated with the CareTeam 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleNotes(Connection connection, VhDirCareTeam ct, int ctId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from note where careteam_note_id = ?", ctId);
		while(resultset.next()) {
			VhDirNote note = ResourceFactory.getNote(resultset);
			ct.addNote(note);  
		}
	}

	/**
	 * Handle the participants associated with the CareTeam 
	 * @param connection
	 * @param ct
	 * @param ctId
	 * @throws SQLException
	 */
	private void handleParticipants(Connection connection, VhDirCareTeam ct, int ctId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from participant where careteam_id = ?", ctId);
		while(resultset.next()) {
			CareTeamParticipantComponent par = ResourceFactory.getParticipantComponent(resultset, connection);
			ct.addParticipant(par);  
		}
		//CareTeamParticipantComponent cc = new CareTeamParticipantComponent();
		//Reference mem = new Reference();
		//cc.setMember(mem);
		//ct.addParticipant(cc);
	}

}
