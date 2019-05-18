package com.esacinc.spd.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CareTeam.CareTeamParticipantComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Endpoint.EndpointStatus;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceEligibilityComponent;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Location.DaysOfWeek;
import org.hl7.fhir.r4.model.Location.LocationHoursOfOperationComponent;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Signature;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.VerificationResult.VerificationResultAttestationComponent;
import org.hl7.fhir.r4.model.VerificationResult.VerificationResultValidatorComponent;

import com.esacinc.spd.model.VhDirAddress;
import com.esacinc.spd.model.VhDirEhr;
import com.esacinc.spd.model.VhDirEndpoint;
import com.esacinc.spd.model.VhDirEndpointUseCase;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirIdentifier.IdentifierStatus;
import com.esacinc.spd.model.VhDirNewpatientprofile;
import com.esacinc.spd.model.VhDirNewpatients;
import com.esacinc.spd.model.VhDirNote;
import com.esacinc.spd.model.VhDirPrimarySource;

/**
 * This utility class contains static methods for creating a number of resources that are used in 
 * several other resources.
 * @author dandonahue
 *
 */
public class ResourceFactory {

	///////////////   GET METHODS  ////////////////////////////////////////////////////////////////
    // Get methods are those methods that create resources from data obtained from the database,
	// often presented in the form of a resultset.
	// For methods that create resources from scratch (no database query), see Make Methods, below
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a Reference resource by retrieving the resource_reference with the given id from the database 
	 * @param refId
	 * @param connection
	 * @return Reference
	 * @throws SQLException
	 */
	static public Reference getResourceReference(int refId, Connection connection) throws SQLException{
		ResultSet refs = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where resource_reference_id = ?", refId);
		while(refs.next()) {
			return getResourceReference(refs,connection); // We only expect one in this case
		}
		ErrorReport.writeError("Reference", String.valueOf(refId), "No resource reference found with id " + refId , "ResourceFactory.getResourceReference");
		return null;  // If we get here, there was no reference with that id
	}

	static public Reference getResourceReference(ResultSet resulset, Connection connection) throws SQLException{
		Reference ref = new Reference();
		ref.setId(resulset.getString("resource_reference_id"));
		ref.setDisplay(resulset.getString("display"));
		ref.setReference(resulset.getString("reference"));
		// If the reference points to an identifier, go get it from the db....
		String identifierId = resulset.getString("reference");
		if (identifierId != null && !identifierId.isEmpty()) {
			ref.setIdentifier(getIdentifier(Integer.valueOf(ref.getReference()), connection));
		}
		ref.setType(resulset.getString("type"));	
		return ref;
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
		String sqlString = "SELECT * from identifier where ridentifier_id = ?";
		PreparedStatement sqlStatement = connection.prepareStatement(sqlString);
		sqlStatement.setInt(1,identifierId);
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from identifier where identifier_id = ?", identifierId);
		while(resultset.next()) {
			identifier = getIdentifier(resultset);
			return identifier; // There should only be one 
		}
		ErrorReport.writeError("VhDirIdentifier", String.valueOf(identifierId), "No identifier found with id " + identifierId , "ResourceFactory.getIdentifier");
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
		
		// Handle use
	    try {
	    	identifier.setUse(IdentifierUse.fromCode(idResultset.getString("use")));
	    }
	    catch (Exception e) {
	    	identifier.setUse(IdentifierUse.NULL);
			ErrorReport.writeWarning("VhDirIdentifier", identifier.getId(), "unrecognized use", e.getMessage());
	    }
	    
	    // Handle system
	    String system = idResultset.getString("system");
	    identifier.setSystem(system);
	    
	    // Handle value
	    String value = idResultset.getString("value");
	    identifier.setValue(value);
	    
		// Handle identifier status
		String status = idResultset.getString("identifier_status_value_code");
		try {
			identifier.setStatus(IdentifierStatus.fromCode(status));
		}
		catch (Exception e) {
			identifier.setStatus(IdentifierStatus.UNKNOWN);
			ErrorReport.writeWarning("VhDirIdentifier", identifier.getId(), "unrecognized status", e.getMessage());
		}

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
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from address where address_id = ?", addrId);
		while(resultset.next()) {
			VhDirAddress addr = getAddress(resultset, connection);
			return addr;
		}	
		ErrorReport.writeError("VhDirAddress", String.valueOf(addrId), "No address found with id " + addrId , "ResourceFactory.getAddress");
		return null;  // If we get here, there was no identifier with that id
	}
	
