package com.esacinc.spd.DataGenerator;

import com.esacinc.spd.util.DatabaseUtil;
import com.esacinc.spd.util.ErrorReport;
import com.esacinc.spd.util.Geocoding;
import com.esacinc.spd.util.PropertiesUtil;

/**
 * A class that holds most of the static variables used during the FHIR Resource generation process.
 * (Other static variables may be found in DatabaseUtil and ErrorReport classes.)
 * 
 * @author dandonahue
 *
 */
public class BuildControlSettings {

	protected static boolean RE_INITIALIZE = true;
	
	// Database connection and querying are handled in DatabaseUtils.java
	
	protected static boolean DO_REPORTING =   true;   // false means no error report file generated.
	public static int        MAX_ENTRIES =    5;      // Control how many entries we process in each section and output. -1 means ALL. 
	public static int     MAX_PP_ENTRIES = 10;   // Number of resources to put in the pretty print file. -1 means all
	protected static int     PP_NTH_CONSOLE = 0;    // Indicates prettyPrint nth item to System.output. Use -1 to skip

	// Which VhDir resources to generate...
	protected static boolean DO_ALL = false;   // If true, process all resource type, regardless of settings below
	protected static boolean DO_ORGANIZATIONS = false;
	protected static boolean DO_PRACTITIONERS = true;
	protected static boolean DO_NETWORKS = false;
	protected static boolean DO_LOCATIONS = false;
	protected static boolean DO_VALIDATIONS = false;
	protected static boolean DO_ENDPOINTS = false;
	protected static boolean DO_CARETEAMS = false;
	protected static boolean DO_HEALTHCARESERVICES = false;
	protected static boolean DO_INSURANCEPLANS = false;
	protected static boolean DO_RESTRICTIONS = false;
	protected static boolean DO_ORGANIZATIONAFFILIATIONS = false;
	protected static boolean DO_PRACTITIONERROLES = true;

	// Which VhDir resource files to generate...
	protected static String FILE_ORGANIZATIONS = "Organization";
	protected static String FILE_PRACTITIONERS = "Practitioner";
	protected static String FILE_NETWORKS = "Network";
	protected static String FILE_LOCATIONS = "Location";
	protected static String FILE_VALIDATIONS = "Validation";
	protected static String FILE_ENDPOINTS = "Endpoint";
	protected static String FILE_CARETEAMS = "Careteam";
	protected static String FILE_HEALTHCARESERVICES = "HealthcareService";
	protected static String FILE_INSURANCEPLANS = "InsurancePlan";
	protected static String FILE_ORGANIZATIONAFFILIATIONS = "OrganizationAffiliation";
	protected static String FILE_PRACTITIONERROLES = "PractitionerRole";
	protected static String FILE_RESTRICTIONS = "Restriction";

	// Indicate where to start reading from the db (id) for each resource
	protected static int FROM_ID_ORGANIZATIONS = 0;
	protected static int FROM_ID_PRACTITIONERS = 0;
	protected static int FROM_ID_NETWORKS = 0;
	protected static int FROM_ID_LOCATIONS = 0;
	protected static int FROM_ID_VALIDATIONS = 0;
	protected static int FROM_ID_ENDPOINTS = 0;
	protected static int FROM_ID_CARETEAMS = 0;
	protected static int FROM_ID_HEALTHCARESERVICES = 0;
	protected static int FROM_ID_INSURANCEPLANS = 0;
	protected static int FROM_ID_RESTRICTIONS = 0;
	protected static int FROM_ID_ORGANIZATIONAFFILIATIONS = 0;
	protected static int FROM_ID_PRACTITIONERROLES = 0;

