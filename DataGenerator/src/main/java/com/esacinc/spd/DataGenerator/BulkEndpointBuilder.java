package com.esacinc.spd.DataGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Endpoint.EndpointStatus;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Reference;

import com.esacinc.spd.model.VhDirContactPoint;
import com.esacinc.spd.model.VhDirEndpoint;
import com.esacinc.spd.model.VhDirEndpointUseCase;
import com.esacinc.spd.model.VhDirIdentifier;
import com.esacinc.spd.util.ContactFactory;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.DigitalCertificateFactory;
import com.esacinc.spd.util.ResourceFactory;

public class BulkEndpointBuilder {
	
	
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
        ResultSet resultSet = DatabaseUtil.runQuery(connection, "SELECT * FROM vhdir_endpoint", null);
		while (resultSet.next() && cnt < BulkDataApp.MAX_ENTRIES) {
			//System.out.println("Creating location for id " + resultSet.getInt("location_id"));
			VhDirEndpoint ep = new VhDirEndpoint();
		
			// set the id
			int epId = resultSet.getInt("location_id");

			ep.setId(resultSet.getString("location_id"));
			ep.setName(resultSet.getString("name"));
			ep.setAddress(resultSet.getString("address"));
			ep.setConnectionType(ResourceFactory.makeCoding(resultSet.getString("connectionType"),resultSet.getString("connectionType"),"Connection Type", false));
			ep.setRank(new IntegerType(resultSet.getInt("rank")));
			ep.setManagingOrganization(ResourceFactory.getResourceReference(resultSet.getInt("managing_organization_id"), connection));
			ep.setPeriod(ResourceFactory.makePeriod(resultSet.getDate("period_start"),resultSet.getDate("period_end"))); 
			ep.setManagingOrganization(ResourceFactory.getResourceReference(resultSet.getInt("managing_organization_id"), connection));

			// Add a digital certificate to the first 3 endpoints
			int certCount = 0;
			if (certCount < DigitalCertificateFactory.MAX_CERTS) {
				// args are:  nthCert, type, use, trustFramework, standard, expirationDate
				ep.addDigitalcertficate(DigitalCertificateFactory.makeDigitalCertificate(certCount++, "role", "auth", "other", "x.509v3", null));
			}

			// Handle the status code
			handleStatus(resultSet,ep);

			handleMimeTypes(resultSet, ep);
			
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
	 * Handles the contact for the endpoint id passed in
	 * 
	 * @param connection
	 * @param ep
	 * @param epId
	 * @throws SQLException
	 */
	private void handleContact(Connection connection, VhDirEndpoint ep, int epId) throws SQLException {
		ResultSet resultset = DatabaseUtil.runQuery(connection, "SELECT * from endpoint_contact where endpoint_contact_id = ?", epId);
		while(resultset.next()) {
				VhDirContactPoint contact = ContactFactory.getEndpointContact(resultset, connection);
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
			Reference ref = ResourceFactory.getResourceReference(resultset.getInt("resource_reference_id"),connection);
			ep.addUsageRestriction(ref);
		}
	}

}
