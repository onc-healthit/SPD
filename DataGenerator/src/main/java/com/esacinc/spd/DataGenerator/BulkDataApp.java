package com.esacinc.spd.DataGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.VerificationResult.VerificationResultAttestationComponent;

import com.esacinc.spd.model.VhDirEndpoint;
import com.esacinc.spd.model.VhDirLocation;
import com.esacinc.spd.model.VhDirNetwork;
import com.esacinc.spd.model.VhDirOrganization;
import com.esacinc.spd.model.VhDirPractitioner;
import com.esacinc.spd.model.VhDirValidation;
import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.Geocoding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

public class BulkDataApp {

	// Database connection and querying are handled in DatabaseUtils.java
	
	// Which VhDir resources to generate...
	private static boolean DO_ALL = false;  
	private static boolean DO_ORGANIZATIONS = true;
	private static boolean DO_PRACTITIONERS = true;
	private static boolean DO_NETWORKS = false;
	private static boolean DO_LOCATIONS = false;
	private static boolean DO_VALIDATIONS = false;
	private static boolean DO_ENDPOINTS = false;
	private static boolean DO_GEOTEST = false;

	// Which VhDir resource files to generate...
	private static String FILE_ORGANIZATIONS = "Organization.ndjson";
	private static String FILE_PRACTITIONERS = "Practitioner.ndjson";
	private static String FILE_NETWORKS = "Network.ndjson";
	private static String FILE_LOCATIONS = "Location.ndjson";
	private static String FILE_VALIDATIONS = "Validation.ndjson";
	private static String FILE_ENDPOINTS = "Endpoint.ndjson";

	// Which VhDir resource pretty-printed files to generate...
	// (Set to null or "" to not generate a file.)
	private static String FILE_ORGANIZATIONS_PP = "Organization_PP.json";
	private static String FILE_PRACTITIONERS_PP = "Practitioner_PP.json";
	private static String FILE_NETWORKS_PP = "Network_PP.json";
	private static String FILE_LOCATIONS_PP = "Location_PP.json";
	private static String FILE_VALIDATIONS_PP = "Validation_PP.json";
	private static String FILE_ENDPOINTS_PP = "Endpoint_PP.json";
	private static int    MAX_PP_ENTRIES = 10;   // Number of resources to put in the pretty print file. -1 means all
	private static int    PP_NTH_CONSOLE = 0;    // Indicates prettyPrint nth item to System.output. Use -1 to skip

	// Control how many entries we process in each section and output. -1 means ALL.
	private static int MAX_ENTRIES = -1;  
	
