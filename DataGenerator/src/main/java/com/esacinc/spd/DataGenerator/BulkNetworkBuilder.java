package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

import com.esacinc.spd.model.VhDirAddress;
import com.esacinc.spd.model.VhDirContact;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirNetwork;
import com.esacinc.spd.util.ContactFactory;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.ErrorReport;
import com.esacinc.spd.util.ResourceFactory;

public class BulkNetworkBuilder {
	
	
	/**
	 * uses the connection provided to get all Network and then builds a list
	 * of Network objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirNetwork> getNetworks(Connection connection) throws SQLException, ParseException {
		int cnt = 0;
		List<VhDirNetwork> networks = new ArrayList<VhDirNetwork>();
		
	       ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_network WHERE network_id > " + BulkDataApp.FROM_ID_NETWORKS + " ORDER BY network_id",null);
		while (resultSet.next() && BulkDataApp.okToProceed(cnt)) {
			VhDirNetwork nw = new VhDirNetwork();
		
			// set the id
			int nwId = resultSet.getInt("network_id");
			nw.setId(resultSet.getString("network_id"));
			ErrorReport.setCursor("VhDirNetwork", nw.getId());

			nw.setText(ResourceFactory.makeNarrative("Organization (id: " + nwId + ")"));

			nw.setActive(resultSet.getBoolean("active"));


			// Handle the identifiers
			handleIdentifiers(connection, nw, nwId);
			
            // Handle the network types
         	handleTypes(connection, nw, nwId);
         	
			// Handle the addresses
         	handleAddresses(connection, nw, nwId);
         	
         	// Assumes the db stores the aliases for the network as a comma-delimited list.
         	String aliasList = resultSet.getString("alias");
         	if (aliasList != null && !aliasList.isEmpty()) {
         		for (String al : aliasList.split(";")) {
         			nw.addAlias(al);
         		}
         	}

            // Handle name - if no name fount, use the first alias as a kludge. If no alias, then kinda give up.
			String name = resultSet.getString("name");
			nw.setName(resultSet.getString("name"));
			if (name == null || name.isEmpty()) {  // Ummm...no name given? The use an alias for the name...
				List<StringType> als = nw.getAlias();
				if (als != null && !als.isEmpty()) {
					nw.setName(als.get(0).asStringValue()); // get first alias.
				}
				else nw.setName("Name not provided"); // oh well.
			}

         	nw.setOrganizationPeriod(ResourceFactory.makePeriod(resultSet.getDate("period_start"), resultSet.getDate("period_end")));
         	
         	// Handle the partOf resource reference
         	nw.setPartOf(ResourceFactory.getResourceReference(resultSet.getInt("part_of_resource_reference_id"), connection));
         	
            // Handle the restrictions
         	handleRestrictions(connection, nw, nwId);
         	
         	 // Handle the location references
         	handleLocationReferences(connection, nw, nwId);
         	
         	// Handle the network contacts
         	handleContacts(connection, nw, nwId);
         	
         	// Handle the network endpoints
         	handleEndpoints(connection, nw, nwId);
         	
			networks.add(nw);
			
			cnt++;
			
		}
		System.out.println("Made " + networks.size() + " networks");
		return networks;
	}

		

	/**
	 * Handles all the elements of the identifiers for Network
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirNetwork nw, int nwId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from identifier where network_id = ?", nwId);
		while(resultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(resultset);
			nw.addIdentifier(identifier);
		}
	}
	
	/**
	 * Handles the addresses for the passed in network ID
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleAddresses(Connection connection, VhDirNetwork nw, int nwId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT address_id from address where network_id = ?", nwId);
		while(resultset.next()) {
			VhDirAddress addr = ResourceFactory.getAddress(resultset.getInt("address_id"), connection);
			nw.addAddress(addr);
		}
	}
	
	/**
	 * Handles the types for the network id passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleTypes(Connection connection, VhDirNetwork nw, int nwId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where network_type_id = ?", nwId);
		while(resultset.next()) {
				nw.addType(ResourceFactory.getCodeableConcept(resultset));
		}
	}
	
	
	
	/**
	 * Handle the restrictions associated with the network 
	 * @param connection
	 * @param nw
	 * @param nwId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirNetwork nw, int nwId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where network_restriction_id = ?", nwId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			nw.addUsageRestriction(ref);
		}
	}
	
	
	/**
	 * Handles the location references for the network id passed in
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleLocationReferences(Connection connection, VhDirNetwork nw, int nwId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where network_location_id = ?", nwId);
		while(resultset.next()) {
				Reference ref = ResourceFactory.getResourceReference(resultset,connection);
				nw.addLocationReference(ref);
		}
	}

	/**
	 * Handles the network contacts for the network passed in
	 * @param connection
	 * @param nw
	 * @param nwId
	 * @throws SQLException
	 */
	private void handleContacts(Connection connection, VhDirNetwork nw, int nwId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from contact where network_id = ?", nwId);
		while(resultset.next()) {
				VhDirContact con = ContactFactory.getContact(resultset, connection);
				nw.addContact(con);
		}
	}
	
	/**
	 * Handle the restrictions associated with the network 
	 * @param connection
	 * @param prac
	 * @param locId
	 * @throws SQLException
	 */
	private void handleEndpoints(Connection connection, VhDirNetwork net, int nwId) throws SQLException {
	    ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from vhdir_endpoint where network_id = ?", nwId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("endpoint_id"), "VhDirEndpoint", null, "Network Endpoint");
			net.addEndpoint(ref);
		}
	}
}
