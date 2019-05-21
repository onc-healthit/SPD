package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleAvailableTimeComponent;
import org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleNotAvailableComponent;

import com.esacinc.spd.model.VhDirAvailableTime;
import com.esacinc.spd.model.VhDirTelecom;
import com.esacinc.spd.model.VhDirPractitionerRole;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirNewpatients;
import com.esacinc.spd.util.ContactFactory;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.DigitalCertificateFactory;
import com.esacinc.spd.util.ErrorReport;
import com.esacinc.spd.util.ResourceFactory;


public class BulkPractitionerRoleBuilder {
	
	
	/**
	 * uses the connection provided to get all VhDirPractitionerRoles and then builds a list
	 * of Practitioner Role objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirPractitionerRole> getPractitionerRoles(Connection connection) throws SQLException, ParseException {
		int cnt = 0;
		List<VhDirPractitionerRole> practitionerroles = new ArrayList<VhDirPractitionerRole>();
        ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_practitioner_role", null);
		while (resultSet.next() && BulkDataApp.okToProceed(cnt)) {
			//System.out.println("Creating practitionerRole for id " + resultSet.getInt("practitioner_role_id"));
			VhDirPractitionerRole pr = new VhDirPractitionerRole();
		
			// set the id
			int prId = resultSet.getInt("practitioner_role_id");
			pr.setId(resultSet.getString("practitioner_role_id"));
			ErrorReport.setCursor("VhDirPractitionerRole", pr.getId());

			pr.setActive(resultSet.getBoolean("active"));
			pr.setPeriod(ResourceFactory.makePeriod(resultSet.getDate("period_start"), resultSet.getDate("period_end")));
			pr.setPractitioner(ResourceFactory.getResourceReference(resultSet.getInt("practitioner_id"), connection));
			pr.setOrganization(ResourceFactory.getResourceReference(resultSet.getInt("organization_id"), connection));
			pr.setAvailabilityExceptions(resultSet.getString("availability_exceptions"));	

			// Add a digital certificate to the first 3 organizations
			int certCount = 0;
         	if (certCount < DigitalCertificateFactory.MAX_CERTS) {
         		// args are:  nthCert, type, use, trustFramework, standard, expirationDate
         		pr.addDigitalcertficate(DigitalCertificateFactory.makeDigitalCertificate(certCount++, "role", "auth", "other", "x.509v3", null));
         	}

			// Handle the restrictions
         	handleRestrictions(connection, pr, prId);

         	handleNewPatients(connection, pr, prId);

         	handleIdentifiers(connection, pr, prId);

         	handleCodes(connection, pr, prId);
         	
			handleSpecialties(connection, pr, prId);
			
			handleLocations(connection, pr, prId);
			
			handleHealthcareServices(connection, pr, prId);

         	handleTelecoms(connection, pr, prId);
         	
         	handleAvailableTimes(connection, pr, prId);
			
			handleNotAvailableTimes(connection, pr, prId);
         	
         // Handle the end points
         	handleEndpoints(connection, pr, prId);
         	
			practitionerroles.add(pr);
			
			cnt++;
		}
		System.out.println("Made " + practitionerroles.size() + " practitionerroles");
		return practitionerroles;
	}

	

	/**
	 * Handles all the  Specialties  for PractitionerRoles
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleSpecialties(Connection connection, VhDirPractitionerRole pr, int prId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where practitioner_role_specialty_id = ?", prId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			pr.addSpecialty(cc);
		}
	}

	/**
	 * Handles all the  Codes  for PractitionerRoles
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleCodes(Connection connection, VhDirPractitionerRole pr, int prId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where practitioner_role_code_id = ?", prId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			pr.addCode(cc);
		}
	}
	
	
	/**
	 * Handles the telecoms for the practitioner role id passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleTelecoms(Connection connection, VhDirPractitionerRole pr, int prId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from telecom where practitioner_role_id = ?", prId);
		while(resultset.next()) {
				VhDirTelecom tele = ContactFactory.getTelecom(resultset,connection);
				tele.setId(resultset.getString("telecom_id"));
				if (!tele.hasAvailableTime()) {
					// Add 9:00-4:30 any day, available time for this telecom contact point
					tele.addAvailableTime(ContactFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
				}
				pr.addTelecom(tele);
		}
	}
	
	
	/**
	 * Handles all the  Locations  for PractitionerRoles
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleLocations(Connection connection, VhDirPractitionerRole pr, int prId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from resource_reference where practitioner_role_location_id = ?", prId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			pr.addLocation(ref);
		}
	}
	
	/**
	 * Handles all the  Healthcare Services  for PractitionerRoles
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleHealthcareServices(Connection connection, VhDirPractitionerRole pr, int prId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from vhdir_healthcare_service where practitioner_role_id = ?", prId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			pr.addLocation(ref);
		}
	}
	

	/**
	 * Handles all the  new patient specs  for PractitionerRoles
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleNewPatients(Connection connection, VhDirPractitionerRole pr, int prId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from new_patients where practitioner_role_id = ?", prId);
		while(resultset.next()) {
			VhDirNewpatients np = ResourceFactory.getNewPatients(resultset,connection);
			pr.addNewpatients(np);
		}
	}


	/**
	 * Handles all the elements of the identifiers for PractitionerRoles
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirPractitionerRole pr, int prId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from identifier where practitioner_role_id = ?", prId);
		while(resultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(resultset);
			pr.addIdentifier(identifier);
		}
	}
	
	
	/**
	 * Handles all the  Available Times  for PractitionerRoles
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleAvailableTimes(Connection connection, VhDirPractitionerRole pr, int prId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from available_time where practitioner_role_id = ?", prId);
		while(resultset.next()) {
			PractitionerRoleAvailableTimeComponent at = ContactFactory.getPractitionerRoleAvailableTime(resultset);
			pr.addAvailableTime(at);
		}
	}

	/**
	 * Handles all the  Not Available Times  for PractitionerRoles
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleNotAvailableTimes(Connection connection, VhDirPractitionerRole pr, int prId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from not_available_time where practitioner_role_id = ?", prId);
		while(resultset.next()) {
			PractitionerRoleNotAvailableComponent nat = ContactFactory.getPRNotAvailableTime(resultset);
			pr.addNotAvailable(nat);
		}
	}
	
	
	/**
	 * Handle the restrictions associated with the practitioner role 
	 * @param connection
	 * @param prac
	 * @param prId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirPractitionerRole pr, int prId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where practitioner_role_restriction_id = ?", prId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			pr.addUsageRestriction(ref);
		}
	}
	
	/**
	 * Handle the endpoints associated with the practitioner role 
	 * @param connection
	 * @param prac
	 * @param hsId
	 * @throws SQLException
	 */
	private void handleEndpoints(Connection connection, VhDirPractitionerRole pr, int prId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from vhdir_endpoint where practitioner_role_id = ?", prId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("endpoint_id"), "VhDirEndpoint", null, "PractitionerRole Endpoint");
			pr.addEndpoint(ref);
		}
	}
}
