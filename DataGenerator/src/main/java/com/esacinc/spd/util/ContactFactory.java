package com.esacinc.spd.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.r4.model.HealthcareService.DaysOfWeek;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceNotAvailableComponent;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleAvailableTimeComponent;
import org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleNotAvailableComponent;

import org.hl7.fhir.r4.model.Organization.OrganizationContactComponent;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.TimeType;

import com.esacinc.spd.model.VhDirAvailableTime;
import com.esacinc.spd.model.VhDirContact;
import com.esacinc.spd.model.VhDirTelecom;

/**
 * This class has static public methods for contacts and telecoms (extended from HAPI contactPoints) from the database.
 * Contacts have a list of telecoms
 * Telecoms can have a list of available times
 *           
 * In the VhDir IG,  endpoints have a list of "contacts", but they are really telecoms.
 * 
 * The VhDir IG profiles almost all have either a list of contacts (which in turn have a list of telecoms), 
 * or just a list of telecoms:
 * 
 *     VhDir Care Team - telecoms
 *     VhDir Endpoint  - telecoms (but they are called contacts, for some reason)
 *     VhDir Healthcare Service - telecoms
 *     VhDir Insurance Plan - contacts
 *     VhDir Location - telecoms
 *     VhDir Network - telecoms
 *     VhDir Organization - telecoms
 *     VhDir Organization Affiliation - telecoms
 *     VhDir Practitioner - telecoms
 *     VhDir Practitioner Role - telecoms 
 *     VhDir Restriction - n/a
 *     VhDir Validation - n/a
 *     
 * @author dandonahue
 *
 */
public class ContactFactory {

	
	public ContactFactory() { }

	//-------------------------------------------------------------------------------------------------------------
	// TELECOMS
	//-------------------------------------------------------------------------------------------------------------

	/**
	 * Creates a VhDirTelecom from the data pointed to by the current cursor in the given database query result set.
	 * This assumes that the ResultSet argument is the result from one of the two sql statements 
	 *        "Select * from endpoint_contact ...."
	 *        "Select * from telecom ...."
	 * (Since both these tables have the same base set of fields)
	 * 
	 * This method does not set the id of the returned contact point object. That must be done in the calling method
	 * as we could be dealing with a telecom table or an endpoint contact table row.
	 * 
	 * @param resultset
	 * @return VhDirContactPoint
	 * @throws SQLException
	 */
	static public VhDirTelecom getTelecom(ResultSet resultset, Connection connection) throws SQLException{
		VhDirTelecom telecom = new VhDirTelecom();
		// id - string
		telecom.setId(resultset.getString("telecom_id"));
		
		// System - code
		try {
			telecom.setSystem(ContactPointSystem.fromCode(resultset.getString("system")));
		}
		catch (Exception e) {
			// Probably means status was not found in LocationStatus
			telecom.setSystem(ContactPointSystem.NULL);
			ErrorReport.writeWarning("VhDirTelecom", telecom.getId(), "unrecognized system ", e.getMessage());

		}

		// Value - string
		telecom.setValue(resultset.getString("value"));

		// Use - code 
		try {
			telecom.setUse(ContactPointUse.fromCode(resultset.getString("use")));
		}
		catch (Exception e) {
			// Probably means status was not found in LocationStatus
			telecom.setUse(ContactPointUse.NULL);
			ErrorReport.writeWarning("VhDirTelecom", telecom.getId(), "unrecognized use ", e.getMessage());

		}

		// Rank - int
		telecom.setRank(resultset.getInt("rank"));

		// Period - period
		telecom.setPeriod(ResourceFactory.makePeriod(resultset.getDate("period_start"), resultset.getDate("period_end")));
		
		// viaIntermediary - resource reference (extension)
		telecom.setViaintermediary(ResourceFactory.getResourceReference(resultset.getInt("contactpoint_intermediary"),connection));
		
		// Available times -  list of VhDirAvailableTime (extension)
		if (connection != null) {
			ResultSet timesResultset = DatabaseUtil.runQuery(connection,"select * from available_time where telecom_id = ?", resultset.getInt("telecom_id"));
			while (timesResultset.next()) {
				VhDirAvailableTime available = makeAvailableTime(timesResultset.getString("days_of_week"),
						timesResultset.getBoolean("all_day"),
						timesResultset.getString("available_start_time"),
						timesResultset.getString("available_end_time"));
				available.setId(timesResultset.getString("available_time_id"));
				telecom.addAvailableTime(available);
			}
		}
		return telecom;
	}
	

