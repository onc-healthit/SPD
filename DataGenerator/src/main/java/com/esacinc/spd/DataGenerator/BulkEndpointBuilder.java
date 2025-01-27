package com.esacinc.spd.DataGenerator;

import com.esacinc.spd.model.VhDirEndpoint;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.model.VhDirTelecom;
import com.esacinc.spd.model.complex_extensions.IEndpointUseCase;
import com.esacinc.spd.util.*;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Endpoint.EndpointStatus;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Reference;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BulkEndpointBuilder implements IEndpointUseCase {
	
	
	/**
	 * uses the connection provided to get all Endpoints and then builds a list
	 * of Endpoint objects to return
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<VhDirEndpoint> getEndpoints(Connection connection) throws SQLException, ParseException {
		int cnt = 0;
		List<VhDirEndpoint> endpoints = new ArrayList<VhDirEndpoint>();
		String limit = (DatabaseUtil.GLOBAL_LIMIT > 0) ? " LIMIT " +DatabaseUtil.GLOBAL_LIMIT : "";

		ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_endpoint WHERE endpoint_id > " + BulkDataApp.FROM_ID_ENDPOINTS + " ORDER BY endpoint_id " + limit,null);
		while (resultSet.next() && BulkDataApp.okToProceed(cnt)) {
			//System.out.println("Creating location for id " + resultSet.getInt("location_id"));
			VhDirEndpoint ep = new VhDirEndpoint();
		
			// set the id
			int epId = resultSet.getInt("endpoint_id");
			ep.setId(resultSet.getString("endpoint_id"));
			ErrorReport.setCursor("VhDirEndpoint", ep.getId());

			ep.setText(ResourceFactory.makeNarrative("Endpoint (id: " + epId + ")"));

			ep.setName(resultSet.getString("name"));
			ep.setAddress(resultSet.getString("address"));
			// Minor issue....connection type must be camel cased, sort of.
			String display = resultSet.getString("connectionType");
		    if ("direct-project".equalsIgnoreCase(display)) {
		    	display = "Direct Project";
		    }
			ep.setConnectionType(ResourceFactory.makeCoding(resultSet.getString("connectionType"),display,"http://terminology.hl7.org/CodeSystem/endpoint-connection-type",false));
			IntegerType rank = new IntegerType(resultSet.getInt("rank"));
			// Must have at least one rank
			if (rank == null || rank.equals(0)) {
				rank.setValue(1);
			}
			ep.setRank(rank);
			
			ep.setManagingOrganization(ResourceFactory.makeResourceReference(resultSet.getString("managing_organization_id"), "Organization", null, "Managing Organization"));
			ep.setPeriod(ResourceFactory.makePeriod(resultSet.getDate("period_start"),resultSet.getDate("period_end"))); 

			// Add a digital certificate to the first 3 endpoints
			int certCount = 0;
			if (certCount < DigitalCertificateFactory.MAX_CERTS) {
				// args are:  nthCert, type, use, trustFramework, standard, expirationDate
				ep.addDigitalcertficate(DigitalCertificateFactory.makeDigitalCertificate(certCount++, "role", "auth", "other", "x.509v3", null));
			}
			
			

			// Handle the status code
			handleStatus(resultSet,ep);

			handleMimeTypes(resultSet, ep);
			
			handlePayloadTypes(connection, ep, epId);
			
			handleHeaders(resultSet, ep);
			
			// Handle the identifiers
			handleIdentifiers(connection, ep, epId);
			
			handleUseCases(connection, ep, epId);
			
           // Handle the contact
         	handleContact(connection, ep, epId);
         	
			// Handle the restrictions
         	handleRestrictions(connection, ep, epId);

         	endpoints.add(ep);
         	
         	cnt++;
		}

		System.out.println("Made " + endpoints.size() + " endpoints");
		return endpoints;
	}



	/**
	 * Handles a status  for enpoints
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleStatus(ResultSet resultset, VhDirEndpoint ep){
		try {
			ep.setStatus(EndpointStatus.fromCode(resultset.getString("status")));
		}
		catch (Exception e) {
			// Probably means status was not found in LocationStatus
			ep.setStatus(EndpointStatus.NULL);
			ErrorReport.writeWarning("VhDirEndpoint", ep.getId(), "Unrecognized status", e.getMessage());
		}
	}

	private void handleMimeTypes(ResultSet resultset, VhDirEndpoint ep) throws SQLException{
		String mimestr = resultset.getString("payload_mime_type"); // assume a semicolon-delimited list of mime types
		if (mimestr == null) {
			return;
		}
		String [] mimes =  mimestr.split(";");
		for (String mime : mimes) {
			ep.addPayloadMimeType(mime); 
		}
	}

	private void handlePayloadTypes(Connection connection, VhDirEndpoint ep, int epId) throws SQLException{
		ResultSet resultset = DatabaseUtil.runQuery(connection, "Select * from fhir_codeable_concept where endpoint_payload_type_id = ?", epId);
		while (resultset.next()) {
			CodeableConcept cc = ResourceFactory.getCodeableConcept(resultset);
			ep.addPayloadType(cc);
		}
	}

	private void handleHeaders(ResultSet resultset, VhDirEndpoint ep) throws SQLException{
		String headerstr = resultset.getString("header"); // assume a semicolon-delimited list of headers
		if (headerstr == null) {
			return;
		}
		String [] headers =  headerstr.split(";");
		for (String header : headers) {
			ep.addHeader(header);  
		}
	}


	/**
	 * Handles all the elements of the identifiers for Locations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleIdentifiers(Connection connection, VhDirEndpoint ep, int epId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from identifier where endpoint_identifier_id = ?", epId);
		while(resultset.next()) {
			VhDirIdentifier identifier = ResourceFactory.getIdentifier(resultset);
			ep.addIdentifier(identifier);
		}
	}
	
	/**
	 * Handles all the use cases for Locations
	 * 
	 * @param connection
	 * @param org
	 * @param orgId
	 * @throws SQLException
	 */
	private void handleUseCases(Connection connection, VhDirEndpoint ep, int epId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from use_case where endpoint_id = ?", epId);
		while(resultset.next()) {
			VhDirEndpointUseCase uc = ResourceFactory.getEndpointUseCase(resultset.getString("use_case_id"), connection);
			ep.addEndpointUsecase(uc);
		}
	}
	
	/**
	 * Handles the contact for the endpoint id passed in.
	 * Note that VhDirEndpoints have a telecom, but it is called a contact, unlike all the other profiles in the IG
	 * 
	 * @param connection
	 * @param ep
	 * @param epId
	 * @throws SQLException
	 */
	private void handleContact(Connection connection, VhDirEndpoint ep, int epId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from telecom where telecom_id = ?", epId);
		while(resultset.next()) {
			VhDirTelecom contact = ContactFactory.getTelecom(resultset, connection);
			if (!contact.hasAvailableTime()) {
				// Add  any day, any time available time for this telecom contact point
				contact.addAvailableTime(ContactFactory.makeAvailableTime("sun;mon;tue;wed;thu;fri;sat", true, "", ""));
			}
			ep.addContact(contact);
			return; // We expect only one.
		}
	}
	
	
	
	/**
	 * Handle the restrictions associated with the practitioner 
	 * @param connection
	 * @param prac
	 * @param epId
	 * @throws SQLException
	 */
	private void handleRestrictions(Connection connection, VhDirEndpoint ep, int epId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from resource_reference where endpoint_restriction_id = ?", epId);
		while(resultset.next()) {
			Reference ref = ResourceFactory.getResourceReference(resultset,connection);
			ep.addUsageRestriction(ref);
		}
	}

}