	/**
	 * Maybe re-initialize all of our global static variables from values found in SPD_Settings.properties file.
	 * Do re-initialization only if the file exists, AND if the "INIT_FROM_PROPS" key in that file is set to true.
	 *
	 */
	public static void maybeReInitFromProperties() {
		RE_INITIALIZE = PropertiesUtil.getPropertyBoolean("INIT_FROM_PROPS",RE_INITIALIZE);
		if (RE_INITIALIZE) {
			
			System.out.println("Re-initializing global static varialbes from file " + PropertiesUtil.PROPERTIES_FILENAME);
			
			// Re-Initialize the variables we use to control how the Report file is generated...
			DO_REPORTING =   PropertiesUtil.getPropertyBoolean("DO_REPORTING",DO_REPORTING);
			ErrorReport.REPORT_FILENAME = PropertiesUtil.getPropertyString("REPORT_FILENAME", ErrorReport.REPORT_FILENAME);
			ErrorReport.IGNORE_ERRORS = PropertiesUtil.getPropertyBoolean("IGNORE_ERRORS",ErrorReport.IGNORE_ERRORS);
			ErrorReport.IGNORE_WARNINGS = PropertiesUtil.getPropertyBoolean("IGNORE_WARNINGS",ErrorReport.IGNORE_WARNINGS);
			ErrorReport.IGNORE_INFO = PropertiesUtil.getPropertyBoolean("IGNORE_INFO",ErrorReport.IGNORE_INFO);
			ErrorReport.IGNORE_GEOCODES = PropertiesUtil.getPropertyBoolean("IGNORE_GEOCODES",ErrorReport.IGNORE_GEOCODES);
			
			// Re-Initialize the variables we use to control how many things get processed, printed...
			MAX_ENTRIES =    PropertiesUtil.getPropertyInteger("MAX_ENTRIES",MAX_ENTRIES);      
			MAX_PP_ENTRIES = PropertiesUtil.getPropertyInteger("MAX_PP_ENTRIES",MAX_PP_ENTRIES);   
			PP_NTH_CONSOLE = PropertiesUtil.getPropertyInteger("PP_NTH_CONSOLE",PP_NTH_CONSOLE);

			// Re-Initialize the variables we use to control what resources we actually process...
			DO_ALL = PropertiesUtil.getPropertyBoolean("DO_ALL",DO_ALL);
			DO_ORGANIZATIONS = PropertiesUtil.getPropertyBoolean("DO_ORGANIZATIONS",DO_ORGANIZATIONS);
			DO_PRACTITIONERS = PropertiesUtil.getPropertyBoolean("DO_PRACTITIONERS",DO_PRACTITIONERS);
			DO_NETWORKS = PropertiesUtil.getPropertyBoolean("DO_NETWORKS",DO_NETWORKS);
			DO_LOCATIONS = PropertiesUtil.getPropertyBoolean("DO_LOCATIONS",DO_LOCATIONS);
			DO_VALIDATIONS = PropertiesUtil.getPropertyBoolean("DO_VALIDATIONS",DO_VALIDATIONS);
			DO_ENDPOINTS = PropertiesUtil.getPropertyBoolean("DO_ENDPOINTS",DO_ENDPOINTS);
			DO_CARETEAMS = PropertiesUtil.getPropertyBoolean("DO_CARETEAMS",DO_CARETEAMS);
			DO_HEALTHCARESERVICES = PropertiesUtil.getPropertyBoolean("DO_HEALTHCARESERVICES",DO_HEALTHCARESERVICES);
			DO_INSURANCEPLANS = PropertiesUtil.getPropertyBoolean("DO_INSURANCEPLANS",DO_INSURANCEPLANS);
			DO_RESTRICTIONS = PropertiesUtil.getPropertyBoolean("DO_RESTRICTIONS",DO_RESTRICTIONS);
			DO_ORGANIZATIONAFFILIATIONS = PropertiesUtil.getPropertyBoolean("DO_ORGANIZATIONAFFILIATIONS",DO_ORGANIZATIONAFFILIATIONS);
			DO_PRACTITIONERROLES = PropertiesUtil.getPropertyBoolean("DO_PRACTITIONERROLES",DO_PRACTITIONERROLES);

			// Re-Initialize the variables we use to control what files our ndjson resources get printed to...
			FILE_ORGANIZATIONS = PropertiesUtil.getPropertyString("FILE_ORGANIZATIONS", FILE_ORGANIZATIONS);
			FILE_PRACTITIONERS = PropertiesUtil.getPropertyString("FILE_PRACTITIONERS", FILE_PRACTITIONERS);
			FILE_NETWORKS = PropertiesUtil.getPropertyString("FILE_NETWORKS", FILE_NETWORKS);
			FILE_LOCATIONS = PropertiesUtil.getPropertyString("FILE_LOCATIONS", FILE_LOCATIONS);
			FILE_VALIDATIONS = PropertiesUtil.getPropertyString("FILE_VALIDATIONS", FILE_ORGANIZATIONS);
			FILE_ENDPOINTS = PropertiesUtil.getPropertyString("FILE_ENDPOINTS", FILE_ENDPOINTS);
			FILE_CARETEAMS = PropertiesUtil.getPropertyString("FILE_CARETEAMS", FILE_CARETEAMS);
			FILE_HEALTHCARESERVICES = PropertiesUtil.getPropertyString("FILE_HEALTHCARESERVICES", FILE_HEALTHCARESERVICES);
			FILE_INSURANCEPLANS = PropertiesUtil.getPropertyString("FILE_INSURANCEPLANS", FILE_INSURANCEPLANS);
			FILE_ORGANIZATIONAFFILIATIONS = PropertiesUtil.getPropertyString("FILE_ORGANIZATIONAFFILIATIONS", FILE_ORGANIZATIONAFFILIATIONS);
			FILE_PRACTITIONERROLES = PropertiesUtil.getPropertyString("FILE_PRACTITIONERROLES", FILE_PRACTITIONERROLES);
			FILE_RESTRICTIONS = PropertiesUtil.getPropertyString("FILE_RESTRICTIONS", FILE_RESTRICTIONS);
	
			FROM_ID_ORGANIZATIONS = PropertiesUtil.getPropertyInteger("FROM_ID_ORGANIZATIONS", FROM_ID_ORGANIZATIONS);
			FROM_ID_PRACTITIONERS = PropertiesUtil.getPropertyInteger("FROM_ID_PRACTITIONERS", FROM_ID_PRACTITIONERS);
			FROM_ID_NETWORKS = PropertiesUtil.getPropertyInteger("FROM_ID_NETWORKS", FROM_ID_NETWORKS);
			FROM_ID_LOCATIONS = PropertiesUtil.getPropertyInteger("FROM_ID_LOCATIONS", FROM_ID_LOCATIONS);
			FROM_ID_VALIDATIONS = PropertiesUtil.getPropertyInteger("FROM_ID_VALIDATIONS", FROM_ID_ORGANIZATIONS);
			FROM_ID_ENDPOINTS = PropertiesUtil.getPropertyInteger("FROM_ID_ENDPOINTS", FROM_ID_ENDPOINTS);
			FROM_ID_CARETEAMS = PropertiesUtil.getPropertyInteger("FROM_ID_CARETEAMS", FROM_ID_CARETEAMS);
			FROM_ID_HEALTHCARESERVICES = PropertiesUtil.getPropertyInteger("FROM_ID_HEALTHCARESERVICES", FROM_ID_HEALTHCARESERVICES);
			FROM_ID_INSURANCEPLANS = PropertiesUtil.getPropertyInteger("FROM_ID_INSURANCEPLANS", FROM_ID_INSURANCEPLANS);
			FROM_ID_ORGANIZATIONAFFILIATIONS = PropertiesUtil.getPropertyInteger("FROM_ID_ORGANIZATIONAFFILIATIONS", FROM_ID_ORGANIZATIONAFFILIATIONS);
			FROM_ID_PRACTITIONERROLES = PropertiesUtil.getPropertyInteger("FROM_ID_PRACTITIONERROLES", FROM_ID_PRACTITIONERROLES);
			FROM_ID_RESTRICTIONS = PropertiesUtil.getPropertyInteger("FROM_ID_RESTRICTIONS", FROM_ID_RESTRICTIONS);

			// Re-Initialize the variables we use connect to datasources...
			DatabaseUtil.dbUsername = PropertiesUtil.getPropertyString("dbUsername", DatabaseUtil.dbUsername);
			DatabaseUtil.dbPassword = PropertiesUtil.getPropertyString("dbPassword", DatabaseUtil.dbUsername);
	
			DatabaseUtil.connectionUrl = PropertiesUtil.getPropertyString("connectionUrl", DatabaseUtil.connectionUrl);
			DatabaseUtil.zipConnectionUrl = PropertiesUtil.getPropertyString("zipConnectionUrl", DatabaseUtil.zipConnectionUrl);
			DatabaseUtil.MAX_CONNECT_ATTEMPTS = PropertiesUtil.getPropertyInteger("MAX_CONNECT_ATTEMPTS", DatabaseUtil.MAX_CONNECT_ATTEMPTS);
			DatabaseUtil.GLOBAL_LIMIT = PropertiesUtil.getPropertyInteger("GLOBAL_LIMIT", DatabaseUtil.GLOBAL_LIMIT);
			
			// Re-Initialize whether we want to do a couple basic tests of the Geocode function. (Probably we don't.)
			Geocoding.DO_GEOCODE_TEST =  PropertiesUtil.getPropertyBoolean("DO_GEOTEST",Geocoding.DO_GEOCODE_TEST);
			Geocoding.PROCESS_GEOCODES_ONLY =  PropertiesUtil.getPropertyBoolean("PROCESS_GEOCODES_ONLY",Geocoding.PROCESS_GEOCODES_ONLY);
			Geocoding.UPDATE_ADDRESSES =  PropertiesUtil.getPropertyBoolean("UPDATE_ADDRESSES",Geocoding.UPDATE_ADDRESSES);

		}
	}

	/**
	 * Used by all the resource builders to determine if it is time to stop processing them...
	 * @param cnt
	 * @return
	 */
	public static boolean okToProceed(int cnt) {
		return (MAX_ENTRIES == -1 || cnt < MAX_ENTRIES);
	}
	
	protected static String makeFilename(String base, int cnt, String extension) {
		if (extension.equalsIgnoreCase("ndjson"))
			return base+"_"+cnt+"."+extension;
		else {
			return base+"_PP_"+cnt+"."+extension;
		}
	}


}
