package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.Organization.OrganizationContactComponent;

import com.esacinc.spd.model.VhDirAlias;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirIdentifier.IdentifierStatus;
import com.esacinc.spd.model.VhDirOrganization;

public class BulkOrganizationBuilder {
	
	public BulkOrganizationBuilder() {
		
	}
	
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
		
		String sql = "SELECT * FROM vhdir_organization LIMIT 10";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			VhDirOrganization org = new VhDirOrganization();
		
			// set the id
			org.setId(resultSet.getString("organization_id"));
			 					
			// Handle description
			String description = resultSet.getString("description");
			if (description != null) {
				org.setDescription(description);
			}
						
			// Handle the identifiers
			int orgId = resultSet.getInt("organization_id");
			handleIdentifiers(connection, org, orgId);
			
			// TODO Hard coded active for now
			org.setActive(true);
			
			// TODO Hard coded type as provider for now
			CodeableConcept typeConcept = new CodeableConcept();
            Coding typeCode = new Coding();
            typeCode.setCode("prov");
            typeCode.setSystem("http://terminology.hl7.org/CodeSystem/organization-type");
            typeConcept.addCoding(typeCode);
            org.addType(typeConcept);
					
            // Handle the name
         	String name = resultSet.getString("name");
         	if (name != null) {
         		org.setName(name);
         	}
         			
			// Handle aliases
			
            // Handle the telecoms
         	
			// Handle the addresses
         	handleAddresses(connection, org, orgId);
			
			// Handle the partOf association reference
			
			// Handle contacts
         	handleContacts(connection, org, orgId);
			 
			organizations.add(org);
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
			VhDirIdentifier identifier = new VhDirIdentifier();
			
			// Set id
			identifier.setId(idResultset.getString("identifier_id"));
			
			// Handle identifier status
			String status = idResultset.getString("identifier_status_value_code");
			if ("active".equals(status))
				identifier.setStatus(IdentifierStatus.ACTIVE);
		    if ("inactive".equals(status))
		    	identifier.setStatus(IdentifierStatus.INACTIVE);
		    if ("issuedinerror".equals(status))
		    	identifier.setStatus(IdentifierStatus.ISSUEDINERROR);
		    if ("revoked".equals(status))
		    	identifier.setStatus(IdentifierStatus.REVOKED);
		    if ("pending".equals(status))
		    	identifier.setStatus(IdentifierStatus.PENDING);
		    if ("unknown".equals(status))
		    	identifier.setStatus(IdentifierStatus.UNKNOWN);
		    
		    // Handle use
		    String use = idResultset.getString("use");
		    if ("usual".equals(use))
		        identifier.setUse(IdentifierUse.USUAL);
		    if ("official".equals(use))
		    	identifier.setUse(IdentifierUse.OFFICIAL);
		    if ("temp".equals(use))
		    	identifier.setUse(IdentifierUse.TEMP);
		    if ("secondary".equals(use))
		    	identifier.setUse(IdentifierUse.SECONDARY);
		    if ("old".equals(use))
		    	identifier.setUse(IdentifierUse.OLD);
		    
		    // Handle system
		    String system = idResultset.getString("system");
		    identifier.setSystem(system);
		    
		    // Handle value
		    String value = idResultset.getString("value");
		    identifier.setValue(value);
			
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
			Address addr = new Address();
			
			// Set ID
			addr.setId(addrResultset.getString("address_id"));
			
			// Set use
			String use = addrResultset.getString("use");
			if ("home".equals(use))
				addr.setUse(AddressUse.HOME);
			if ("work".equals(use))
				addr.setUse(AddressUse.WORK);
			if ("temp".equals(use))
				addr.setUse(AddressUse.TEMP);
			if ("old".equals(use))
				addr.setUse(AddressUse.OLD);
			if ("billing".equals(use))
				addr.setUse(AddressUse.BILLING);
			
			// Set Type
			
			// Set Text
			
			// Set Line
			String line1 = addrResultset.getString("line1");
			if (line1 != null ) {
				addr.addLine(line1);
			}
			String line2 = addrResultset.getString("line2");
			if (line2 != null) {
				addr.addLine(line2);
			}
			
			// Set City
			String city = addrResultset.getString("line2");
			if (city != null) {
				addr.setCity(city);
			}
			
			// Set District (County)
			String district = addrResultset.getString("district");
			if (district != null) {
				addr.setDistrict(district);
			}
						
			// Set State
			String state = addrResultset.getString("state");
			if (state != null) {
				addr.setState(state);
			}
			
			// Set PostalCode
			String postal = addrResultset.getString("postalCode");
			if (postal != null) {
				addr.setPostalCode(postal);
			}
			
			// Set PostalCode
			String country = addrResultset.getString("country");
			if (country != null) {
				addr.setCountry(country);
			}
			
			org.addAddress(addr);
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
     			"FROM spd.name as n, spd.organization_contact as oc " +
     			"WHERE n.name_id = oc.name_id AND oc.organization_id = ?";
     	PreparedStatement contactStatement = connection.prepareStatement(contactSql);
     	contactStatement.setInt(1, orgId);
		ResultSet contactResultset = contactStatement.executeQuery();
		while(contactResultset.next()) {
			OrganizationContactComponent occ = new OrganizationContactComponent();
			
			// Set id
			occ.setId(contactResultset.getString("organization_contact_id"));
			
			// Set name
			HumanName name = new HumanName();
			name.setId(contactResultset.getString("name_id"));
			name.setFamily(contactResultset.getString("family"));
			name.addPrefix(contactResultset.getString("prefix"));
			name.addGiven(contactResultset.getString("given"));
			name.addSuffix(contactResultset.getString("suffix"));
			
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
			alias.setValue(aliasResultset.getString("value"));
			
			//org.addAlias(alias);
		}
	}
}