	/**
	 * Creates a VhDirTelecom from the identifier with the given identifierId in the database
	 * Note:  We create VhDirTelecom objects from rows in the database telecom table
	 * @param addrId
	 * @param connection
	 * @return VhDirTelecom
	 * @throws SQLException
	 */
	static public VhDirTelecom getTelecom(int telecomId, Connection connection) throws SQLException{
		if (telecomId == 0) return null;  // We know for sure there's no 0 id.
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from telecom where telecom_id = ?", telecomId);
		while(resultset.next()) {
			VhDirTelecom telecom = getTelecom(resultset, connection);
			return telecom; // Only expecting one
		}	
		ErrorReport.writeError("VhDirTelecom", resultset.getString("telecomId"), "ContactFactory.getTelecom", "No telecom found with given id: "+telecomId);

		return null;  // If we get here, there was no row in the telecom table with that id
	}

	//-------------------------------------------------------------------------------------------------------------
    // CONTACTS
	//-------------------------------------------------------------------------------------------------------------
	
	/**
	 * Creates a VhDirContact resource from the data in the given resultset at the current cursor location.
	 * Connection is needed to retrieve further data from other tables to populate the contact info
	 * @param resultset
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public VhDirContact getContact(ResultSet resultset, Connection connection) throws SQLException{
		VhDirContact con = new VhDirContact();
		con.setId(resultset.getString("contact_id"));
		con.setPurpose(ResourceFactory.getCodeableConcept(resultset.getInt("purpose_id"),connection));
		con.setName(ResourceFactory.getHumanName(resultset.getInt("name_id"),connection));
		if (connection != null) {
			// Gather the telecoms for this netowrk contact
			ResultSet tcresultset = DatabaseUtil.runQuery(connection, "SELECT * FROM telecom WHERE contact_id = ?", resultset.getInt("name_id"));
			while(tcresultset.next()) {
				VhDirTelecom tele = getTelecom(tcresultset, connection);
				if (!tele.hasAvailableTime() )
					// Add 9:00-4:30 any day, available time for this telecom contact point
					tele.addAvailableTime(makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
				    con.addTelecom(tele);
			    }
			    // Get the address for this contact
			    con.setAddress(ResourceFactory.getAddress(tcresultset.getInt("address_id"),connection));
		}
		return con;
	}

	/**
	 * Organization profile uses OrganizationContactComponent as a contact component.
	 * @param resultset
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public OrganizationContactComponent getOrganizationContact(ResultSet resultset, Connection connection) throws SQLException {
		OrganizationContactComponent occ = new OrganizationContactComponent();
		// Set id
		int orgContactId = resultset.getInt("contact_id");
		occ.setId(resultset.getString("contact_id"));
		
		// Set name
		occ.setName(ResourceFactory.getHumanName(resultset));
		
		// Handle telecoms for contacts
	    ResultSet teleResultset = DatabaseUtil.runQuery(connection, "SELECT * FROM telecom WHERE contact_id = ?", orgContactId);
		while(teleResultset.next()) {
			VhDirTelecom tele = getTelecom(teleResultset,connection); 
			if (!tele.hasAvailableTime()) {
				// Add weekday, normal business hours availablility for this contact
				tele.addAvailableTime(ContactFactory.makeAvailableTime("mon;tue;wed;thu;fri", false, "08:00:00", "17:00:00"));
			}
			occ.addTelecom(tele);
		}
		
		return occ;
	}

	//-------------------------------------------------------------------------------------------------------------
    // AVAILABLE TIMES  &  NOT AVAILABLE TIMES
	//-------------------------------------------------------------------------------------------------------------

	static public VhDirAvailableTime getAvailableTime(ResultSet resultset) throws SQLException {
		VhDirAvailableTime available = makeAvailableTime(resultset.getString("days_of_week"),
				resultset.getBoolean("all_day"),
				resultset.getString("available_start_time"),
				resultset.getString("available_end_time"));
		available.setId(resultset.getString("available_time_id"));
		return available;
	}

	/**
	 * Generate an availableTime object from the given parameters
	 * @param daysString, semicolon delimited string of 3-letter day names, e.g.  "mon;tue;wed;thu;fri"
	 * @param allDay  true if this available time is all day
	 * @param startTime if allday is false, start time, e.g. "08:00:00"
	 * @param endTime if allday is false, start time, e.g. "17:00:00"
	 * @return VhDirContactPointAvailableTime
	 */
	static public VhDirAvailableTime makeAvailableTime(String daysString, boolean allDay, String startTime, String endTime) {
		// Set some available time - for organizations make it all day 7 days a week
		VhDirAvailableTime available = new VhDirAvailableTime();
		CodeType dayCode = null;
		String[] days = daysString.split(";");
		for (String d : days) {
			dayCode = new CodeType();
			dayCode.setSystem("http://hl7.org/fhir/days-of-week");
			dayCode.setValue(d);
			available.addDaysOfWeek(dayCode);
		}
		available.setAllDay(allDay);
		if (!allDay) {
			TimeType start = new TimeType();
			start.setValue("08:00:00");
			available.setAvailableStartTime(start);
			TimeType end = new TimeType();
			end.setValue("17:00:00");
			available.setAvailableEndTime(end);
		}
		return available;	
	}

