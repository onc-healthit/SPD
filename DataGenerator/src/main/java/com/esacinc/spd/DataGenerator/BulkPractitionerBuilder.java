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
import com.esacinc.spd.model.VhDirOrganization;
import com.esacinc.spd.model.VhDirIdentifier.IdentifierStatus;
import com.esacinc.spd.model.VhDirPractitioner;
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
		
		int certCount = 0;
		String sql = "SELECT * FROM vhdir_practitioner";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			VhDirPractitioner prac = new VhDirPractitioner();
		
			// set the id
			int pracId = resultSet.getInt("practitioner_id");
			prac.setId(resultSet.getString("practitioner_id"));
			 
			prac.setActive(resultSet.getBoolean("active"));
			
			// Add a digital certificate to the first 3 organizations
			if (certCount < 3) {
				VhDirDigitalCertificate cert = new VhDirDigitalCertificate();
				cert.setType(ResourceFactory.makeCoding("role",  "role", "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate", false));
				cert.setUse(ResourceFactory.makeCoding("auth",  "auth", "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate", false));
				cert.setCertificateStandard(ResourceFactory.makeCoding("x.509v3",  "x.509v3", "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate", false));
				cert.setCertificate(DigitalCertificateFactory.getNthCert(certCount++));
				Date expire = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(expire);
				cal.add(Calendar.YEAR, 1);
				cert.setExpirationDate(cal.getTime());
				CodeableConcept certTrust = new CodeableConcept();
				certTrust.addCoding(ResourceFactory.makeCoding("other",  "other", "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-digitalcertificate", false));
				cert.setTrustFramework(certTrust);
				prac.addDigitalcertficate(cert);
			}
						
			// Handle the identifiers
			handleIdentifiers(connection, prac, pracId);
			
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
		}
		
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
		String idSql = "SELECT * from identifier where practitioner_id = ?";
		PreparedStatement idStatement = connection.prepareStatement(idSql);
		idStatement.setInt(1, pracId);
		ResultSet idResultset = idStatement.executeQuery();
		while(idResultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(idResultset);
			prac.addIdentifier(identifier);
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
		String addrSql = "SELECT * from address where practitioner_id = ?";
		PreparedStatement addrStatement = connection.prepareStatement(addrSql);
		addrStatement.setInt(1, pracId);
		ResultSet addrResultset = addrStatement.executeQuery();
		while(addrResultset.next()) {
			VhDirAddress addr = ResourceFactory.getAddress(addrResultset, connection);
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
		String addrSql = "SELECT * from telecom where practitioner_id = ?";
		PreparedStatement telecomStatement = connection.prepareStatement(addrSql);
		telecomStatement.setInt(1, pracId);
		ResultSet telecomResultset = telecomStatement.executeQuery();
		while(telecomResultset.next()) {
				VhDirContactPoint tele = ResourceFactory.getContactPoint(telecomResultset);
				// Add 9:00-4:30 any day, available time for this telecom contact point
				tele.addAvailableTime(ResourceFactory.makeAvailableTime("sun,mon,tue,wed,thu,fri,sat,sun", false, "09:00:00", "17:30:00"));
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
		String addrSql = "SELECT * from name where practitioner_id = ?";
		PreparedStatement nameStatement = connection.prepareStatement(addrSql);
		nameStatement.setInt(1, pracId);
		ResultSet names = nameStatement.executeQuery();
		while(names.next()) {
			HumanName name = ResourceFactory.getHumanName(names);
			prac.addName(name);
		}
	}
	
	//TODO following is not complete at all!
	/**
	 * Handle the restrictions associated with the practitioner 
	 * @param connection
	 * @param prac
	 * @param pracId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirPractitioner prac, int pracId) throws SQLException {
		String resSql = "SELECT * from vhdir_restriction where practitioner_id = ?"; //TODO this might need to use the resource_reference table. Is it modeled?
		PreparedStatement resStatement = connection.prepareStatement(resSql);
		resStatement.setInt(1, pracId);
		ResultSet restrictions = resStatement.executeQuery();
		while(restrictions.next()) {
			Reference ref = ResourceFactory.getRestrictionReference(restrictions);
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
		String commSql = "SELECT * from communication where practitioner_id = ?";
		PreparedStatement commStatement = connection.prepareStatement(commSql);
		commStatement.setInt(1, pracId);
		ResultSet comms = commStatement.executeQuery();
		// Then, for each communication in the result set above, go and get all the codings for that communication...
		int cnt = 0;
		while (comms.next())   
		{	
			cnt++;
			CodeableConcept comm_cc = ResourceFactory.getCommunicationProficiency(comms.getString("communication_id"), connection); // To hold all the codings
			prac.addCommunication(comm_cc);
		}
		// If we didn't find any communications in the db, let's just make one up for now
		if (cnt == 0) {
			prac.addCommunication(ResourceFactory.makeCommunicationProficiencyCodes());
		}
	}


}
