package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.TimeType;

import com.esacinc.spd.model.VhDirAddress;
import com.esacinc.spd.model.VhDirAlias;
import com.esacinc.spd.model.VhDirContactPoint;
import com.esacinc.spd.model.VhDirContactPointAvailableTime;
import com.esacinc.spd.model.VhDirDigitalCertificate;
import com.esacinc.spd.model.VhDirEndpoint;
import com.esacinc.spd.model.VhDirGeoLocation;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirOrganization_MH;
import com.esacinc.spd.model.VhDirIdentifier.IdentifierStatus;
import com.esacinc.spd.model.VhDirPractitioner;
import com.esacinc.spd.model.VhDirQualification;
import com.esacinc.spd.util.ContactFactory;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.DigitalCertificateFactory;
import com.esacinc.spd.util.Geocoding;
import com.esacinc.spd.util.ResourceFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BulkPractitionerBuilder {
	
	
	/**
	 * uses the connection provided to get all practitioner and then builds a list
	 * of Practitioner objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirPractitioner> getPractitioners(Connection connection) throws SQLException, ParseException {
		List<VhDirPractitioner> practitioners = new ArrayList<VhDirPractitioner>();
		int cnt = 0;
		int certCount = 0;
        ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_practitioner", null);
		while (resultSet.next() && cnt < BulkDataApp.MAX_ENTRIES) {
			VhDirPractitioner prac = new VhDirPractitioner();
		
			// set the id
			int pracId = resultSet.getInt("practitioner_id");
			prac.setId(resultSet.getString("practitioner_id"));
			 
			prac.setActive(resultSet.getBoolean("active"));
			
			// Add a digital certificate to the first 3 organizations
			if (certCount < DigitalCertificateFactory.MAX_CERTS) {
				// args are:  nthCert, type, use, trustFramework, standard, expirationDate
				prac.addDigitalcertficate(DigitalCertificateFactory.makeDigitalCertificate(certCount++, "role", "auth", "other", "x.509v3", null));
			}
						
			// Handle the identifiers
			handleIdentifiers(connection, prac, pracId);
			
			// Handle the qualifications
			handleQualifications(connection, prac, pracId);
			
			// Handle the gender
			handleGender(prac, resultSet.getString("gender"));
			
			// Handle the birth date
			handleBirthDate(prac, resultSet.getDate("birthDate"));
			
            // Handle names
         	handleNames(connection, prac, pracId);
					
			
            // Handle the telecoms
         	handleTelecoms(connection, prac, pracId);
         	
			// Handle the addresses
         	handleAddresses(connection, prac, pracId);
         	
            // Handle the restrictions
         	handleRestrictions(connection, prac, pracId);
         	
         	// Handle the communications
         	handleCommunications(connection, prac, pracId);
			
			practitioners.add(prac);
			
			cnt++;
		}
		System.out.println("Made " + practitioners.size() + " practitioners");
		return practitioners;
	}

	/**
	 * Takes a string representing gender and sets the practitioner gender to a corresponding AdministrativeGender value.
	 * 
	 * @param prac
	 * @param gender
	 */
	private void handleGender(VhDirPractitioner prac, String gender) {
		// First of all, if there's nothing in the db for gender, then we'll say UNKNOWN
		if (gender == null || gender.isEmpty()) {
			prac.setGender(AdministrativeGender.UNKNOWN);
		}
		else {
			// Otherwise, let's try to handle the db value in the normal way...
			try {
				prac.setGender(AdministrativeGender.valueOf(gender));
			}
			catch (IllegalArgumentException e){
				// If we get an error, then it may just be that the db has "f" or "m" as the gender. At least we can handle that case...
				if ("F".equalsIgnoreCase(gender)) {
					prac.setGender(AdministrativeGender.FEMALE);
				}
				else if ("M".equalsIgnoreCase(gender)) {
					prac.setGender(AdministrativeGender.MALE);
				} 
				else {
					// Who knows what they've put as gender in the db. Let's just say OTHER.
					prac.setGender(AdministrativeGender.OTHER);
				}
			}
		}
	}
		
	/**
	 * Takes a Date representing birthDate and sets the practitioner birth date
	 * 
	 * @param prac
	 * @param gender
	 */
	private void handleBirthDate(VhDirPractitioner prac, Date birthdate) {
		if (birthdate != null) {
			prac.setBirthDate(birthdate);
		}
	}

	/**
	 * Handles all the elements of the identifiers for Practitioners
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from identifier where practitioner_id = ?", pracId);
		while(resultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(resultset);
			prac.addIdentifier(identifier);
		}
	}

	/**
	 * Handles all the elements of the qualifications for Practitioners
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleQualifications(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from qualification where practitioner_id = ?", pracId);
		while(resultset.next()) {
			PractitionerQualificationComponent qu = ResourceFactory.getQualification(resultset, connection);
			prac.addQualification(qu);
		}
	}

	/**
	 * Handles the addresses for the passed in practitioner ID
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleAddresses(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from address where practitioner_id = ?", pracId);
		while(resultset.next()) {
			VhDirAddress addr = ResourceFactory.getAddress(resultset, connection);
			prac.addAddress(addr);
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
	private void handleTelecoms(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from telecom where practitioner_id = ?", pracId);
		while(resultset.next()) {
				VhDirContactPoint tele = ContactFactory.getContactPoint(resultset,connection);
				tele.setId(resultset.getString("telecom_id"));
				// Add 9:00-4:30 any day, available time for this telecom contact point
				tele.addAvailableTime(ContactFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
				prac.addTelecom(tele);
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
	private void handleNames(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from name where practitioner_id = ?", pracId);
		while(resultset.next()) {
			HumanName name = ResourceFactory.getHumanName(resultset);
			prac.addName(name);
		}
	}
	
	/**
	 * Handle the restrictions associated with the practitioner 
	 * @param connection
	 * @param prac
	 * @param pracId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where practitioner_restriction_id = ?", pracId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset.getInt("resource_reference_id"),connection);
			prac.addUsageRestriction(ref);
		}
	}
	
	/**
	 * Handle the communication proficiencies associated with the practitioner
	 * @param connection
	 * @param prac
	 * @param pracId
	 * @throws SQLException
	 */
	private void handleCommunications(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
		// A communication is a codeable concept. Such codeable concepts can have multiple codings in it.
		// First, query the db for all the communications for this practioner
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from communication where practitioner_id = ?", pracId);
		// Then, for each communication in the result set above, go and get all the codings for that communication...
		int cnt = 0;
		while (resultset.next())   
		{	
			cnt++;
			CodeableConcept comm_cc = ResourceFactory.getCommunicationProficiency(resultset.getString("communication_id"), connection); // To hold all the codings
			prac.addCommunication(comm_cc);
		}
		// If we didn't find any communications in the db, let's just make one up for now
		if (cnt == 0) {
			prac.addCommunication(ResourceFactory.makeCommunicationProficiencyCodes());
		}
	}


}
