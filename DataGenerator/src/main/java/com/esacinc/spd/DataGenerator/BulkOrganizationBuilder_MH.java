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
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.Organization.OrganizationContactComponent;
import org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent;
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
import com.esacinc.spd.model.VhDirNetwork;
import com.esacinc.spd.model.VhDirIdentifier.IdentifierStatus;
import com.esacinc.spd.model.VhDirOrganization_MH;
import com.esacinc.spd.model.VhDirPractitioner;
import com.esacinc.spd.util.ContactFactory;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.DigitalCertificateFactory;
import com.esacinc.spd.util.Geocoding;
import com.esacinc.spd.util.ResourceFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BulkOrganizationBuilder_MH {
	
	
	/**
	 * uses the connection provided to get all organization and then builds a list
	 * of Organization objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirOrganization_MH> getOrganizations(Connection connection) throws SQLException, ParseException {
		List<VhDirOrganization_MH> organizations = new ArrayList<VhDirOrganization_MH>();
		int cnt = 0;
		int certCount = 0;
        ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_organization", null);
		while (resultSet.next() && cnt < BulkDataApp.MAX_ENTRIES) {
			VhDirOrganization_MH org = new VhDirOrganization_MH();
			try {
			// set the id
			int orgId = resultSet.getInt("organization_id");
			org.setId(resultSet.getString("organization_id"));
			 					
			// Handle description
			String description = resultSet.getString("description");
			if (description != null) {
				org.setDescription(description);
			}
			
			// Add a digital certificate to the first 3 organizations
			if (certCount < DigitalCertificateFactory.MAX_CERTS) {
				// args are:  nthCert, type, use, trustFramework, standard, expirationDate
				org.addDigitalcertficate(DigitalCertificateFactory.makeDigitalCertificate(certCount++, "role", "auth", "other", "x.509v3", null));
			}
						
			// Handle the identifiers
			handleIdentifiers(connection, org, orgId);
			
			// TODO Hard coded active for now
			int active = resultSet.getInt("active");
			if (active == 1)
				org.setActive(true);
			else
				org.setActive(false);
			
			// TODO Hard coded type as provider for now
			CodeableConcept typeConcept = new CodeableConcept();
            typeConcept.addCoding(ResourceFactory.makeCoding("prov", "Healthcare Provider", "http://terminology.hl7.org/CodeSystem/organization-type", false));
            org.addType(typeConcept);
					
            // Handle the name
         	String name = resultSet.getString("name");
         	if (name != null) {
         		org.setName(name);
         	}
         			
			// Handle aliases
         	// TODO handleAliases(connection, org, orgId);
			
            // Handle the telecoms
         	handleTelecoms(connection, org, orgId);
         	
			// Handle the addresses
         	handleAddresses(connection, org, orgId);
			
			// Handle the partOf association reference
         	String partOfId = resultSet.getString("partOf_organization_id");
         	if (partOfId != null) {
	         	Reference partOf = new Reference();
	         	partOf.setId(partOfId);
	         	partOf.setReference("urn:uuid:" + partOfId);
	         	org.setPartOf(partOf);
         	}
			
			// Handle contacts
         	handleContacts(connection, org, orgId);
         	
         	// Handle endpoints
         	handleEndpoints(connection, org, orgId);

            // Handle the restrictions
         	//handleRestrictions(connection, org, orgId);

			organizations.add(org);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			cnt++;
		}
		System.out.println("Made " + organizations.size() + " organizations");
		return organizations;
	}
	
	/**
	 * Handles all the elements of the identifiers for Organizations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirOrganization_MH org, int orgId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from identifier where organization_id = ?", orgId);
		while(resultset.next()) {
				VhDirIdentifier identifier = ResourceFactory.getIdentifier(resultset);
				org.addIdentifier(identifier);
		}
	}
	
	/**
	 * Handles the addresses for the passed in organization ID
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleAddresses(Connection connection, VhDirOrganization_MH org, int orgId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from address where organization_id = ?", orgId);
		while(resultset.next()) {
			VhDirAddress addr = ResourceFactory.getAddress(resultset, connection);
			org.addAddress(addr);
		}
	}
	
	/**
	 * Handles the telecoms for the organization id passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleTelecoms(Connection connection, VhDirOrganization_MH org, int orgId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from telecom where organization_id = ?", orgId);
		while(resultset.next()) {
			VhDirContactPoint tele = ContactFactory.getContactPoint(resultset,connection);
			tele.setId(resultset.getString("telecom_id"));
			// Add a 24x7 available time for this telecom contact point
			tele.addAvailableTime(ContactFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat;sun", true, null, null));
			org.addTelecom(tele);
		}
	}
	
	/**
	 * Handles the contacts for the organization ID passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleContacts(Connection connection, VhDirOrganization_MH org, int orgId) throws SQLException {
     	ResultSet resultset = DatabaseUtil.runQuery(connection, 
     			"SELECT n.name_id, n.use, n.prefix, n.family, n.given, n.suffix, n.period_start, n.period_end, " +
				"oc.contact_id " +
     			"FROM name as n, contact as oc " +
     			"WHERE n.name_id = oc.name_id AND oc.organization_id = ?", 
     			orgId);
		while(resultset.next()) {
			OrganizationContactComponent occ = new OrganizationContactComponent();
			
			// Set id
			int orgContactId = resultset.getInt("contact_id");
			occ.setId(resultset.getString("contact_id"));
			
			// Set name
			occ.setName(ResourceFactory.getHumanName(resultset));
			
			// Handle telecoms for contacts
			handleContactTelecoms(connection, occ, orgContactId);
			
			org.addContact(occ);
		}
	}
	
	/**
	 * Handles the aliases for an organization using the organization ID passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleAliases(Connection connection, VhDirOrganization_MH org, int orgId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * FROM organization_alias WHERE organization_id = ?", orgId);
		while(resultset.next()) {
			VhDirAlias alias = new VhDirAlias();
			
			// Set id
			alias.setId(resultset.getString("organization_alias_id"));
			
			// Handle type
			
			// Handle period
			
			// Handle value
			String value = resultset.getString("value");
			if (value != null)
				alias.setValue(value);
			
			org.addAlias(alias);
		}
	}
	
	/**
	 * Gets the telecoms for the contacts for an organization given the organization contact ID
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleContactTelecoms(Connection connection, OrganizationContactComponent occ, int orgContactId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * FROM telecom WHERE contact_id = ?", orgContactId);
		while(resultset.next()) {
			VhDirContactPoint tele = ContactFactory.getContactPoint(resultset,connection); 
			// Add weekday, normal business hours availablility for this contact
			tele.addAvailableTime(ContactFactory.makeAvailableTime("mon;tue;wed;thu;fri", false, "08:00:00", "17:00:00"));
			occ.addTelecom(tele);
		}
	}

	/**
	 * Handle the endpoint references associated with the organization 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleEndpoints(Connection connection, VhDirOrganization_MH org, int orgId) throws SQLException {
        ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from vhdir_endpoint where organization_id = ?", orgId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("endpoint_id"), "VhDirEndpoint", null, "Organization Endpoint");
			org.addEndpoint(ref);  
		}
	}

	/**
	 * Handle the restrictions associated with the organization 
	 * @param connection
	 * @param prac
	 * @param pracId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirOrganization_MH org, int orgId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where organization_restriction_id = ?", orgId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset.getInt("resource_reference_id"),connection);
			org.addUsageRestriction(ref);
		}
	}



}