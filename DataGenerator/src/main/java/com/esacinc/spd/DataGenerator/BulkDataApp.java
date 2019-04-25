package com.esacinc.spd.DataGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import com.esacinc.spd.model.VhDirOrganization;
import com.esacinc.spd.model.VhDirPractitioner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

public class BulkDataApp {

	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_scrubbed";
	public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_small";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_small_scrubbed";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_medium";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_medium_scrubbed";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_large";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_larged_scrubbed";
	
	public static String dbUsername = "spduser";
	public static String dbPassword = "SpdUs3r45";
	
	public static void main(String[] args) {		
		Connection connection = null;
		IParser jsonParser;
		
		// Create the JSON Parser for later use
		FhirContext ctx = FhirContext.forR4();
		jsonParser = ctx.newJsonParser();
		
		// Connect to the DB
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(BulkDataApp.connectionUrl,
					BulkDataApp.dbUsername, BulkDataApp.dbPassword);
	        
			// Get Organizations
			BulkOrganizationBuilder orgBuilder = new BulkOrganizationBuilder();
			List<VhDirOrganization> organizations = orgBuilder.getOrganizations(connection);
			
			// Get Practitioners
			BulkPractitionerBuilder pracBuilder = new BulkPractitionerBuilder();
			List<VhDirPractitioner> practitioners = pracBuilder.getPractitioners(connection);
			
			// Now iterate through writing to the ndjson file
			BufferedWriter writer = new BufferedWriter(new FileWriter("Organization.ndjson"));
			for (VhDirOrganization org : organizations) {
				String orgJson = jsonParser.encodeResourceToString(org);
				writer.write(orgJson);
				writer.write("\n");
				
				// String above is appropriate for ndjson output but for now here is a pretty version
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonParser jp = new JsonParser();
				JsonElement je = jp.parse(orgJson);
				String prettyJsonString = gson.toJson(je);
				System.out.println(prettyJsonString);
			}
			System.out.println("---------------------------------------------------------------------------------");
			for (VhDirPractitioner prac : practitioners) {
				String pracJson = jsonParser.encodeResourceToString(prac);
				writer.write(pracJson);
				writer.write("\n");
				
				// String above is appropriate for ndjson output but for now here is a pretty version
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonParser jp = new JsonParser();
				JsonElement je = jp.parse(pracJson);
				String prettyJsonString = gson.toJson(je);
				System.out.println(prettyJsonString);
			}

			writer.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
