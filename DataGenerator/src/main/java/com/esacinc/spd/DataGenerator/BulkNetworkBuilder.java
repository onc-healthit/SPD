package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

import com.esacinc.spd.model.VhDirAddress;
import com.esacinc.spd.model.VhDirContactPoint;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirLocation;
import com.esacinc.spd.model.VhDirNetwork;
import com.esacinc.spd.model.VhDirNetworkContact;
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
		List<VhDirNetwork> networks = new ArrayList<VhDirNetwork>();
		
		String sql = "SELECT * FROM vhdir_network";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			VhDirNetwork nw = new VhDirNetwork();
		
			// set the id
			int nwId = resultSet.getInt("network_id");
			nw.setId(resultSet.getString("network_id"));
			 
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
			
		}
		
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
		String idSql = "SELECT * from identifier where network_id = ?";
		PreparedStatement idStatement = connection.prepareStatement(idSql);
		idStatement.setInt(1, nwId);
		ResultSet idResultset = idStatement.executeQuery();
		while(idResultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(idResultset);
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
		String addrSql = "SELECT * from address where network_id = ?";
		PreparedStatement addrStatement = connection.prepareStatement(addrSql);
		addrStatement.setInt(1, nwId);
		ResultSet addrResultset = addrStatement.executeQuery();
		while(addrResultset.next()) {
			VhDirAddress addr = ResourceFactory.getAddress(addrResultset, connection);
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
		String strSql = "SELECT * from fhir_codeable_concept where network_type_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, nwId);
		ResultSet resultset = sqlStatement.executeQuery();
		while(resultset.next()) {
				nw.addType(ResourceFactory.getCodeableConcept(resultset));
		}
	}
	
	
	
	//TODO following is not complete at all!
	/**
	 * Handle the restrictions associated with the network 
	 * @param connection
	 * @param nw
	 * @param nwId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirNetwork nw, int nwId) throws SQLException {
		String resSql = "SELECT * from vhdir_restriction where network_id = ?"; //TODO this might need to use the resource_reference table. Is it modeled?
		PreparedStatement resStatement = connection.prepareStatement(resSql);
		resStatement.setInt(1, nwId);
		ResultSet restrictions = resStatement.executeQuery();
		while(restrictions.next()) {
			Reference ref = ResourceFactory.getRestrictionReference(restrictions);
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
		String addrSql = "SELECT * from resource_reference where reference = ?";
		PreparedStatement telecomStatement = connection.prepareStatement(addrSql);
		telecomStatement.setInt(1, nwId);
		ResultSet telecomResultset = telecomStatement.executeQuery();
		while(telecomResultset.next()) {
				VhDirContactPoint tele = ResourceFactory.getContactPoint(telecomResultset);
				// Add 9:00-4:30 any day, available time for this telecom contact point
				tele.addAvailableTime(ResourceFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", false, "09:00:00", "17:30:00"));
				nw.addTelecom(tele);
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
		String strSql = "SELECT * from contact where network_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, nwId);
		ResultSet resultset = sqlStatement.executeQuery();
		while(resultset.next()) {
				VhDirNetworkContact con = ResourceFactory.getNetworkContact(resultset, connection);
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
	private void handleEndpoints(Connection connection, VhDirNetwork net, int netId) throws SQLException {
		String strSql = "SELECT * from vhdir_endpoint where network_id = ?"; 
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		sqlStatement.setInt(1, netId);
		ResultSet resultset = sqlStatement.executeQuery();
		while(resultset.next()) {
			Reference ref = ResourceFactory.makeResourceReference(resultset.getString("endpoint_id"), "VhDirEndpoint", null, "Network Endpoint");
			net.addEndpoint(ref);
		}
	}
}
