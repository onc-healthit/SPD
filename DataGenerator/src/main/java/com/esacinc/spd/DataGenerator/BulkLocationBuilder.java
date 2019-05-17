package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Location.LocationHoursOfOperationComponent;
import org.hl7.fhir.r4.model.Location.LocationPositionComponent;
import org.hl7.fhir.r4.model.Location.LocationStatus;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

import com.esacinc.spd.model.VhDirContactPoint;
import com.esacinc.spd.model.VhDirEhr;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirLocation;
import com.esacinc.spd.model.VhDirNewpatientprofile;
import com.esacinc.spd.model.VhDirNewpatients;
import com.esacinc.spd.util.ContactFactory;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.ResourceFactory;

public class BulkLocationBuilder {
	
	
	/**
	 * uses the connection provided to get all VhDirLocations and then builds a list
	 * of Practitioner objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirLocation> getLocations(Connection connection) throws SQLException, ParseException {
		int cnt = 0;
		List<VhDirLocation> locations = new ArrayList<VhDirLocation>();
        ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_location", null);
		while (resultSet.next() && cnt < BulkDataApp.MAX_ENTRIES) {
			//System.out.println("Creating location for id " + resultSet.getInt("location_id"));
			VhDirLocation loc = new VhDirLocation();
		
			// set the id
			int locId = resultSet.getInt("location_id");
			loc.setId(resultSet.getString("location_id"));
			loc.setName(resultSet.getString("name"));
			loc.addAlias(resultSet.getString("alias"));
			loc.setDescription(resultSet.getString("description"));
			loc.setAddress(ResourceFactory.getAddress(resultSet.getInt("address_id"), connection));
			loc.setPhysicalType(ResourceFactory.getCodeableConcept(resultSet.getInt("physical_type_cc_id"),connection)); 
			loc.setManagingOrganization(ResourceFactory.getResourceReference(resultSet.getInt("managing_organization_id"), connection));
			loc.setPartOf(ResourceFactory.getResourceReference(resultSet.getInt("part_of_location_id"), connection));
			loc.setAvailabilityExceptions(resultSet.getString("availability_exceptions"));	
			loc.addLocation_boundary_geojson(new StringType(resultSet.getString("location_boundary_geojson"))); // TODO we aren't modeling these

			handleStatus(resultSet,loc);

			handlePosition(resultSet, loc);
			
			handleAccessibilities(connection, loc, locId);
			
			handleEhrs(connection, loc, locId);
			
			handleNewPatients(connection, loc, locId);
			
			handleNewPatientProfiles(connection, loc, locId);
			
			// Handle the identifiers
			handleIdentifiers(connection, loc, locId);
			
			handleTypes(connection, loc, locId);
			
           // Handle the telecoms
         	handleTelecoms(connection, loc, locId);
         	
			// Handle the restrictions
         	handleRestrictions(connection, loc, locId);
         	
         	// Handle the hours of operation
         	handleHoursOfOperation(connection, loc, locId);

         	// Handle the end points
         	handleEndpoints(connection, loc, locId);

			locations.add(loc);
			
			cnt++;
		}
		System.out.println("Made " + locations.size() + " locations");
		return locations;
	}

	/**
	 * Handles the location position
	 * 
	 * @param resultSet
	 * @param loc
	 * @throws SQLException
	 */
	private void handlePosition(ResultSet resultSet, VhDirLocation loc) throws SQLException{
		LocationPositionComponent pos = new LocationPositionComponent();
		pos.setLatitude(resultSet.getDouble("latitude"));
		pos.setLongitude(resultSet.getDouble("longitude"));
		pos.setAltitude(resultSet.getDouble("altitude"));
		loc.setPosition(pos);
	}
	
	/**
	 * Handles astatus  for Locations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleStatus(ResultSet resultset, VhDirLocation loc){
		
		try {
			loc.setStatus(LocationStatus.fromCode(resultset.getString("status")));
		}
		catch (Exception e) {
			// Probably means status was not found in LocationStatus
			loc.setStatus(LocationStatus.NULL);
		}

	}

	/**
	 * Handles all the elements of the Accessibilities  for Locations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleAccessibilities(Connection connection, VhDirLocation loc, int locId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where location_accessibility_id = ?", locId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			loc.addAccessibility(cc);
		}
	}

	/**
	 * Handles all the elements of the Ehrs  for Locations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleEhrs(Connection connection, VhDirLocation loc, int locId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from ehr where location_id = ?", locId);
		while(resultset.next()) {
			VhDirEhr ehr = ResourceFactory.getEhr(resultset,connection);
			loc.addEhr(ehr);
		}
	}

	/**
	 * Handles all the elements of the new patient specs  for Locations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleNewPatients(Connection connection, VhDirLocation loc, int locId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from new_patients where location_id = ?", locId);
		while(resultset.next()) {
			VhDirNewpatients np = ResourceFactory.getNewPatients(resultset,connection);
			loc.addNewpatients(np);
		}
	}

	/**
	 * Handles all the elements of the new patient specs  for Locations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleNewPatientProfiles(Connection connection, VhDirLocation loc, int locId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from new_patient_profile where location_id = ?", locId);
		while(resultset.next()) {
			VhDirNewpatientprofile np = ResourceFactory.getNewPatientprofile(resultset);
			loc.addNewpatientprofile(np);
		}
	}

	/**
	 * Handles all the elements of the identifiers for Locations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirLocation loc, int locId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from identifier where location_id = ?", locId);
		while(resultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(resultset);
			loc.addIdentifier(identifier);
		}
	}
	
	/**
	 * Handles all the elements of the Types  for Locations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleTypes(Connection connection, VhDirLocation loc, int locId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where location_type_id = ?", locId);
		while(resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			loc.addAccessibility(cc);
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
	private void handleTelecoms(Connection connection, VhDirLocation loc, int locId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from telecom where location_id = ?", locId);
		while(resultset.next()) {
				VhDirContactPoint tele = ContactFactory.getContactPoint(resultset,connection);
				tele.setId(resultset.getString("telecom_id"));
				// Add 9:00-4:30 any day, available time for this telecom contact point
				tele.addAvailableTime(ContactFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
				loc.addTelecom(tele);
		}
	}
	
	
	
	/**
	 * Handle the restrictions associated with the practitioner 
	 * @param connection
	 * @param prac
	 * @param locId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirLocation loc, int locId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where location_restriction_id = ?", locId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset.getInt("resource_reference_id"),connection);
			loc.addUsageRestriction(ref);
		}
	}
	
	/**
	 * Handle the restrictions associated with the practitioner 
	 * @param connection
	 * @param prac
	 * @param locId
	 * @throws SQLException
	 */
	private void handleHoursOfOperation(Connection connection, VhDirLocation loc, int locId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from available_time where location_hours_of_operation_id = ?", locId);
		while(resultset.next()) {
			LocationHoursOfOperationComponent hrs = ResourceFactory.getHoursOfOperation(resultset);
			loc.addHoursOfOperation(hrs);
		}
	}

	/**
	 * Handle the restrictions associated with the practitioner 
	 * @param connection
	 * @param prac
	 * @param locId
	 * @throws SQLException
	 */
	private void handleEndpoints(Connection connection, VhDirLocation loc, int locId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from vhdir_endpoint where location_id = ?", locId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("endpoint_id"), "VhDirEndpoint", null, "Location Endpoint");
			loc.addEndpoint(ref);
		}
	}

}
