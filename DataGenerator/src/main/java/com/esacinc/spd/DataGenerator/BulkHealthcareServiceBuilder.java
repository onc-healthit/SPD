package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceEligibilityComponent;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceNotAvailableComponent;
import org.hl7.fhir.r4.model.Reference;


import com.esacinc.spd.model.VhDirTelecom;
import com.esacinc.spd.model.VhDirHealthcareService;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirNewpatients;
import com.esacinc.spd.util.ContactFactory;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.ResourceFactory;

public class BulkHealthcareServiceBuilder {
	
	
	/**
	 * uses the connection provided to get all VhDirHealthcareServices and then builds a list
	 * of Practitioner objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirHealthcareService> getHealthcareServices(Connection connection) throws SQLException, ParseException {
		int cnt = 0;
		List<VhDirHealthcareService> healthcareservices = new ArrayList<VhDirHealthcareService>();
        ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_healthcare_service", null);
		while (resultSet.next() && cnt < BulkDataApp.MAX_ENTRIES) {
			//System.out.println("Creating healthcareservice for id " + resultSet.getInt("healthcare_service_id"));
			VhDirHealthcareService hs = new VhDirHealthcareService();
		
			// set the id
			int hsId = resultSet.getInt("healthcare_service_id");
			hs.setId(resultSet.getString("healthcare_service_id"));
			hs.setActive(resultSet.getBoolean("active"));
			hs.setProvidedBy(ResourceFactory.getResourceReference(resultSet.getInt("provided_by_organization_id"), connection));
			hs.setName(resultSet.getString("name"));
			hs.setComment(resultSet.getString("comment"));
			hs.setExtraDetails(resultSet.getString("extra_details")); 
			//hs.setPhoto(new Attachment(resultSet.getString("photo")));
			hs.setAppointmentRequired(resultSet.getBoolean("active"));
			hs.setAvailabilityExceptions(resultSet.getString("availability_exceptions"));	

			// Handle the restrictions
         	handleRestrictions(connection, hs, hsId);

         	handleNewPatients(connection, hs, hsId);

         	handleIdentifiers(connection, hs, hsId);
         	
			handleCategories(connection, hs, hsId);

			handleTypes(connection, hs, hsId);
			
			handleSpecialties(connection, hs, hsId);
			
			handleLocations(connection, hs, hsId);

         	handleTelecoms(connection, hs, hsId);

			
			handleCoverageAreas(connection, hs, hsId);
			
			handleServiceCodes(connection, hs, hsId);
			
			handleEligibilities(connection, hs, hsId);

			handlePrograms(connection, hs, hsId);

			handleCharacteristics(connection, hs, hsId);

			handleCommunications(connection, hs, hsId);

			handleReferralMethods(connection, hs, hsId);
			
			handleAvailableTimes(connection, hs, hsId);
			
			handleNotAvailableTimes(connection, hs, hsId);

         	// Handle the end points
         	handleEndpoints(connection, hs, hsId);

			healthcareservices.add(hs);
			
			cnt++;
		}
		System.out.println("Made " + healthcareservices.size() + " healthcareservices");
		return healthcareservices;
	}

	

	/**
	 * Handles all the  Categories  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleCategories(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where healthcare_service_category_id = ?", hsId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			hs.addCategory(cc);
		}
	}


	/**
	 * Handles all the  types  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleTypes(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where healthcare_service_type_id = ?", hsId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			hs.addType(cc);
		}
	}

	/**
	 * Handles all the  Specialties  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleSpecialties(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where healthcare_service_specialty_id = ?", hsId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			hs.addCategory(cc);
		}
	}

	/**
	 * Handles all the  Locations  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleLocations(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from resource_reference where healthcare_service_location_id = ?", hsId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			hs.addLocation(ref);
		}
	}

	/**
	 * Handles all the  Coverage Areas  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleCoverageAreas(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from resource_reference where healthcare_service_coverageArea_id = ?", hsId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			hs.addCoverageArea(ref);
		}
	}

	/**
	 * Handles all the  Service Provision Codes  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleServiceCodes(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where healthcare_service_service_provision_code_id = ?", hsId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			hs.addServiceProvisionCode(cc);
		}
	}

	/**
	 * Handles all the  Eligibilities  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleEligibilities(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from eligibility where healthcare_service_id = ?", hsId);
		while(resultset.next()) {
			HealthcareServiceEligibilityComponent ee = ResourceFactory.getEligibility(resultset, connection);
			hs.addEligibility(ee);
		}
	}

	/**
	 * Handles all the  Programs  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handlePrograms(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where healthcare_service_program_id = ?", hsId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			hs.addProgram(cc);
		}
	}

	/**
	 * Handles all the  Characteristics  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleCharacteristics(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where healthcare_service_characteristic_id = ?", hsId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			hs.addCharacteristic(cc);
		}
	}

	/**
	 * Handles all the  Communications  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleCommunications(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where healthcare_service_communication_id = ?", hsId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			hs.addCommunication(cc);
		}
	}

	/**
	 * Handles all the  Referral Methods  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleReferralMethods(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where healthcare_service_referral_method_id = ?", hsId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			hs.addReferralMethod(cc);
		}
	}

	
	/**
	 * Handles all the  Available Times  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleAvailableTimes(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from available_time where healthcare_service_id = ?", hsId);
		while(resultset.next()) {
			HealthcareServiceAvailableTimeComponent at = ContactFactory.getHealthCareServiceAvailableTime(resultset);
			hs.addAvailableTime(at);
		}
	}

	/**
	 * Handles all the  Not Available Times  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleNotAvailableTimes(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from not_available_time where healthcare_service_id = ?", hsId);
		while(resultset.next()) {
			HealthcareServiceNotAvailableComponent nat = ContactFactory.getNotAvailableTime(resultset);
			hs.addNotAvailable(nat);
		}
	}

	/**
	 * Handles all the  new patient specs  for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleNewPatients(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from new_patients where healthcare_service_id = ?", hsId);
		while(resultset.next()) {
			VhDirNewpatients np = ResourceFactory.getNewPatients(resultset,connection);
			hs.addNewpatients(np);
		}
	}


	/**
	 * Handles all the elements of the identifiers for HealthcareServices
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from identifier where healthcare_service_id = ?", hsId);
		while(resultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(resultset);
			hs.addIdentifier(identifier);
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
	private void handleTelecoms(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from telecom where healthcare_service_id = ?", hsId);
		while(resultset.next()) {
				VhDirTelecom tele = ContactFactory.getTelecom(resultset,connection);
				tele.setId(resultset.getString("telecom_id"));
				if (!tele.hasAvailableTime()) {
					// Add 9:00-4:30 any day, available time for this telecom contact point
					tele.addAvailableTime(ContactFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
				}
				hs.addTelecom(tele);
		}
	}
	
	
	
	/**
	 * Handle the restrictions associated with the healthcare service 
	 * @param connection
	 * @param prac
	 * @param hsId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where healthcare_service_restriction_id = ?", hsId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			hs.addUsageRestriction(ref);
		}
	}
	
	/**
	 * Handle the endpoints associated with the healthcare service 
	 * @param connection
	 * @param prac
	 * @param hsId
	 * @throws SQLException
	 */
	private void handleEndpoints(Connection connection, VhDirHealthcareService hs, int hsId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from vhdir_endpoint where healthcare_service_id = ?", hsId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("endpoint_id"), "VhDirEndpoint", null, "HealthcareService Endpoint");
			hs.addEndpoint(ref);
		}
	}

}