	/**
	 * Creates a VhDirAddress from the data pointed to by the current cursor in the given database query result set.
	 * This assumes that the ResultSet argument is the result from a "Select * from address ...." sql statement 
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
		try {
			addr.setUse(AddressUse.fromCode(addrResultset.getString("use")));
		}
		catch (Exception e) {
			addr.setUse(AddressUse.NULL);
			ErrorReport.writeWarning("VhDirAddress", addr.getId(), "unrecognized address use", e.getMessage());

		}
		
		// Set Type
		try {
			addr.setType(AddressType.fromCode(addrResultset.getString("type")));
		}
		catch (Exception e) {
			addr.setType(AddressType.NULL);
			ErrorReport.writeWarning("VhDirAddress", addr.getId(), "unrecognized address type", e.getMessage());

		}
		
		// Set text
		addr.setText(addrResultset.getString("text"));
		
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
			
			// Set Geolocation
			Double lat = addrResultset.getDouble("latitude");
			Double lon = addrResultset.getDouble("longitude");
			addr.setGeolocation(Geocoding.getGeoLocation(lat, lon, postal,  connection));
			
		}
		
		// Set Country
		String country = addrResultset.getString("country");
		if (country != null) {
			addr.setCountry(country);
		}
		
		addr.setPeriod(makePeriod(addrResultset.getDate("period_start"), addrResultset.getDate("period_end")));
		
		return addr;
	}
	

	/**
	 * Creates a HumanName resource from data in the name table of the database with the given name_id
	 * @param name_id
	 * @param connection
	 * @return HumanName
	 * @throws SQLException
	 */
	static public HumanName getHumanName(int nameId, Connection connection) throws SQLException{
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from name where name_id = ?", nameId);
		while(resultset.next()) {
			return getHumanName(resultset);  // We expect only one.
		}
		ErrorReport.writeError("HumanName", String.valueOf(nameId), "No name found with id " + nameId , "ResourceFactory.getHumanName");

		return null; // If we get here, there was no name with that id
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
		try {
			name.setUse(NameUse.fromCode(names.getString("use")));
		}
		catch (Exception e) {
			name.setUse(NameUse.NULL);
			ErrorReport.writeWarning("HumanName", name.getId(), "Unrecognized use", e.getMessage());
		}		
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
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from fhir_codeable_concept where communication_id = ?", Integer.getInteger(commId));
		// Then for each code, above, add to the codeable concept
		while(resultset.next()) {
			Coding coding = ResourceFactory.getCommunicationProficiencyCodes(resultset);
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
	 * Return a resource reference built from the current cursor into the given result set.
	 * @param resultset
	 * @return Reference
	 */
	static public Reference  getRestrictionReference(ResultSet resultset) {
		Reference usageRestrctionRef = new Reference();
		
		return usageRestrctionRef;
	}

	/**
	 * Return a CodeableConcept built from the current cursor into the given result set.
	 * Note that a CodeableConcept may have multiple codings within it. However, the SPD
	 * database only models one coding in a CodeableConcept, and that coding is represented
	 * by individual fields in the resultset row.
	 * 
	 * @param resultset
	 * @return CodeableConcept
	 */

	static public CodeableConcept getCodeableConcept(ResultSet resultset) throws SQLException{
		CodeableConcept cc = new CodeableConcept();
		cc.setId(resultset.getString("codeable_concept_id"));
		cc.setText(resultset.getString("text"));
		Coding cdng = new Coding();
		cdng.setCode(resultset.getString("coding_code"));
		cdng.setSystem(resultset.getString("coding_system"));
		cdng.setVersion(resultset.getString("coding_version"));
		cdng.setDisplay(resultset.getString("coding_display"));
		cdng.setUserSelected(resultset.getBoolean("coding_user_selected"));
		cc.addCoding(cdng);
		return cc;
	}
	
	/**
	 * Return a CodeableConcept object created from the row in the fhir_codeable_concept table with the given id.
	 * 
	 * @param concept_id
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public CodeableConcept getCodeableConcept(int conceptId, Connection connection) throws SQLException{
		ResultSet resultset = DatabaseUtil.runQuery(connection,"SELECT * from fhir_codeable_concept where codeable_concept_id = ?", conceptId);
		while(resultset.next()) {
			return getCodeableConcept(resultset); // We only expect one with this query
		}
		ErrorReport.writeError("CodeableConcept", String.valueOf(conceptId), "No CodeableConcept found with id: " + conceptId, "ResourceFactory.getCodeableConcept");
		return null;  // If we get here, there was no cc found.
	}

	/**
	 * Return a VhDirEndpoint object created from the row in the fhir_codeable_concept table with the given id.
	 * @param resultset
	 * @param connection  if not null, used to get other resources to populate the endpoint
	 * @return VhDirEndpoint
	 * @throws SQLException
	 */
	static public VhDirEndpoint getEndpoint(ResultSet resultset, Connection connection) throws SQLException{
		VhDirEndpoint ep = new VhDirEndpoint();
		ep.setId(resultset.getString("endpoint_id"));
		ep.setRank(new IntegerType(resultset.getInt("rank")));
		ep.setStatus(EndpointStatus.valueOf(resultset.getString("status")));
		ep.setConnectionType(makeCoding(resultset.getString("connectionType"),resultset.getString("connectionType"),"http://terminology.hl7.org/CodeSystem/endpoint-connection-type",false));
		ep.setName(resultset.getString("name"));
		ep.setPeriod(makePeriod(resultset.getDate("period_start"),resultset.getDate("period_start")));
		ep.addPayloadMimeType(resultset.getString("payload_mime_type"));
		ep.addHeader(resultset.getString("header"));
		if (connection != null) {
			String strSql = "SELECT * FROM fhir_codeable_concept WHERE endpoint_payload_type_id = ?";
	     	PreparedStatement sqlStatement = connection.prepareStatement(strSql);
	     	sqlStatement.setInt(1, Integer.valueOf(ep.getId()));
			ResultSet sqlResultset = DatabaseUtil.runQuery(connection,"SELECT * FROM fhir_codeable_concept WHERE endpoint_payload_type_id = ?", Integer.valueOf(ep.getId()));
			while(sqlResultset.next()) {
				ep.addPayloadType(getCodeableConcept(sqlResultset));
			}
			sqlResultset = DatabaseUtil.runQuery(connection,"SELECT * FROM fhir_restriction WHERE endpoint_id = ?", Integer.valueOf(ep.getId()));
			while(sqlResultset.next()) {
				ep.addUsageRestriction(getRestrictionReference(sqlResultset));
			}
		}
		// Digital certificates, resrtictions and use cases should be added after this object is created.
		return ep;
	}

	/**
	 * Return a VhDirEndpointUseCase from data in a row in the use_case table
	 * @param ucId
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	static public VhDirEndpointUseCase getEndpointUseCase(String ucId, Connection connection) throws SQLException {
		VhDirEndpointUseCase uc = new VhDirEndpointUseCase(); //
		uc.setId(ucId);
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from use_case where use_case_id = ?", Integer.valueOf(ucId)); 
		while(resultset.next()) {
			uc.setCaseType(getCodeableConcept(resultset.getInt("type_cc_id"),connection));
			uc.setStandard(new UriType(resultset.getString("standard")));
			// uc.setUrl (Url is handled automatically in the class definition)
			return uc; // We are expecting only one.
		}
		ErrorReport.writeError("VhDirEndpointUseCase", String.valueOf(ucId), "No use_case found with id: " + ucId, "ResourceFactory.getEndpointUseCase");
		return null;
	}

	

	public static VhDirEhr getEhr(ResultSet resultset, Connection connection ) throws SQLException {
		VhDirEhr ehr = new VhDirEhr();
		ehr.setId(resultset.getString("vhdir_ehr_id"));
		String url = resultset.getString("url");
		if (url == null || url.isEmpty()) {
			url = "http://hl7.org/fhir/uv/vhdir/StructureDefinition/ehr";
		}
		ehr.setUrl(url);
		ehr.setDeveloper(new StringType(resultset.getString("developer")));
		ehr.setProduct(new StringType(resultset.getString("product")));
		ehr.setVersion(new StringType(resultset.getString("version")));
		ehr.setCertificationEdition(makeCoding(resultset.getString("certification_edition"), "", "http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-ehrcharacteristics", false));
		ehr.setCertificationID(new StringType(resultset.getString("certification_id")));
		if (connection != null) {
			// Gather the patient access codeable concepts for this ehr
			ResultSet sqlResultset = DatabaseUtil.runQuery(connection,"SELECT * FROM fhir_codeable_concept WHERE ehr_patient_acces_id = ?",resultset.getInt("vhdir_ehr_id"));
			while(sqlResultset.next()) {
				CodeableConcept cc = ResourceFactory.getCodeableConcept(sqlResultset);
				ehr.addPatientAccess(cc);
			}
		}
		return ehr;
	}

	public static VhDirNewpatients getNewPatients(ResultSet resultset, Connection connection ) throws SQLException {
		VhDirNewpatients np = new VhDirNewpatients();
		String url = resultset.getString("url");
		if (url == null || url.isEmpty()) {
			url = "http://hl7.org/fhir/uv/vhdir/StructureDefinition/newpatients";
		}
		np.setUrl(url);
		np.setId(resultset.getString("new_patients_id"));
		np.setAcceptingPatients(new BooleanType(resultset.getBoolean("accepting_patient")));
		// Note: the network_resource_reference_id points to a row in the resource_reference table. 
		String nwrk = resultset.getString("network_resource_reference_id");
		if (nwrk != null && !nwrk.isEmpty()) {
			np.setNetwork(makeResourceReference(nwrk, "VhDirNetwork", null, "Network for a newpatients"));;
		}
		return np;
	}
	
	public static VhDirNewpatientprofile getNewPatientprofile(ResultSet resultset) throws SQLException {
		VhDirNewpatientprofile np = new VhDirNewpatientprofile();
		String url = resultset.getString("url");
		if (url == null || url.isEmpty()) {
			url = "http://hl7.org/fhir/uv/vhdir/StructureDefinition/newpatientprofile";
		}
		np.setUrl(url);
		np.setId(resultset.getString("new_patient_profile_id"));
		np.setValue(new StringType(resultset.getString("profile_string")));
		return np;
	}
	
	public static LocationHoursOfOperationComponent getHoursOfOperation(ResultSet resultset) throws SQLException {
		LocationHoursOfOperationComponent hrs = new LocationHoursOfOperationComponent();
		hrs.setId(resultset.getString("available_time_id"));
		hrs.setAllDay(resultset.getBoolean("all_day"));
		hrs.setOpeningTime(resultset.getString("available_start_time"));
		hrs.setClosingTime(resultset.getString("available_end_time"));
		String dowStr = resultset.getString("days_of_week"); // assume value is a semicolon-delimited list of days of the week.
		String[] dow = dowStr.split(";");
		for (String d : dow) {
			hrs.addDaysOfWeek(DaysOfWeek.fromCode(d));
		}
		return hrs;
	}
	
	public static VhDirPrimarySource getPrimarySource(ResultSet resultset, Connection connection) throws SQLException {
		VhDirPrimarySource ps = new VhDirPrimarySource();
		int psId = resultset.getInt("primary_source_id");
		ps.setId(resultset.getString("primary_source_id"));
		ps.setCanPushUpdates(getCodeableConcept(resultset.getInt("can_push_updates_cc_id"),connection));
		if (connection != null) {
			// Gather the communication method codeable concepts for this primary source
			ResultSet sqlResultset = DatabaseUtil.runQuery(connection,"SELECT * FROM fhir_codeable_concept WHERE primary_source_communication_method_id = ?",psId);
			while(sqlResultset.next()) {
				CodeableConcept cc = ResourceFactory.getCodeableConcept(sqlResultset);
				ps.addCommunicationMethod(cc);
			}
			// Gather the Availalbe Push Type codeable concepts for this primary source
  		    sqlResultset = DatabaseUtil.runQuery(connection,"SELECT * FROM fhir_codeable_concept WHERE primary_source_push_type_available_id = ?",psId);
			while(sqlResultset.next()) {
				CodeableConcept cc = ResourceFactory.getCodeableConcept(sqlResultset);
				ps.addPushTypeAvailable(cc);
			}
			// Gather the  Type codeable concepts for this primary source
  		    sqlResultset = DatabaseUtil.runQuery(connection,"SELECT * FROM fhir_codeable_concept WHERE primary_source_type_id = ?",psId);
			while(sqlResultset.next()) {
				CodeableConcept cc = ResourceFactory.getCodeableConcept(sqlResultset);
				ps.addType(cc);
			}

		}

		return ps;
	}

	public static PractitionerQualificationComponent getQualification(ResultSet resultset, Connection connection) throws SQLException {
		PractitionerQualificationComponent qu = new PractitionerQualificationComponent();
		qu.setCode(getCodeableConcept(resultset.getInt("code_cc_id"),connection));
		qu.setPeriod(makePeriod(resultset.getDate("period_start"),resultset.getDate("period_end")));
		qu.setIssuer(getResourceReference(resultset.getInt("issuing_organization_id"),connection));
	    ResultSet identifierResults = DatabaseUtil.runQuery(connection, "SELECT * from identifier where qualification_id = ?", resultset.getInt("qualification_id"));
		while(identifierResults.next()) {
			VhDirIdentifier identifier = getIdentifier(identifierResults.getInt("identifier_id"), connection);
			qu.addIdentifier(identifier);
		}

		// TODO add qualification history
		return qu;
	}

	public static VerificationResultAttestationComponent getAttestation(ResultSet resultset, Connection connection) throws SQLException {
		VerificationResultAttestationComponent att = new VerificationResultAttestationComponent();
		att.setId(resultset.getString("attestation_id"));
		att.setDate(resultset.getDate("date"));
		att.setSourceIdentityCertificate(resultset.getString("source_identity_certificate"));
		att.setProxyIdentityCertificate(resultset.getString("proxy_identity_certificate"));
		Signature sig = makeSignature("signatureType", "mimeType"); // TODO signatures not modeled yet
		//TODO Signature not modeled in db yet.sig.set
		att.setSourceSignature(sig);
		att.setProxySignature(sig);
		if (connection != null) {
			att.setWho(getResourceReference(resultset.getInt("who_resource_reference_id"),connection));
			att.setOnBehalfOf(getResourceReference(resultset.getInt("on_behalf_of_resource_reference_id"),connection));
			att.setCommunicationMethod(getCodeableConcept(resultset.getInt("communication_method_cc_id"),connection));
		}
		return att;
		
	}

	public static VerificationResultValidatorComponent getValidator(ResultSet resultset, Connection connection) throws SQLException {
		VerificationResultValidatorComponent validator = new VerificationResultValidatorComponent();
		validator.setId(resultset.getString("validator_id"));
		validator.setIdentityCertificate(resultset.getString("identity_certificate"));
		Signature sig = makeSignature("signatureType", "mimeType"); // TODO signatures not modeled yet
		validator.setAttestationSignature(sig);
		if (connection != null) {
			validator.setOrganization(getResourceReference(resultset.getInt("organization_id"),connection));
		}
		return validator;
		
	}

	public static VhDirNote getNote(ResultSet resultset) throws SQLException {
		VhDirNote note = new VhDirNote();
		// TODO The 'note' model maintains a practitioner_id pointing to a VhDirPractitioner. This should really be a resource reference
		ErrorReport.writeInfo("VhDirNote", resultset.getString("note_id"), "The 'note' model maintains a practitioner_id pointing to a VhDirPractitioner. This should really be a resource reference", "ResourceFactory.getNote");
		note.setId(resultset.getString("note_id"));
		note.setAuthor(makeResourceReference(resultset.getString("practitioner_id"),"VhDirPractioner", null, "Author"));
		note.setTime(resultset.getDate("time"));
		note.setText(resultset.getString("text"));
		return note;
	}


	public static CareTeamParticipantComponent getParticipantComponent(ResultSet resultset, Connection connection) throws SQLException {
		CareTeamParticipantComponent par = new CareTeamParticipantComponent();
		par.setId(resultset.getString("participant_id"));
		par.setPeriod(makePeriod(resultset.getDate("period_start"),resultset.getDate("period_end")));
		par.setMember(getResourceReference(resultset.getInt("member"),connection));
		ResultSet codeableconcepts = DatabaseUtil.runQuery(connection,"Select * from fhir_codeable_concept where careteam_participant_role_id = ?", resultset.getInt("participant_id"));
		while (codeableconcepts.next()) {
			par.addRole(getCodeableConcept(codeableconcepts));
		}
		return par;
	}

	public static HealthcareServiceEligibilityComponent getEligibility(ResultSet resultset, Connection connection) throws SQLException {
		HealthcareServiceEligibilityComponent ec = new HealthcareServiceEligibilityComponent();
		ec.setId(resultset.getString("eligibility_id"));
		ec.setCode(getCodeableConcept(resultset.getInt("code_cc_id"),connection));
		ec.setComment(resultset.getString("comment"));
		return ec;
	}


	///////////////   MAKE METHODS  ////////////////////////////////////////////////////////////////
    // Make methods are those methods that create resources from data parameters passed into them.
	// They are not created from data queried from the database.
	////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	 * Create a Coding object from the input parameters
	 * 
	 * @param code
	 * @param display
	 * @param system
	 * @param userSelected
	 * @return Coding
	 */
	static public Coding makeCoding(String code, String display, String system, boolean userSelected ) {
		Coding cd = new Coding();
		cd.setCode(code);
		cd.setDisplay(display);
		cd.setSystem(system);
		cd.setUserSelected(userSelected);
		return cd;
	}
	
	/**
	 * Create a Period object from the given parmeters.
	 * @param startDatetime
	 * @param endDatetime
	 * @return Period
	 */
	static public Period makePeriod(Date startDatetime, Date endDatetime) {
		Period per = new Period();
		try {
			per.setStart(startDatetime);
			per.setEnd(endDatetime);
		}
		catch (Exception e) {
			per.setUserData("Error","Invalid date for start or end encountered.");
			ErrorReport.writeWarning("Period", "", String.format("Error in ResourceFactory.makePeriod(%s,%s)",startDatetime, endDatetime), e.getMessage());

		}
		return per;
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

	static public Signature makeSignature(String type, String targetFormat ) {
		Signature sig = new Signature();
		Date inst = new Date();
		sig.setWhen(inst);
		List<Coding> typeList = new ArrayList<Coding>();
		try {
			Coding code = makeCoding(type, type, "http://hl7.org/fhir/ValueSet/signature-type", false);
			code.setCode(type);
			typeList.add(code);
		}
		catch (Exception e) {
			ErrorReport.writeWarning("Signature", "", String.format("Error in ResourceFactory.makeSignature(type;%s, targetFormat:%s)",type,targetFormat), e.getMessage());
		}
		sig.setType(typeList);
		sig.setWho(makeResourceReference("1","VhDirPractitioner",null,""));
		sig.setTargetFormat(targetFormat);
		return sig;
	}
}
