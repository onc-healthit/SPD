package com.esacinc.spd.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.TimeType;
import org.hl7.fhir.r4.model.CareTeam.CareTeamStatus;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HealthcareService.DaysOfWeek;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceNotAvailableComponent;
import org.hl7.fhir.r4.model.Period;

import com.esacinc.spd.model.VhDirContactPoint;
import com.esacinc.spd.model.VhDirContactPointAvailableTime;
import com.esacinc.spd.model.VhDirDigitalCertificate;
import com.esacinc.spd.model.VhDirNetworkContact;

/**
 * This class has static public methods for return various forms of contacts or contactpoints from the database.
 * Examples:  ContactPoint, Network contact, Telecom, Endpoint contact.
 * While all of these have many common elements, they also have some slight differences in them.
 * 
 * @author dandonahue
 *
 */
public class ContactFactory {

	
	public ContactFactory() { }

	/**
	 * Creates a VhDirContactPoint from the data pointed to by the current cursor in the given database query result set.
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
	static public VhDirContactPoint getContactPoint(ResultSet resultset, Connection connection) throws SQLException{
		VhDirContactPoint contact = new VhDirContactPoint();
		try {
			contact.setSystem(ContactPointSystem.fromCode(resultset.getString("system")));
		}
		catch (Exception e) {
			// Probably means status was not found in LocationStatus
			contact.setSystem(ContactPointSystem.NULL);
		}
		contact.setPeriod(ResourceFactory.makePeriod(resultset.getDate("period_start"), resultset.getDate("period_end")));
		contact.setValue(resultset.getString("value"));
		try {
			contact.setUse(ContactPointUse.fromCode(resultset.getString("use")));
		}
		catch (Exception e) {
			// Probably means status was not found in LocationStatus
			contact.setUse(ContactPointUse.NULL);
		}
		contact.setRank(resultset.getInt("rank"));
		
		contact.setViaintermediary(ResourceFactory.getResourceReference(resultset.getInt("contactpoint_intermediary"),connection));
		
		// Leave it up to the calling method to add available time(s)
		
		return contact;
	}

	/**
	 * Creates a VhDirContactPoint from the identifier with the given identifierId in the database
	 * Note:  We create VhDirContactPoint objects from rows in the database telecom table
	 * @param addrId
	 * @param connection
	 * @return VhDirAddress
	 * @throws SQLException
	 */
	static public VhDirContactPoint getTelecomContactPoint(int contactId, Connection connection) throws SQLException{
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from telecom where telecom_id = ?", contactId);
		while(resultset.next()) {
			VhDirContactPoint contactPoint = getContactPoint(resultset, connection);
			contactPoint.setId(resultset.getString("telecom_id"));
			return contactPoint; // Only expecting one
		}	
		return null;  // If we get here, there was no row in the telecom table with that id
	}

	/**
	 * Creates a VhDirNetworkContact resource from the data in the given resultset at the current cursor location.
	 * Connection is needed to retrieve further data from other tables to populate the contact info
	 * @param resultset
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public VhDirNetworkContact getNetworkContact(ResultSet resultset, Connection connection) throws SQLException{
		VhDirNetworkContact con = new VhDirNetworkContact();
		con.setId(resultset.getString("contact_id"));
		con.setPurpose(ResourceFactory.getCodeableConcept(resultset.getInt("purpose_id"),connection));
		con.setName(ResourceFactory.getHumanName(resultset.getInt("name_id"),connection));
		if (connection != null) {
			// Gather the telecoms for this netowrk contact
			ResultSet tcresultset = DatabaseUtil.runQuery(connection, "SELECT * FROM telecom WHERE contact_id = ?", resultset.getInt("name_id"));
			while(tcresultset.next()) {
				VhDirContactPoint tele = getContactPoint(tcresultset, connection);
				tele.setId(tcresultset.getString("telecom_id"));
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
	 * Get a VhDirContactPoint specific to and Endpoint contact
	 * @param resultset
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public VhDirContactPoint getEndpointContact(ResultSet resultset, Connection connection) throws SQLException {
		VhDirContactPoint contact = getContactPoint(resultset, connection);
		contact.setId(resultset.getString("endpoint_contact_id"));
		contact.setViaintermediary(ResourceFactory.getResourceReference(resultset.getInt("via_intermediary"), connection));
		// Add 9:00-4:30 any day, available time for this contact point
		contact.addAvailableTime(makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
		return contact;
	}

	/**
	 * Get a VhDirAvailableTime from a resultsetat current cursor
	 * @param resultset
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public HealthcareServiceAvailableTimeComponent getAvailableTime(ResultSet resultset) throws SQLException {
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
	static public HealthcareServiceNotAvailableComponent getNotAvailableTime(ResultSet resultset) throws SQLException {
		HealthcareServiceNotAvailableComponent nat =new HealthcareServiceNotAvailableComponent();
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
				}
			}
		}
		return at;
	}

	/**
	 * Generate an availableTime object from the given parameters
	 * @param daysString, semicolon delimited string of 3-letter day names, e.g.  "mon;tue;wed;thu;fri"
	 * @param allDay  true if this available time is all day
	 * @param startTime if allday is false, start time, e.g. "08:00:00"
	 * @param endTime if allday is false, start time, e.g. "17:00:00"
	 * @return VhDirContactPointAvailableTime
	 */
	static public VhDirContactPointAvailableTime makeAvailableTime(String daysString, boolean allDay, String startTime, String endTime) {
		// Set some available time - for organizations make it all day 7 days a week
		VhDirContactPointAvailableTime available = new VhDirContactPointAvailableTime();
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
	

}
