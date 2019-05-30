package com.esacinc.spd.DataGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import com.esacinc.spd.model.VhDirCareTeam;
import com.esacinc.spd.model.VhDirEndpoint;
import com.esacinc.spd.model.VhDirHealthcareService;
import com.esacinc.spd.model.VhDirInsurancePlan;
import com.esacinc.spd.model.VhDirLocation;
import com.esacinc.spd.model.VhDirNetwork;
import com.esacinc.spd.model.VhDirOrganization;
import com.esacinc.spd.model.VhDirOrganizationAffiliation;
import com.esacinc.spd.model.VhDirPractitioner;
import com.esacinc.spd.model.VhDirRestriction;
import com.esacinc.spd.model.VhDirValidation;
import com.esacinc.spd.model.VhDirCareTeam;
//import com.esacinc.spd.model.VhDirInsurancePlan;
//import com.esacinc.spd.model.VhDirHealthcareService;
//import com.esacinc.spd.model.VhDirOrganizationAffiliation;
import com.esacinc.spd.model.VhDirPractitionerRole;
//import com.esacinc.spd.model.VhDirRestriction;

import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.ErrorReport;
import com.esacinc.spd.util.Geocoding;
import com.esacinc.spd.util.ProgressTimer;
import com.esacinc.spd.util.PropertiesUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

public class BulkDataApp extends BuildControlSettings {

    // See BuildControlSettings.java for definitions of all the static variables and other functions used throughout.
	
