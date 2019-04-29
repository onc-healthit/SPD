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
import com.esacinc.spd.model.VhDirIdentifier.IdentifierStatus;
import com.esacinc.spd.model.VhDirOrganization;
import com.esacinc.spd.util.DigitalCertificateFactory;
import com.esacinc.spd.util.Geocoding;
import com.esacinc.spd.util.ResourceFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BulkOrganizationBuilder {
	
	
	/**
	 * uses the connection provided to get all organization and then builds a list
	 * of Organization objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirOrganization> getOrganizations(Connection connection) throws SQLException, ParseException {
		List<VhDirOrganization> organizations = new ArrayList<VhDirOrganization>();
		
		int certCount = 0;
		String sql = "SELECT * FROM vhdir_organization";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			VhDirOrganization org = new VhDirOrganization();
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
			if (certCount < 3) {
				// Figure the date one year from now, use that as an expiration date
				Date expire = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(expire);
				cal.add(Calendar.YEAR, 1);
				// args are:  nthCert, type, use, trustFramework, standard, expirationDate
				org.addDigitalcertficate(DigitalCertificateFactory.makeDigitalCertificate(certCount++, "role", "auth", "other", "x.509.v3", cal.getTime()));
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
            typeConcept.addCoding(ResourceFactory.makeCoding("prov", "prov", "http://terminology.hl7.org/CodeSystem/organization-type", false));
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
			 
			organizations.add(org);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
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
	private void handleIdentifiers(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
		String idSql = "SELECT * from identifier where organization_id = ?";
		PreparedStatement idStatement = connection.prepareStatement(idSql);
		idStatement.setInt(1, orgId);
		ResultSet idResultset = idStatement.executeQuery();
		while(idResultset.next()) {
				VhDirIdentifier identifier = ResourceFactory.getIdentifier(idResultset);
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
	private void handleAddresses(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
		String addrSql = "SELECT * from address where organization_id = ?";
		PreparedStatement addrStatement = connection.prepareStatement(addrSql);
		addrStatement.setInt(1, orgId);
		ResultSet addrResultset = addrStatement.executeQuery();
		while(addrResultset.next()) {
			while(addrResultset.next()) {
				VhDirAddress addr = ResourceFactory.getAddress(addrResultset, connection);
				org.addAddress(addr);
			}
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
	private void handleTelecoms(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
		String addrSql = "SELECT * from telecom where organization_id = ?";
		PreparedStatement telecomStatement = connection.prepareStatement(addrSql);
		telecomStatement.setInt(1, orgId);
		ResultSet telecomResultset = telecomStatement.executeQuery();
		while(telecomResultset.next()) {
			VhDirContactPoint tele = ResourceFactory.getContactPoint(telecomResultset);
			// Add a 24x7 available time for this telecom contact point
			tele.addAvailableTime(ResourceFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat;sun", true, null, null));
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
	private void handleContacts(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
		String contactSql = "SELECT n.name_id, n.use, n.prefix, n.family, n.given, n.suffix, " +
				"oc.organization_contact_id " +
     			"FROM name as n, organization_contact as oc " +
     			"WHERE n.name_id = oc.name_id AND oc.organization_id = ?";
     	PreparedStatement contactStatement = connection.prepareStatement(contactSql);
     	contactStatement.setInt(1, orgId);
		ResultSet contactResultset = contactStatement.executeQuery();
		while(contactResultset.next()) {
			OrganizationContactComponent occ = new OrganizationContactComponent();
			
			// Set id
			int orgContactId = contactResultset.getInt("organization_contact_id");
			occ.setId(contactResultset.getString("organization_contact_id"));
			
			// Set name
			HumanName name = new HumanName();
			String nameId = contactResultset.getString("name_id");
			if (nameId != null)
				name.setId(nameId);
			
			String family = contactResultset.getString("family");
			if (family != null)
				name.setFamily(family);
			
			String prefix = contactResultset.getString("prefix");
			if (prefix != null)
				name.addPrefix(prefix);
			
			String given = contactResultset.getString("given");
			if (given != null)
				name.addGiven(given);
			
			String suffix = contactResultset.getString("suffix");
			if (suffix != null)
				name.addSuffix(suffix);
			
			// Set name use
			String use = contactResultset.getString("use");
			if ("official".equals(use))
				name.setUse(NameUse.OFFICIAL);
			if ("official".equals(use))
				name.setUse(NameUse.ANONYMOUS);
			if ("official".equals(use))
				name.setUse(NameUse.MAIDEN);
			if ("official".equals(use))
				name.setUse(NameUse.NICKNAME);
			if ("official".equals(use))
				name.setUse(NameUse.OLD);
			if ("official".equals(use))
				name.setUse(NameUse.TEMP);
			if ("official".equals(use))
				name.setUse(NameUse.USUAL);
			
			occ.setName(name);
			
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
	private void handleAliases(Connection connection, VhDirOrganization org, int orgId) throws SQLException {
		String aliasSql = "SELECT * FROM organization_alias WHERE organization_id = ?";
     	PreparedStatement aliasStatement = connection.prepareStatement(aliasSql);
     	aliasStatement.setInt(1, orgId);
		ResultSet aliasResultset = aliasStatement.executeQuery();
		while(aliasResultset.next()) {
			VhDirAlias alias = new VhDirAlias();
			
			// Set id
			alias.setId(aliasResultset.getString("organization_alias_id"));
			
			// Handle type
			
			// Handle period
			
			// Handle value
			String value = aliasResultset.getString("value");
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
	private void handleContactTelecoms(Connection connection, OrganizationContactComponent occ,
			int orgContactId) throws SQLException {
		String contactSql = "SELECT * FROM telecom WHERE organization_contact_id = ?";
     	PreparedStatement telecomStatement = connection.prepareStatement(contactSql);
     	telecomStatement.setInt(1, orgContactId);
		ResultSet telecomResultset = telecomStatement.executeQuery();
		while(telecomResultset.next()) {
			VhDirContactPoint tele = ResourceFactory.getContactPoint(telecomResultset); 
			// Add weekday, normal business hours availablility for this contact
			tele.addAvailableTime(ResourceFactory.makeAvailableTime("mon;tue;wed;thu;fri", false, "08:00:00", "17:00:00"));
			occ.addTelecom(tele);
		}
	}




}
