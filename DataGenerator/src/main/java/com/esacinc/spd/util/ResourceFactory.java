package com.esacinc.spd.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.Reference;

import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirIdentifier.IdentifierStatus;

/**
 * This utility class contains static methods for creating a number of resources that are used in 
 * several other resources.
 * @author dandonahue
 *
 */
public class ResourceFactory {
	
	/**
	 * Creates a Reference resource using the component parameters passed in
	 * 
	 * @param resourceId
	 * @param typeUri
	 * @param identifier
	 * @param display
	 * @return Reference
	 */
	static public Reference makeResourceReference(String resourceId, String typeUri, Identifier identifier, String display) {
		Reference ref = new Reference();
		ref.setDisplay(display);
		ref.setReference(resourceId);
		ref.setIdentifier(identifier);
		ref.setType(typeUri);
		return ref;
	}
	
	/**
	 * Creates a Reference resource by retrieving the resource_reference with the given id from the database 
	 * @param refId
	 * @param connection
	 * @return Reference
	 * @throws SQLException
	 */
	static public Reference makeResourceReference(int refId, Connection connection) throws SQLException{
		Reference ref = new Reference();
		String sqlString = "SELECT * from resource_reference where resource_reference_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(sqlString);
		sqlStatement.setInt(1,refId);
		ResultSet refs = sqlStatement.executeQuery();
		while(refs.next()) {
			ref.setId(refs.getString(refId));
			ref.setDisplay(refs.getString("display"));
			ref.setReference(refs.getString("reference"));
			// If the reference points to an identifier, go get it from the db....
			String identifierId = refs.getString("identifier");
			if (identifierId != null && !identifierId.isEmpty()) {
				ref.setIdentifier(makeIdentifier(Integer.valueOf(identifierId), connection));
			}
			ref.setType(refs.getString("type"));	
			return ref;
		}
		return null;  // If we get here, there was no reference with that id
	}
	
	/**
	 * Creates a VhDirIdentifier from the identifier with the given identifierId in the database
	 * @param identifierId
	 * @param connection
	 * @return VhDirIdentifier
	 * @throws SQLException
	 */
	static public VhDirIdentifier makeIdentifier(int identifierId, Connection connection) throws SQLException{
		VhDirIdentifier identifier = new VhDirIdentifier();
		String sqlString = "SELECT * from videntifier where ridentifier_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(sqlString);
		sqlStatement.setInt(1,identifierId);
		ResultSet idResultset = sqlStatement.executeQuery();
		while(idResultset.next()) {
			identifier = makeIdentifier(idResultset);
			return identifier; // There should only be one 
		}
		return null;  // If we get here, there was no identifier with that id
	}
	
	/**
	 * Creates a VhDirIdentifier from the data pointed to by the current cursor in the given database query result set.
	 * This assumes that the ResultSet argument is the result from a "Select * from identifier ...." sql statement 
	 * @param idResultset
	 * @return VhDirIdentifier
	 * @throws SQLException
	 */
	static public VhDirIdentifier makeIdentifier(ResultSet idResultset) throws SQLException{
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
		return identifier;
	}
}