	/**
	 * Get a VhDirAvailableTime from a resultsetat current cursor
	 * @param resultset
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public HealthcareServiceAvailableTimeComponent getHealthCareServiceAvailableTime(ResultSet resultset) throws SQLException {
		HealthcareServiceAvailableTimeComponent at = _makeAvailableTime(resultset.getString("days_of_week"),
				                                                        resultset.getBoolean("all_day"),
				                                                        resultset.getString("available_start_time"),
				                                                        resultset.getString("available_end_time"));
		// makeAvailableTime only sets the data elements, not the id.
		at.setId(resultset.getString("available_time_id"));
		return at;
	}
	
	/**
	 * Get a VhDirAvailableTime from a resultsetat current cursor
	 * @param resultset
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public PractitionerRoleAvailableTimeComponent getPractitionerRoleAvailableTime(ResultSet resultset) throws SQLException {
		PractitionerRoleAvailableTimeComponent at = _makePRAvailableTime(resultset.getString("days_of_week"),
				                                                        resultset.getBoolean("all_day"),
				                                                        resultset.getString("available_start_time"),
				                                                        resultset.getString("available_end_time"));
		// makeAvailableTime only sets the data elements, not the id.
		at.setId(resultset.getString("available_time_id"));
		return at;
	}

	/**
	 * Get a VhDirAvailableTime from a resultsetat current cursor
	 * @param resultset
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public HealthcareServiceNotAvailableComponent getNotAvailableTime(ResultSet resultset) throws SQLException {
		HealthcareServiceNotAvailableComponent nat =new HealthcareServiceNotAvailableComponent();
		nat.setId(resultset.getString("unavailable_time_id"));
		nat.setDescription(resultset.getString("description"));
		Period per = ResourceFactory.makePeriod(resultset.getDate("during_start_time"), resultset.getDate("during_end_time"));
		nat.setDuring(per);
		return nat;
	}
	
	/**
	 * Get a VhDirAvailableTime from a resultsetat current cursor
	 * @param resultset
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public PractitionerRoleNotAvailableComponent getPRNotAvailableTime(ResultSet resultset) throws SQLException {
		PractitionerRoleNotAvailableComponent nat =new PractitionerRoleNotAvailableComponent();
		nat.setId(resultset.getString("unavailable_time_id"));
		nat.setDescription(resultset.getString("description"));
		Period per = ResourceFactory.makePeriod(resultset.getDate("during_start_time"), resultset.getDate("during_end_time"));
		nat.setDuring(per);
		return nat;
	}


	/**
	 * Generate an availableTime object from the given parameters
	 * @param daysString, semicolon delimited string of 3-letter day names, e.g.  "mon;tue;wed;thu;fri"
	 * @param allDay  true if this available time is all day
	 * @param startTime if allday is false, start time, e.g. "08:00:00"
	 * @param endTime if allday is false, start time, e.g. "17:00:00"
	 * @return VhDirContactPointAvailableTime
	 */
	static public HealthcareServiceAvailableTimeComponent _makeAvailableTime(String daysString, boolean allDay, String startTime, String endTime) {
		HealthcareServiceAvailableTimeComponent at =new HealthcareServiceAvailableTimeComponent();
		at.setAllDay(allDay);
		if (!at.getAllDay()) {
			at.setAvailableStartTime(startTime);
			at.setAvailableEndTime(endTime);
		}
		if (daysString != null && !daysString.isEmpty()) {
			String[] days = daysString.split(";");
			for (String d : days) {
				try {
					at.addDaysOfWeek(DaysOfWeek.fromCode(d));
				}
				catch (Exception e){
					// Bad day value. Don't do anything.
					System.err.println("Bad Day of Week value: " + d + " in getAvailableTime");
					System.err.println("   " + e.getMessage());
					ErrorReport.writeWarning("AvailableTime", "" , "Bad day of week value in: " + daysString, e.getMessage());

				}
			}
		}
		return at;
	}
	
	static public PractitionerRoleAvailableTimeComponent _makePRAvailableTime(String daysString, boolean allDay, String startTime, String endTime) {
		PractitionerRoleAvailableTimeComponent at =new PractitionerRoleAvailableTimeComponent();
		at.setAllDay(allDay);
		if (!at.getAllDay()) {
			at.setAvailableStartTime(startTime);
			at.setAvailableEndTime(endTime);
		}
		if (daysString != null && !daysString.isEmpty()) {
			String[] days = daysString.split(";");
			for (String d : days) {
				try {
					at.addDaysOfWeek(PractitionerRole.DaysOfWeek.fromCode(d));
				}
				catch (Exception e){
					// Bad day value. Don't do anything.
					System.err.println("Bad Day of Week value: " + d + " in getAvailableTime");
					System.err.println("   " + e.getMessage());
					ErrorReport.writeWarning("AvailableTime", "" , "Bad day of week value in: " + daysString, e.getMessage());

				}
			}
		}
		return at;
	}

	

}