	public static void main(String[] args) {
		
		ProgressTimer timer = new ProgressTimer(false);
		
		maybeReInitFromProperties(); // in BuildControlSettings
		
		// Open the error report file, and write some bookkeeping info
		if (DO_REPORTING) {
			ErrorReport.open();
		}
		if (RE_INITIALIZE) {
			ErrorReport.writeMessage("C","Setup","Control Variables", "", "" ,String.format("Build Control variables re-initialized from properties in file: %s ", PropertiesUtil.PROPERTIES_FILENAME));
		}
		ErrorReport.writeMessage("C","Setup","Control Variables", "", "" ,String.format("Process all resource types: %s, Max Entries: %d, Max Pretty Print Entries: %d, Print Nth of each Resource to console: %d", (DO_ALL)?"Yes":"No", MAX_ENTRIES, MAX_PP_ENTRIES, PP_NTH_CONSOLE));
		ErrorReport.writeMessage("C","Setup","DB Conections", "", "" ,String.format("SPD: %s, ZipCodes: %s, ", DatabaseUtil.connectionUrl, DatabaseUtil.zipConnectionUrl));
		
		// Open connections to the databases that we will use throughout.
		Connection connection = DatabaseUtil.openAllConnections();

		// Testing some geocode stuff.
		if (Geocoding.DO_GEOCODE_TEST) {
			Geocoding.basicTest();
		}
		
		
		
		if (DO_ALL || DO_ORGANIZATIONS) {
			ErrorReport.writeInfo("DO_ORGANIZATIONS","","","");
			try{
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write Organizations
				System.out.println("Generate Organization resources...");
				BulkOrganizationBuilder orgBuilder = new BulkOrganizationBuilder();
				List<VhDirOrganization> organizations = orgBuilder.getOrganizations(connection);
				outputOrganizationList(organizations); 
				ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d Organizations Collected", organizations.size()));
			} 
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}
			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in DO_PRACTITIONERS", e.getMessage());
				e.printStackTrace();
				DatabaseUtil.closeAllConnections(connection);
			} catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "PARSE error in DO_PRACTITIONERS", e.getMessage());
				e.printStackTrace();
			} 			
		}
		
		if (DO_ALL || DO_PRACTITIONERS) {
			ErrorReport.writeInfo("DO_PRACTITIONERS","","","");
			try {
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write Practitioners
				System.out.println("Generate Practitioner resources...");
	
				BulkPractitionerBuilder pracBuilder = new BulkPractitionerBuilder();
				List<VhDirPractitioner> practitioners = pracBuilder.getPractitioners(connection);
				outputPractitionerList(practitioners); 
				ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d Practitioners Collected", practitioners.size()));
			} 
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}
			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in DO_PRACTITIONERS", e.getMessage());
				e.printStackTrace();
				DatabaseUtil.closeAllConnections(connection);
			} catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "PARSE error in DO_PRACTITIONERS", e.getMessage());
				e.printStackTrace();
			} 			
		}
		
		if (DO_ALL || DO_NETWORKS) {
			ErrorReport.writeInfo("DO_NETWORKS","","","");
			try {
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write Networks
				System.out.println("Generate Network resources...");
	
				BulkNetworkBuilder nwBuilder = new BulkNetworkBuilder();
				List<VhDirNetwork> networks = nwBuilder.getNetworks(connection);
				outputNetworkList(networks); 
				ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d Networks Collected", networks.size()));
			} 
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}
			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in DO_NETWORKS", e.getMessage());
				e.printStackTrace();
				DatabaseUtil.closeAllConnections(connection);
			} catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "Parse error in DO_NETWORKS", e.getMessage());
				e.printStackTrace();
			} 			
		}

		if (DO_ALL || DO_LOCATIONS) {
			ErrorReport.writeInfo("DO_LOCATIONS","","","");
			try{
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write Locations
				System.out.println("Generate Location resources...");
				BulkLocationBuilder locBuilder = new BulkLocationBuilder();
				List<VhDirLocation> locations = locBuilder.getLocations(connection);
				outputLocationList(locations);  
				ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d Locations Collected", locations.size()));
			}	
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}
			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in DO_LOCATIONS", e.getMessage());
				e.printStackTrace();
				DatabaseUtil.closeAllConnections(connection);
			}
			catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "Parse error in DO_NETWORKS", e.getMessage());
				e.printStackTrace();
			}  
		}

		if (DO_ALL || DO_VALIDATIONS) {
			ErrorReport.writeInfo("DO_VALIDATIONS","","","");
			try{
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write Validations
				System.out.println("Generate Validation resources...");
				BulkValidationBuilder valBuilder = new BulkValidationBuilder();
				List<VhDirValidation> validations = valBuilder.getValidations(connection);
				outputValidationList(validations);  
				ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d Validations Collected", validations.size()));
			}
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}
			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in DO_VALIDATIONS", e.getMessage());
				e.printStackTrace();
				
			}
			catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "Parse error in DO_VALIDATIONS", e.getMessage());
				e.printStackTrace();
			}  
		}

		if (DO_ALL || DO_ENDPOINTS) {
			ErrorReport.writeInfo("DO_ENDPOINTS","","","");
			try{
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write Endpoints
				System.out.println("Generate Endpoint resources...");
				BulkEndpointBuilder epBuilder = new BulkEndpointBuilder();
				List<VhDirEndpoint> endpoints = epBuilder.getEndpoints(connection);
				outputEndpointList(endpoints);  
				ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d Endpoints Collected", endpoints.size()));
			}	
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}

			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in DO_ENDPOINTS", e.getMessage());
				e.printStackTrace();
			}
			catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "Parse error in DO_ENDPOINTSS", e.getMessage());
				e.printStackTrace();
			}  
		}

		if (DO_ALL || DO_CARETEAMS) {
			ErrorReport.writeInfo("DO_CARETEAMS","","","");
			try{
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write Careteams
				System.out.println("Generate Careteam resources...");
				BulkCareTeamBuilder ctBuilder = new BulkCareTeamBuilder();
				List<VhDirCareTeam> careteams = ctBuilder.getCareTeams(connection);
				outputCareTeamList(careteams);  
				ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d CareTeams Collected", careteams.size()));
			}	
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}

			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in DO_CARETEAMS", e.getMessage());
				e.printStackTrace();
			}
			catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "Parse error in DO_CARETEAMS", e.getMessage());
				e.printStackTrace();
			} 
		}

		if (DO_ALL || DO_HEALTHCARESERVICES) {
			ErrorReport.writeInfo("DO_HEALTHCARESERVICES","","","");
			try{
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write HeathcareServices
				System.out.println("Generate Healthcare_Service resources...");
				BulkHealthcareServiceBuilder hsBuilder = new BulkHealthcareServiceBuilder();
				List<VhDirHealthcareService> services = hsBuilder.getHealthcareServices(connection);
				outputServicesList(services);  
				ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d Healthcare Services Collected", services.size()));
			}	
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}
			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in DO_HEALTHCARESERVICES", e.getMessage());
				e.printStackTrace();
			}
			catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "Parse error in DO_HEALTHCARESERVICES", e.getMessage());
				e.printStackTrace();
			} 
		}

		if (DO_ALL || DO_INSURANCEPLANS) {
			ErrorReport.writeInfo("DO_INSURANCEPLANS","","","");
			try{
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write Insurance Planes
				System.out.println("Generate Insurance_Plan resources...");
				BulkInsurancePlanBuilder epBuilder = new BulkInsurancePlanBuilder();
				List<VhDirInsurancePlan> plans = epBuilder.getInsurancePlans(connection);
				outputInsurancePlanList(plans);  
				ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d Insurance Plans Collected", plans.size()));
			}	
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}
			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in DO_INSURANCEPLANS", e.getMessage());
				e.printStackTrace();
			}
			catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "Parse error in DO_INSURANCEPLANS", e.getMessage());
				e.printStackTrace();
			} 
		}

		if (DO_ALL || DO_ORGANIZATIONAFFILIATIONS) {
			ErrorReport.writeInfo("DO_ORGANIZATIONAFFILIATIONS","","","");
			try{
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write Organization Affiliations
				System.out.println("Generate Organization_Affiliation resources...");
				BulkOrganizationAffiliationBuilder affBuilder = new BulkOrganizationAffiliationBuilder();
				List<VhDirOrganizationAffiliation> affiliations = affBuilder.getOrganizationAffiliations(connection);
				outputOrganizationAffiliationsList(affiliations);  
			    ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d Organization Affiliations Collected", affiliations.size()));
			}	
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}
			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in DO_ORGANIZATIONAFFILIATIONS", e.getMessage());
				e.printStackTrace();
			}
			catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "Parse error in DO_ORGANIZATIONAFFILIATIONS", e.getMessage());
				e.printStackTrace();
			} 
		}

		if (DO_ALL || DO_PRACTITIONERROLES) {
			ErrorReport.writeInfo("DO_PRACTITIONERROLES","","","");
			
			try{
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write Practitioner Roles
				System.out.println("Generate Practitioner_Role resources...");
				BulkPractitionerRoleBuilder prBuilder = new BulkPractitionerRoleBuilder();
				List<VhDirPractitionerRole> practitionerroles = prBuilder.getPractitionerRoles(connection);
				outputPractitionerRoleList(practitionerroles);  
			    ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d Practitioner Roles Collected", practitionerroles.size()));
			}	
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}
			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in DO_PRACTITIONERROLES", e.getMessage());
				e.printStackTrace();
			}
			catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "Parse error in DO_PRACTITIONERROLES", e.getMessage());
				e.printStackTrace();
			} 
			
		}

		if (DO_ALL || DO_RESTRICTIONS) {
			ErrorReport.writeInfo("DO_RESTRICTIONS","","","");
			try{
				if (connection == null) {
					connection = DatabaseUtil.openAllConnections();
				}
				// Get and write Restrictions
				System.out.println("Generate Restriction resources...");
				BulkRestrictionBuilder resBuilder = new BulkRestrictionBuilder();
				List<VhDirRestriction> restrictions = resBuilder.getRestrictions(connection);
				outputRestrictionList(restrictions);  
				ErrorReport.setCursor("", "");
				ErrorReport.writeInfo("","","",String.format("%d Restrictions Collected", restrictions.size()));
			}	
			catch (CommunicationsException e) {
				ErrorReport.writeError("BulkDataApp", "", "Communications Exception in DO_NETWORKS", e.getMessage());
				DatabaseUtil.closeAllConnections(connection);
				connection = null;
			}
			catch (SQLException e) {
				ErrorReport.writeError("BulkDataApp", "", "SQL error in O_RESTRICTIONS", e.getMessage());
				e.printStackTrace();
				DatabaseUtil.closeAllConnections(connection);
			}
			catch (ParseException e) {
				ErrorReport.writeError("BulkDataApp", "", "Parse error in O_RESTRICTIONS", e.getMessage());
				e.printStackTrace();
			} 
		}

		
		DatabaseUtil.closeAllConnections(connection);
		
		// Note how long all this took...
		long elapsed = timer.stop();
		String elapsedTime = "Elapsed time: " + timer.convertToHours(elapsed);
		
		ErrorReport.setCursor("", "");
		System.out.println("\n\nFHIR Resource generation complete.");
		System.out.println(elapsedTime);
		
		if (DO_REPORTING) {
			System.out.println("   " + ErrorReport.getSummaryNote(elapsedTime));
			System.out.println("   See " + ErrorReport.REPORT_FILENAME + " for details.");
			ErrorReport.close();
		}

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
			ErrorReport.writeError("BulkDataApp", "outputOrganizationList", "IOException", e.getMessage());
			System.err.println("IO EXCEPTION writing organization list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputOrganizationList", "null pointer", e.getMessage());
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
			ErrorReport.writeError("BulkDataApp", "outputPractitionerList", "IOException", e.getMessage());
			System.err.println("EXCEPTION writing practitioner list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputPractitionerList", "null pointer", e.getMessage());
			System.err.println("NULL POINTER EXCEPTION writing practitioner list: " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	private static void outputPractitionerRoleList(List<VhDirPractitionerRole>practitionerroles) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PRACTITIONERROLES));
			BufferedWriter pp_writer = null;
			if (FILE_PRACTITIONERROLES_PP != null &&  !FILE_PRACTITIONERROLES_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_PRACTITIONERROLES_PP));
			}
			for (VhDirPractitionerRole prac : practitionerroles) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(prac);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "PRACTITIONER ROLE");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}

		}
		catch (IOException e) {
			ErrorReport.writeError("BulkDataApp", "outputPractitionerRoleList", "IOException", e.getMessage());
			System.err.println("EXCEPTION writing practitionerrole list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputPractitionerRoleList", "null pointer", e.getMessage());
			System.err.println("NULL POINTER EXCEPTION writing practitionerrole list: " + e.getMessage());
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
			ErrorReport.writeError("BulkDataApp", "outputNetworkList", "IOException", e.getMessage());
			System.err.println("EXCEPTION writing network list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputNetworkList", "null pointer", e.getMessage());
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
			ErrorReport.writeError("BulkDataApp", "outputLocationList", "IOException", e.getMessage());
			System.err.println("EXCEPTION writing location list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputLocationList", "null pointer", e.getMessage());
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
			ErrorReport.writeError("BulkDataApp", "outputValidationList", "IOException", e.getMessage());
			System.err.println("EXCEPTION writing validation list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputValidationList", "null pointer", e.getMessage());
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
			ErrorReport.writeError("BulkDataApp", "outputEndpointList", "IOException", e.getMessage());
			System.err.println("EXCEPTION writing endpoint list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputEndpointList", "null pointer", e.getMessage());
			System.err.println("NULL POINTER EXCEPTION writing endpoint list: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private static void outputCareTeamList(List<VhDirCareTeam>careteams) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_CARETEAMS));
			BufferedWriter pp_writer = null;
			if (FILE_CARETEAMS_PP != null &&  !FILE_CARETEAMS_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_CARETEAMS_PP));
			}
			for (VhDirCareTeam ct : careteams) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(ct);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "CARETEAM");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}
		}
		catch (IOException e) {
			ErrorReport.writeError("BulkDataApp", "outputCareTeamList", "IOException", e.getMessage());
			System.err.println("EXCEPTION writing careteam list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputCareTeamList", "null pointer", e.getMessage());
			System.err.println("NULL POINTER EXCEPTION writing careteam list: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private static void outputServicesList(List<VhDirHealthcareService>services) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_HEALTHCARESERVICES));
			BufferedWriter pp_writer = null;
			if (FILE_HEALTHCARESERVICES_PP != null &&  !FILE_HEALTHCARESERVICES_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_HEALTHCARESERVICES_PP));
			}
			for (VhDirHealthcareService ct : services) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(ct);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "HEALTHCARE SERVICE");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}
		}
		catch (IOException e) {
			ErrorReport.writeError("BulkDataApp", "outputHealthcareServiceList", "IOException", e.getMessage());
			System.err.println("EXCEPTION writing healtchare service list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputHealtheareServiceList", "null pointer", e.getMessage());
			System.err.println("NULL POINTER EXCEPTION writing healthcare service list: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private static void outputInsurancePlanList(List<VhDirInsurancePlan>plans) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_INSURANCEPLANS));
			BufferedWriter pp_writer = null;
			if (FILE_INSURANCEPLANS_PP != null &&  !FILE_INSURANCEPLANS_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_INSURANCEPLANS_PP));
			}
			for (VhDirInsurancePlan plan : plans) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(plan);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "INSURANCE PLAN");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}
		}
		catch (IOException e) {
			ErrorReport.writeError("BulkDataApp", "outputInsurancePlanList", "IOException", e.getMessage());
			System.err.println("EXCEPTION writing insurance plan list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputInsurancePlanList", "null pointer", e.getMessage());
			System.err.println("NULL POINTER EXCEPTION writing insurance plan list: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private static void outputRestrictionList(List<VhDirRestriction>restrictions) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_RESTRICTIONS));
			BufferedWriter pp_writer = null;
			if (FILE_RESTRICTIONS_PP != null &&  !FILE_RESTRICTIONS_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_RESTRICTIONS_PP));
			}
			for (VhDirRestriction plan : restrictions) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(plan);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "RESTRICTION");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}
		}
		catch (IOException e) {
			ErrorReport.writeError("BulkDataApp", "outputRestrictionList", "IOException", e.getMessage());
			System.err.println("EXCEPTION writing restrictions list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputRestrictionList", "null pointer", e.getMessage());
			System.err.println("NULL POINTER EXCEPTION writing restrictions list: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private static void outputOrganizationAffiliationsList(List<VhDirOrganizationAffiliation>affiliations) {
		FhirContext ctx = FhirContext.forR4();
		IParser jsonParser = ctx.newJsonParser();
		int cnt = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_ORGANIZATIONAFFILIATIONS));
			BufferedWriter pp_writer = null;
			if (FILE_ORGANIZATIONAFFILIATIONS_PP != null &&  !FILE_ORGANIZATIONAFFILIATIONS_PP.isEmpty()){
				pp_writer = new BufferedWriter(new FileWriter(FILE_ORGANIZATIONAFFILIATIONS_PP));
			}
			for (VhDirOrganizationAffiliation plan : affiliations) {
				if(MAX_ENTRIES != -1 && cnt >= MAX_ENTRIES) {
					break;
				}

				String nwJson = jsonParser.encodeResourceToString(plan);
				writer.write(nwJson);
				writer.write("\n");

				String prettyJson = maybePrettyPrintToFile(pp_writer, nwJson, cnt ); // Note: returns pretty print version of input json
				maybePrettyPrintToConsole(prettyJson, cnt, "ORGANIZATION AFFILIATION");
				
				cnt++;
			}
			writer.close();
			if (pp_writer != null) {
				pp_writer.close();
			}
		}
		catch (IOException e) {
			ErrorReport.writeError("BulkDataApp", "outputRestrictionList", "IOException", e.getMessage());
			System.err.println("EXCEPTION writing restrictions list: " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			ErrorReport.writeError("BulkDataApp", "outputRestrictionList", "null pointer", e.getMessage());
			System.err.println("NULL POINTER EXCEPTION writing restrictions list: " + e.getMessage());
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
