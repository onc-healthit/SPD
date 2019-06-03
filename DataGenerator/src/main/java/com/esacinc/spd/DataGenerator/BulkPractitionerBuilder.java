package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.codesystems.NarrativeStatus;

import com.esacinc.spd.model.VhDirAddress;
import com.esacinc.spd.model.VhDirTelecom;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirPractitioner;
import com.esacinc.spd.util.ContactFactory;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.DigitalCertificateFactory;
import com.esacinc.spd.util.ErrorReport;
import com.esacinc.spd.util.ResourceFactory;

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
	       ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_practitioner WHERE practitioner_id > " + BulkDataApp.FROM_ID_PRACTITIONERS + " ORDER BY practitioner_id",null);
		while (resultSet.next() && BulkDataApp.okToProceed(cnt)) {
			VhDirPractitioner prac = new VhDirPractitioner();
		
			// set the id
			int pracId = resultSet.getInt("practitioner_id");
			prac.setId(resultSet.getString("practitioner_id"));
			ErrorReport.setCursor("VhDirPractitioner", prac.getId());

			prac.setText(ResourceFactory.makeNarrative("Practitioner (id: " + pracId + ")"));

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
				prac.setGender(AdministrativeGender.fromCode(gender));
			}
			catch (Exception e){
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
				ErrorReport.writeWarning("VhDirPractitioner", prac.getId(), "unrecognized gender: " + gender, e.getMessage());

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
			PractitionerQualificationComponent qu = ResourceFactory.getPractitionerQualification(resultset, connection);
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
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT address_id from address where practitioner_id = ?", pracId);
		while(resultset.next()) {
			VhDirAddress addr = ResourceFactory.getAddress(resultset.getInt("address_id"), connection);
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
				VhDirTelecom tele = ContactFactory.getTelecom(resultset,connection);
				if (!tele.hasAvailableTime()) {
					// Add 9:00-4:30 any day, available time for this telecom contact point
					tele.addAvailableTime(ContactFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
				}
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
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
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
