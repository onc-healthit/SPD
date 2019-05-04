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
		List<VhDirLocation> locations = new ArrayList<VhDirLocation>();
		
		String sql = "SELECT * FROM vhdir_location";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			//System.out.println("Creating location for id " + resultSet.getInt("location_id"));
			VhDirLocation loc = new VhDirLocation();
		
			// set the id
			int locId = resultSet.getInt("location_id");
			loc.setId(resultSet.getString("location_id"));
			 
			handleStatus(resultSet,loc);
			loc.setName(resultSet.getString("name"));
			loc.addAlias(resultSet.getString("alias"));
			loc.setDescription(resultSet.getString("description"));
			loc.setAddress(ResourceFactory.getAddress(resultSet.getInt("address_id"), connection));
			loc.setPhysicalType(ResourceFactory.getCodeableConcept(resultSet.getInt("physcial_type_cc_id"),connection)); // TOTO fix physcial in db
			
			LocationPositionComponent pos = new LocationPositionComponent();
			pos.setLatitude(resultSet.getDouble("latitude"));
			pos.setLongitude(resultSet.getDouble("longitude"));
			pos.setAltitude(resultSet.getDouble("altitude"));
			loc.setPosition(pos);
			
			loc.setManagingOrganization(ResourceFactory.getResourceReference(resultSet.getInt("managing_organization_id"), connection));
			loc.setPartOf(ResourceFactory.getResourceReference(resultSet.getInt("part_of_location_id"), connection));
			loc.setAvailabilityExceptions(resultSet.getString("availability_exceptions"));
			
			loc.addLocation_boundary_geojson(new StringType(resultSet.getString("location_boundary_geojson"))); // TODO we aren't modeling these
			
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
		}
		System.out.println("Made " + locations.size() + " locations");
		return locations;
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
		String strSql = "SELECT * from fhir_codeable_concept where location_accessibility_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, locId);
		ResultSet resultset = sqlStatement.executeQuery();
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
		String strSql = "SELECT * from ehr where location_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, locId);
		ResultSet resultset = sqlStatement.executeQuery();
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
		String strSql = "SELECT * from new_patients where location_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, locId);
		ResultSet resultset = sqlStatement.executeQuery();
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
		String strSql = "SELECT * from new_patient_profile where location_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, locId);
		ResultSet resultset = sqlStatement.executeQuery();
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
		String idSql = "SELECT * from identifier where location_id = ?";
		PreparedStatement idStatement = connection.prepareStatement(idSql);
		idStatement.setInt(1, locId);
		ResultSet idResultset = idStatement.executeQuery();
		while(idResultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(idResultset);
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
		String strSql = "SELECT * from fhir_codeable_concept where location_type_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, locId);
		ResultSet resultset = sqlStatement.executeQuery();
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
		String addrSql = "SELECT * from telecom where location_id = ?";
		PreparedStatement telecomStatement = connection.prepareStatement(addrSql);
		telecomStatement.setInt(1, locId);
		ResultSet telecomResultset = telecomStatement.executeQuery();
		while(telecomResultset.next()) {
				VhDirContactPoint tele = ResourceFactory.getContactPoint(telecomResultset);
				// Add 9:00-4:30 any day, available time for this telecom contact point
				tele.addAvailableTime(ResourceFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
				loc.addTelecom(tele);
		}
	}
	
	
	
	//TODO following is not complete at all!
	/**
	 * Handle the restrictions associated with the practitioner 
	 * @param connection
	 * @param prac
	 * @param locId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirLocation loc, int locId) throws SQLException {
		String resSql = "SELECT * from vhdir_restriction where location_id = ?"; //TODO this might need to use the resource_reference table. Is it modeled?
		PreparedStatement resStatement = connection.prepareStatement(resSql);
		resStatement.setInt(1, locId);
		ResultSet restrictions = resStatement.executeQuery();
		while(restrictions.next()) {
			Reference ref = ResourceFactory.getRestrictionReference(restrictions);
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
		String strSql = "SELECT * from available_time where location_hours_of_operation_id = ?"; 
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, locId);
		ResultSet resultset = sqlStatement.executeQuery();
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
		String strSql = "SELECT * from vhdir_endpoint where location_id = ?"; 
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, locId);
		ResultSet resultset = sqlStatement.executeQuery();
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("endpoint_id"), "VhDirEndpoint", null, "Location Endpoint");
			loc.addEndpoint(ref);
		}
	}

}
