package com.esacinc.spd.DataGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import com.esacinc.spd.model.VhDirLocation;
import com.esacinc.spd.model.VhDirNetwork;
import com.esacinc.spd.model.VhDirOrganization;
import com.esacinc.spd.model.VhDirPractitioner;
import com.esacinc.spd.util.Geocoding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

public class BulkDataApp {

	// Database credentials...
	public static String dbUsername = "spduser";
	public static String dbPassword = "SpdUs3r45";
	
	// Which schema to read data from....

	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_scrubbed";
	public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_small";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_small_scrubbed";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_medium";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_medium_scrubbed";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_large";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_larged_scrubbed";
	
	// Which VhDir resources to generate...
	private static boolean DO_ORGANIZATIONS = false;
	private static boolean DO_PRACTITIONERS = false;
	private static boolean DO_NETWORKS = false;
	private static boolean DO_LOCATIONS = true;
	
	private static int MAX_ENTRIES = -1;  // Control how many entries we process in each section and output. -1 means ALL.
	
	public static void main(String[] args) {		
		Connection connection = null;
		
		// Testing some geocode stuff.
		if (!DO_ORGANIZATIONS && !DO_PRACTITIONERS && !DO_NETWORKS && !DO_LOCATIONS)
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
		
		// Connect to the DB
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(BulkDataApp.connectionUrl,	BulkDataApp.dbUsername, BulkDataApp.dbPassword);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("\nFHIR Resource generation terminated with connection error");
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			System.err.println("\nFHIR Resource generation terminated, mysql driver not found");
			e.printStackTrace();
			return;
		} 
		
		if (DO_ORGANIZATIONS) {
			try{
				// Get and write Organizations
				System.out.println("Generate Organization resources...");
				BulkOrganizationBuilder orgBuilder = new BulkOrganizationBuilder();
				List<VhDirOrganization> organizations = orgBuilder.getOrganizations(connection);
				outputOrganizationList(organizations, "Organization.ndjson",0);  // last arg indicates prettyPrint nth org. Use -1 to skip
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
		
		if (DO_PRACTITIONERS) {
			try {
				// Get and write Practitioners
				System.out.println("Generate Practitioner resources...");
	
				BulkPractitionerBuilder pracBuilder = new BulkPractitionerBuilder();
				List<VhDirPractitioner> practitioners = pracBuilder.getPractitioners(connection);
				outputPractitionerList(practitioners, "Practitioner.ndjson", 0); // last arg indicates prettyPrint nth prac. Use -1 to skip
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			
		}
		
		if (DO_NETWORKS) {
			try {
				// Get and write Networks
				System.out.println("Generate Network resources...");
	
				BulkNetworkBuilder nwBuilder = new BulkNetworkBuilder();
				List<VhDirNetwork> networks = nwBuilder.getNetworks(connection);
				outputNetworkList(networks, "Network.ndjson", 0); // last arg indicates prettyPrint nth network. Use -1 to skip
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			
		}

		if (DO_LOCATIONS) {
			try{
				// Get and write Locations
				System.out.println("Generate Location resources...");
				BulkLocationBuilder locBuilder = new BulkLocationBuilder();
				List<VhDirLocation> locations = locBuilder.getLocations(connection);
				outputLocationList(locations, "Locations.ndjson",0);  // last arg indicates prettyPrint nth org. Use -1 to skip
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

		System.out.println("\nFHIR Resource generation complete");

	}

	private static void outputOrganizationList(List<VhDirOrganization>organizations, String filename, int prettyPrintNth) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			int cnt = 0;
			for (VhDirOrganization org : organizations) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}
				String orgJson = jsonParser.encodeResourceToString(org);
				writer.write(orgJson);
				writer.write("\n");
				
				// String above is appropriate for ndjson output but for now here is a pretty version
				if (cnt == prettyPrintNth) {
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					JsonParser jp = new JsonParser();
					JsonElement je = jp.parse(orgJson);
					String prettyJsonString = gson.toJson(je);
					System.out.println("\n------------------------------- ORGANIZATION ------------------------------------\n");
					System.out.println(prettyJsonString);
				}
				cnt++;
			}
			writer.close();
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
	
	private static void outputPractitionerList(List<VhDirPractitioner>practitioners, String filename, int prettyPrintNth) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (VhDirPractitioner prac : practitioners) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}
				String pracJson = jsonParser.encodeResourceToString(prac);
				writer.write(pracJson);
				writer.write("\n");
				
				// String above is appropriate for ndjson output but for now here is a pretty version
				if (cnt == prettyPrintNth) {
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					JsonParser jp = new JsonParser();
					JsonElement je = jp.parse(pracJson);
					String prettyJsonString = gson.toJson(je);
					System.out.println("\n------------------------------- PRACTITIONER ------------------------------------\n");
					System.out.println(prettyJsonString);
				}
				cnt++;
			}
			writer.close();
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
	
	private static void outputNetworkList(List<VhDirNetwork>networks, String filename, int prettyPrintNth) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (VhDirNetwork nw : networks) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}
				String nwJson = jsonParser.encodeResourceToString(nw);
				writer.write(nwJson);
				writer.write("\n");
				
				// String above is appropriate for ndjson output but for now here is a pretty version
				if (cnt == prettyPrintNth) {
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					JsonParser jp = new JsonParser();
					JsonElement je = jp.parse(nwJson);
					String prettyJsonString = gson.toJson(je);
					System.out.println("\n------------------------------- NETWORK ------------------------------------\n");
					System.out.println(prettyJsonString);
				}
				cnt++;
			}
			writer.close();
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
	
	private static void outputLocationList(List<VhDirLocation>locations, String filename, int prettyPrintNth) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (VhDirLocation loc : locations) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}
				String nwJson = jsonParser.encodeResourceToString(loc);
				writer.write(nwJson);
				writer.write("\n");
				
				// String above is appropriate for ndjson output but for now here is a pretty version
				if (cnt == prettyPrintNth) {
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					JsonParser jp = new JsonParser();
					JsonElement je = jp.parse(nwJson);
					String prettyJsonString = gson.toJson(je);
					System.out.println("\n------------------------------- LOCATION ------------------------------------\n");
					System.out.println(prettyJsonString);
				}
				cnt++;
			}
			writer.close();
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

}
