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
	private static boolean DO_ALL = true;  
	private static boolean DO_ORGANIZATIONS = false;
	private static boolean DO_PRACTITIONERS = false;
	private static boolean DO_NETWORKS = false;
	private static boolean DO_LOCATIONS = false;
	private static boolean DO_VALIDATIONS = false;
	private static boolean DO_ENDPOINTS = true;
	private static boolean DO_GEOTEST = false;

	// Which VhDir resource files to generate...
	private static String FILE_ORGANIZATIONS = "Organization.ndjson";
	private static String FILE_PRACTITIONERS = "Practitioner.ndjson";
	private static String FILE_NETWORKS = "Network.ndjson";
	private static String FILE_LOCATIONS = "Location.ndjson";
	private static String FILE_VALIDATIONS = "Validation.ndjson";
	private static String FILE_ENDPOINTS = "Endpoint.ndjson";

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
				outputOrganizationList(organizations, FILE_ORGANIZATIONS,0);  // last arg indicates prettyPrint nth org. Use -1 to skip
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
				outputPractitionerList(practitioners, FILE_PRACTITIONERS, 0); // last arg indicates prettyPrint nth prac. Use -1 to skip
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
				outputNetworkList(networks, FILE_NETWORKS, 0); // last arg indicates prettyPrint nth network. Use -1 to skip
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
				outputLocationList(locations, FILE_LOCATIONS,0);  // last arg indicates prettyPrint nth org. Use -1 to skip
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
				outputValidationList(validations, FILE_VALIDATIONS,0);  // last arg indicates prettyPrint nth org. Use -1 to skip
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
				outputEndpointList(endpoints, FILE_ENDPOINTS,0);  // last arg indicates prettyPrint nth org. Use -1 to skip
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

	private static void outputValidationList(List<VhDirValidation>validations, String filename, int prettyPrintNth) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (VhDirValidation val : validations) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}
				
				String nwJson = jsonParser.encodeResourceToString(val);
				writer.write(nwJson);
				writer.write("\n");
				
				// String above is appropriate for ndjson output but for now here is a pretty version
				if (cnt == prettyPrintNth) {
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					JsonParser jp = new JsonParser();
					JsonElement je = jp.parse(nwJson);
					String prettyJsonString = gson.toJson(je);
					System.out.println("\n------------------------------- VALIDATION ------------------------------------\n");
					System.out.println(prettyJsonString);
				}
				cnt++;
			}
			writer.close();
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

	private static void outputEndpointList(List<VhDirEndpoint>endpoints, String filename, int prettyPrintNth) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (VhDirEndpoint ep : endpoints) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}
				
				String nwJson = jsonParser.encodeResourceToString(ep);
				writer.write(nwJson);
				writer.write("\n");
				
				// String above is appropriate for ndjson output but for now here is a pretty version
				if (cnt == prettyPrintNth) {
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					JsonParser jp = new JsonParser();
					JsonElement je = jp.parse(nwJson);
					String prettyJsonString = gson.toJson(je);
					System.out.println("\n------------------------------- ENDPOINT ------------------------------------\n");
					System.out.println(prettyJsonString);
				}
				cnt++;
			}
			writer.close();
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

}