	public static void main(String[] args) {
		
		// Testing some geocode stuff.
		if (DO_GEOTEST)
		{
			try {
				Geocoding.geocodePostalCode("46224", null); // We know this is valid;
				Geocoding.geocodePostalCode("096030300", null);
				Geocoding.geocodePostalCode("96297", null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// Open a connection to the database that we will use throughout.
		Connection connection = DatabaseUtil.getConnection();
		if (connection == null) {
			return;
		}
		
		
		if (DO_ALL || DO_ORGANIZATIONS) {
			try{
				// Get and write Organizations
				System.out.println("Generate Organization resources...");
				BulkOrganizationBuilder orgBuilder = new BulkOrganizationBuilder();
				List<VhDirOrganization> organizations = orgBuilder.getOrganizations(connection);
				outputOrganizationList(organizations);  
			}	
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		
		if (DO_ALL || DO_PRACTITIONERS) {
			try {
				// Get and write Practitioners
				System.out.println("Generate Practitioner resources...");
	
				BulkPractitionerBuilder pracBuilder = new BulkPractitionerBuilder();
				List<VhDirPractitioner> practitioners = pracBuilder.getPractitioners(connection);
				outputPractitionerList(practitioners); 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			
		}
		
		if (DO_ALL || DO_NETWORKS) {
			try {
				// Get and write Networks
				System.out.println("Generate Network resources...");
	
				BulkNetworkBuilder nwBuilder = new BulkNetworkBuilder();
				List<VhDirNetwork> networks = nwBuilder.getNetworks(connection);
				outputNetworkList(networks); 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			
		}

		if (DO_ALL || DO_LOCATIONS) {
			try{
				// Get and write Locations
				System.out.println("Generate Location resources...");
				BulkLocationBuilder locBuilder = new BulkLocationBuilder();
				List<VhDirLocation> locations = locBuilder.getLocations(connection);
				outputLocationList(locations);  
			}	
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}

		if (DO_ALL || DO_VALIDATIONS) {
			try{
				// Get and write Validations
				System.out.println("Generate Validation resources...");
				BulkValidationBuilder valBuilder = new BulkValidationBuilder();
				List<VhDirValidation> validations = valBuilder.getValidations(connection);
				outputValidationList(validations);  
			}	
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}

		if (DO_ALL || DO_ENDPOINTS) {
			try{
				// Get and write Endpoints
				System.out.println("Generate Endpoint resources...");
				BulkEndpointBuilder epBuilder = new BulkEndpointBuilder();
				List<VhDirEndpoint> endpoints = epBuilder.getEndpoints(connection);
				outputEndpointList(endpoints);  
			}	
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}

		System.out.println("\n\nFHIR Resource generation complete");

	}

	private static void outputOrganizationList(List<VhDirOrganization>organizations) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_ORGANIZATIONS));
			BufferedWriter pp_writer = null;
			if (FILE_ORGANIZATIONS_PP != null &&  !FILE_ORGANIZATIONS_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_ORGANIZATIONS_PP));
			}
			int cnt = 0;
			for (VhDirOrganization org : organizations) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(org);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "ORGANIZATION");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}

		}
		catch (IOException e) {
			System.err.println("IO EXCEPTION writing organization list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			System.err.println("NULL POINTER EXCEPTION writing organization list: " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	private static void outputPractitionerList(List<VhDirPractitioner>practitioners) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PRACTITIONERS));
			BufferedWriter pp_writer = null;
			if (FILE_PRACTITIONERS_PP != null &&  !FILE_PRACTITIONERS_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_PRACTITIONERS_PP));
			}
			for (VhDirPractitioner prac : practitioners) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(prac);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "PRACTITIONER");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}

		}
		catch (IOException e) {
			System.err.println("EXCEPTION writing practitioner list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			System.err.println("NULL POINTER EXCEPTION writing practitioner list: " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	private static void outputNetworkList(List<VhDirNetwork>networks) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NETWORKS));
			BufferedWriter pp_writer = null;
			if (FILE_NETWORKS_PP != null &&  !FILE_NETWORKS_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_NETWORKS_PP));
			}
			for (VhDirNetwork nw : networks) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(nw);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "NETWORK");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}

		}
		catch (IOException e) {
			System.err.println("EXCEPTION writing network list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			System.err.println("NULL POINTER EXCEPTION writing network list: " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	private static void outputLocationList(List<VhDirLocation>locations) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_LOCATIONS));
			BufferedWriter pp_writer = null;
			if (FILE_LOCATIONS_PP != null &&  !FILE_LOCATIONS_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_LOCATIONS_PP));
			}
			for (VhDirLocation loc : locations) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(loc);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "LOCATION");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}

		}
		catch (IOException e) {
			System.err.println("EXCEPTION writing location list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			System.err.println("NULL POINTER EXCEPTION writing location list: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private static void outputValidationList(List<VhDirValidation>validations) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_VALIDATIONS));
			BufferedWriter pp_writer = null;
			if (FILE_VALIDATIONS_PP != null &&  !FILE_VALIDATIONS_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_VALIDATIONS_PP));
			}
			for (VhDirValidation val : validations) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(val);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "VALIDATION");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}

		}
		catch (IOException e) {
			System.err.println("EXCEPTION writing validation list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			System.err.println("NULL POINTER EXCEPTION writing validation list: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private static void outputEndpointList(List<VhDirEndpoint>endpoints) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_ENDPOINTS));
			BufferedWriter pp_writer = null;
			if (FILE_ENDPOINTS_PP != null &&  !FILE_ENDPOINTS_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_ENDPOINTS_PP));
			}
			for (VhDirEndpoint ep : endpoints) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(ep);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "ENDPOINT");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}
		}
		catch (IOException e) {
			System.err.println("EXCEPTION writing endpoint list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			System.err.println("NULL POINTER EXCEPTION writing endpoint list: " + e.getMessage());
			e.printStackTrace();
		}

	}

	// --------------------------------------------------------------------------------------------------------------------
	// Utility functions for pretty printing 
	// --------------------------------------------------------------------------------------------------------------------

	/**
	 * If given count == global PP_NTH_CONSOLE, then output the pretty print json string to the console.
	 * 
	 * @param prettyJsonStr
	 * @param cnt
	 * @param title
	 */
	private static void maybePrettyPrintToConsole(String prettyJsonStr, int cnt, String title) {
		if (prettyJsonStr != null && cnt == PP_NTH_CONSOLE) {
			if (title != null) {
				System.out.println("\n------------------------------- " + title + " ------------------------------------\n");
			}
			System.out.println(prettyJsonStr);
		}
	}

	/**
	 * 	If given count < global PP_MAX_ENTRES, then convert the given jsonStr to a pretty print version, and
	 *  output it to the given file writer.
	 *  Return the pretty print version of the given json string, or null
     *
	 * @param outFile
	 * @param jsonStr
	 * @param cnt
	 * @return
	 * @throws IOException
	 */
	
	private static String maybePrettyPrintToFile(BufferedWriter outFile, String jsonStr, int cnt) throws IOException{
		String prettyJsonString = null;
		// If we are under the max entries limit OR if we have landed on the nth item to print to console,
		// then we at least need to generate the pretty print version to return.
		// (We have to have the cnt == PP_NTH test in case we are wanting to print to console an item that is
		//  beyond the max entries limit.)
		if (cnt < MAX_PP_ENTRIES || cnt == PP_NTH_CONSOLE) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(jsonStr);
			prettyJsonString = gson.toJson(je);
			// If we are under the max entry count, then write to the given file....
			if (cnt < MAX_PP_ENTRIES) {
				outFile.write(prettyJsonString);
				outFile.write("\n");
			}
		}
		return prettyJsonString;
	}
}
