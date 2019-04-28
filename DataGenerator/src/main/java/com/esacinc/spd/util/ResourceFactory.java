package com.esacinc.spd.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.TimeType;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;

import com.esacinc.spd.model.VhDirAddress;
import com.esacinc.spd.model.VhDirContactPoint;
import com.esacinc.spd.model.VhDirContactPointAvailableTime;
import com.esacinc.spd.model.VhDirGeoLocation;
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
	static public Reference getResourceReference(int refId, Connection connection) throws SQLException{
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
				ref.setIdentifier(getIdentifier(Integer.valueOf(identifierId), connection));
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
	static public VhDirIdentifier getIdentifier(int identifierId, Connection connection) throws SQLException{
		VhDirIdentifier identifier = new VhDirIdentifier();
		String sqlString = "SELECT * from videntifier where ridentifier_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(sqlString);
		sqlStatement.setInt(1,identifierId);
		ResultSet idResultset = sqlStatement.executeQuery();
		while(idResultset.next()) {
			identifier = getIdentifier(idResultset);
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
	static public VhDirIdentifier getIdentifier(ResultSet idResultset) throws SQLException{
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
	
	/**
	 * Creates a VhDirAddress from the identifier with the given identifierId in the database
	 * @param addrId
	 * @param connection
	 * @return VhDirAddress
	 * @throws SQLException
	 */
	static public VhDirAddress getAddress(int addrId, Connection connection) throws SQLException{
		String addrSql = "SELECT * from address where address_id = ?";
		PreparedStatement addrStatement = connection.prepareStatement(addrSql);
		addrStatement.setInt(1, addrId);
		ResultSet addrResultset = addrStatement.executeQuery();
		while(addrResultset.next()) {
			VhDirAddress addr = getAddress(addrResultset, connection);
			return addr;
		}	
		return null;  // If we get here, there was no identifier with that id
	}
	
	/**
	 * Creates a VhDirIdentifier from the data pointed to by the current cursor in the given database query result set.
	 * This assumes that the ResultSet argument is the result from a "Select * from identifier ...." sql statement 
	 * @param addrResultset
	 * @param connection  - need to pass in the connection in case we need to do some geocoding
	 * @return VhDirAddress
	 * @throws SQLException
	 */
	static public VhDirAddress getAddress(ResultSet addrResultset, Connection connection) throws SQLException{
		VhDirAddress addr = new VhDirAddress();
		
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
			
			// First check to see if the lat and lon are set
			double lat = addrResultset.getDouble("latitude");
			double lon = addrResultset.getDouble("longitude");
			
			VhDirGeoLocation loc;
			
			if (lat == 0.0) {
				System.out.println("makeAddress in ResourceFactory: Geocoding lat-lon for postal code " + postal + ", addres:"+addr.getId());
				loc = Geocoding.geocodePostalCode(postal.substring(0,5), connection);
			} else {
				loc = new VhDirGeoLocation();
				loc.setLatitude(lat);
				loc.setLongitude(lon);
			}
			
			addr.setGeolocation(loc);
		}
		
		// Set Country
		String country = addrResultset.getString("country");
		if (country != null) {
			addr.setCountry(country);
		}
		return addr;
	}
	

	/**
	 * Creates a VhDirContactPoint from the identifier with the given identifierId in the database
	 * Note:  We create VhDirContactPoint objects from rows in the database telecom table
	 * @param addrId
	 * @param connection
	 * @return VhDirAddress
	 * @throws SQLException
	 */
	static public VhDirContactPoint getContactPoint(int contactId, Connection connection) throws SQLException{
		String contactSql = "SELECT * from telecom where telecom_id = ?";
		PreparedStatement contactStatement = connection.prepareStatement(contactSql);
		contactStatement.setInt(1, contactId);
		ResultSet contactResultset = contactStatement.executeQuery();
		while(contactResultset.next()) {
			VhDirContactPoint contactPoint = getContactPoint(contactResultset);
			return contactPoint;
		}	
		return null;  // If we get here, there was no contactpoint with that id
	}

	/**
	 * Creates a VhDirContactPoint from the data pointed to by the current cursor in the given database query result set.
	 * This assumes that the ResultSet argument is the result from a "Select * from telecom ...." sql statement 
	 * @param addrResultset
	 * @return VhDirContactPoint
	 * @throws SQLException
	 */
	static public VhDirContactPoint getContactPoint(ResultSet objResultset) throws SQLException{
		VhDirContactPoint contactPoint = new VhDirContactPoint();
		// Set id
		contactPoint.setId(objResultset.getString("telecom_id"));
		
		// Set system
		String system = objResultset.getString("system");
		if (system == null)
			contactPoint.setSystem(ContactPointSystem.NULL);
		else if ("email".equals(system))
			contactPoint.setSystem(ContactPointSystem.EMAIL);
		else if ("fax".equals(system))
			contactPoint.setSystem(ContactPointSystem.FAX);
		else if ("other".equals(system))
			contactPoint.setSystem(ContactPointSystem.OTHER);
		else if ("pager".equals(system))
			contactPoint.setSystem(ContactPointSystem.PAGER);
		else if ("phone".equals(system))
			contactPoint.setSystem(ContactPointSystem.PHONE);
		else if ("sms".equals(system))
			contactPoint.setSystem(ContactPointSystem.SMS);
		else if ("url".equals(system))
			contactPoint.setSystem(ContactPointSystem.URL);
		
		// Set value
		String value = objResultset.getString("value");
		if (value != null)
			contactPoint.setValue(value);
		return contactPoint;
	}
	
	/**
	 * Generate an availableTime object from the given parameters
	 * @param daysString, comma delimited string of 3-letter day names, e.g.  "mon,tue,wed,thu,fri"
	 * @param allDay  true if this available time is all day
	 * @param startTime if allday is false, start time, e.g. "08:00:00"
	 * @param endTime if allday is false, start time, e.g. "17:00:00"
	 * @return VhDirContactPointAvailableTime
	 */
	static public VhDirContactPointAvailableTime makeAvailableTime(String daysString, boolean allDay, String startTime, String endTime) {
		// Set some available time - for organizations make it all day 7 days a week
		VhDirContactPointAvailableTime available = new VhDirContactPointAvailableTime();
		CodeType dayCode = null;
		String[] days = daysString.split(",");
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
	
	/**
	 * Create a HumanName resource from the data in the current cursor of the given result set
	 * @param names
	 * @return
	 * @throws SQLException
	 */
	static public HumanName getHumanName(ResultSet names) throws SQLException{
		HumanName name = new HumanName();
		// Set id
		name.setId(names.getString("name_id"));
		name.setFamily(names.getString("family"));
		name.addGiven(names.getString("given"));
		name.addPrefix(names.getString("prefix"));
		name.addSuffix(names.getString("suffix"));
		Period per = new Period();
		per.setStart(names.getDate("period_start"));
		per.setEnd(names.getDate("period_end"));
		name.setPeriod(per);
		
		String use = names.getString("use");
		if ( use == null || "usual".equals(use))
			name.setUse(NameUse.USUAL);
		
		return name;
	}

	/**
	 * Return a codeable concept representing a Communication proficiency. This codeable concept may contain
	 * several Code elements, which are read from the db in the given connection.
	 * @param commId
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public CodeableConcept getCommunicationProficiency(String commId, Connection connection) throws SQLException {
		CodeableConcept comm_cc = new CodeableConcept(); // To hold all the codings
		comm_cc.setId(commId);
		// Now get all the codes belonging to this communication
		String codingSql = "SELECT * from fhir_codeable_concept where communication_id = ?";
		PreparedStatement codeStatement = connection.prepareStatement(codingSql);
		codeStatement.setString(1, commId);
		ResultSet codes = codeStatement.executeQuery();
		// Then for each code, above, add to the codeable concept
		while(codes.next()) {
			Coding coding = ResourceFactory.getCommunicationProficiencyCodes(codes);
			comm_cc.addCoding(coding);
		}
		return comm_cc;
	}

	/**
	 * Retrun a Coding object created from the data in the current cursor of the give result set of codeable concepts
	 * @param codeableConcepts
	 * @return
	 * @throws SQLException
	 */
	static public Coding getCommunicationProficiencyCodes(ResultSet codeableConcepts) throws SQLException {
		Coding coding = new Coding();
		coding.setId(codeableConcepts.getString(codeableConcepts.getString("codeable_concept_id")));
		coding.setSystem("http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-languageproficiency");
		coding.setVersion("0.2.0");
		coding.setDisplay(codeableConcepts.getString("coding_display"));
		coding.setUserSelected(codeableConcepts.getBoolean("coding_user_selected"));
		coding.setCode(codeableConcepts.getString("coding_code"));
		return coding;
	}
	
	/**
	 * Return a totally made up codeable concept object representing a Communication proficiency code.
	 * The returned object id is a random integer between 1 and 10,000, prefixed with an "x";
	 * @return CodeableConcept
	 */
	static public CodeableConcept makeCommunicationProficiencyCodes() {
		CodeableConcept comm_cc = new CodeableConcept();
		Coding coding = new Coding();
		coding.setDisplay("Functional Native Proficiency");
		coding.setSystem("http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-languageproficiency");
		coding.setVersion("0.2.0");
		coding.setUserSelected(true);
		coding.setCode("50");
		// Generate a totally random id, prefixed by "x"
		Random ran = new Random();
		comm_cc.setId("x"+ ran.nextInt(10000-1 + 1));
		comm_cc.setText("Just made something up");
		comm_cc.addCoding(coding);
		return comm_cc;

	}
	
	/**
	 * Return a resource reference built from the current cursor into the given result set.
	 * @param resultset
	 * @return Reference
	 */
	static public Reference  getRestrictionReference(ResultSet resultset) {
		Reference usageRestrctionRef = new Reference();
		
		return usageRestrctionRef;
	}
}
