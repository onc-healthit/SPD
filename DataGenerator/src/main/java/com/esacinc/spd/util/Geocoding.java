package com.esacinc.spd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.esacinc.spd.model.VhDirGeoLocation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Geocoding {
	
	static public boolean LIMIT_REACHED = false; 
	static public Connection zipconnection = null;
	static public boolean DO_GEOCODE_TEST = false;

	static public Connection openConnection() {
		zipconnection = DatabaseUtil.getZipConnection();
		return zipconnection;
	}
	
	static public void closeConnection() {
		System.out.println("Closing Zipcode Database connection to " + DatabaseUtil.zipConnectionUrl);
		DatabaseUtil.closeConnection(zipconnection);
		zipconnection = null;
	}
	
	/**
	 * Calls a web service to get the lat/lon for the postal code passed in.  If connection parameter is
	 * non-null, then update all of the rows in the 'address' table in the database with the same postal code with the
	 * newly determined lat/lon values.
	 *  
	 * @param postalCode must be at least a 5-digit postal code
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static VhDirGeoLocation geocodePostalCode(String postalCode, Connection connection) throws SQLException {
		
		// Don't even bother if we don't have a postal code that is at least 5 characters
		if (postalCode.length() < 5) {
			ErrorReport.writeGeoCodeMsg("VhDirGeoLocation", "postalCode: " + postalCode, "Invalid postal code", "Must be at least 5 characters. Unable to geolocate");
			System.err.println("Geocoding Error: Invalid postal code: " + postalCode + ". Must be at least 5 characters. Unable to geolocate");
			return null;
		}
		postalCode = postalCode.substring(0,5);
		// If first 5 chars of the postal code aren't digits, then we also have a problem.
		if (!postalCode.matches("[0-9]+")) {
			ErrorReport.writeGeoCodeMsg("VhDirGeoLocation", "postalCode: " + postalCode, "Invalid postal code", "Must be all digits. Unable to geolocate");
			System.err.println("Geocoding Error: Invalid postal code: " + postalCode + ". Must be all digits. Unable to geolocate");
			return null;
		}
		// Try to get a geolocation from our local zipcode table...
		VhDirGeoLocation loc = geocodeLocalPostalCode(postalCode);
		// If we couldn't get a loc from the local zipcode table, then try calling an external source...
		if (loc == null && !LIMIT_REACHED )
		{
			try {
				loc = new VhDirGeoLocation();
				String baseUrl = "http://api.geonames.org/postalCodeSearchJSON?country=US&postalcode=";
				String fullUrl = baseUrl + postalCode + "&username=mholck";
				URL postResource;
	
				postResource = new URL(fullUrl);
				HttpURLConnection con;
				con = (HttpURLConnection) postResource.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("Content-Type", "application/json");
				con.setDoOutput(true);
				con.setConnectTimeout(0);
				con.setReadTimeout(0);
				
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String l = null;
				while ((l=br.readLine())!=null) {
					System.out.println(l);
					if (l.indexOf("the hourly limit") > -1) {
						LIMIT_REACHED = true;
						ErrorReport.writeGeoCodeMsg("VhDirGeoLocation", "postalCode: " + postalCode, "Too Many calls", "Limit reached of 1000 calls in an hour to service api.geonames.org.");
					}
					JsonElement result = new JsonParser().parse(l);
				    JsonObject resultObj = result.getAsJsonObject();
				    JsonArray postalCodes = resultObj.getAsJsonArray("postalCodes");
				    if (postalCodes == null || !postalCodes.isJsonArray() || postalCodes.size() == 0) {
						ErrorReport.writeGeoCodeMsg("VhDirGeoLocation", "postalCode: " + postalCode, "Null or Empty Results", "Error parsing results from http://api.geonames.org with postal code: " + postalCode + " Returned line: '" + l + "'");
				    	System.err.println("Geocoding Error: Error parsing results from http://api.geonames.org with postal code: " + postalCode + " Returned line: '" + l + "'");
				    }
					else {
					    JsonObject propertiesJson = postalCodes.get(0).getAsJsonObject();
					    double lat = propertiesJson.get("lat").getAsDouble();
					    double lon = propertiesJson.get("lng").getAsDouble();
					    loc.setLatitude(lat);
					    loc.setLongitude(lon);
				    
					    // Update all DB records with this postalCode to add lat/lon
					    if (connection != null) {
						    String updateQuery = "UPDATE address SET latitude=?, longitude=? WHERE postalCode like '" +
						    		postalCode + "%'";
						    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
						    updateStatement.setDouble(1, lat);
						    updateStatement.setDouble(2, lon);
							updateStatement.executeUpdate();
					    }
				    }
				}
				br.close();
		
				con.disconnect();
			} catch (MalformedURLException e) {
				ErrorReport.writeGeoCodeMsg("VhDirGeoLocation", "postalCode: " + postalCode, "URL Exception", e.getMessage());
				System.err.println("Geocoding Error: MalformedURLException: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				ErrorReport.writeGeoCodeMsg("VhDirGeoLocation", "postalCode: " + postalCode, "IO Exception", e.getMessage());
				System.err.println("Geocoding Error: IOException: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return loc;
	}
	
	
	static public VhDirGeoLocation getGeoLocation(Double lat, Double lon, String postalCode, Connection connection)  {
		VhDirGeoLocation loc = new VhDirGeoLocation();
		try {
			// If we don't have a valid lat/lon, the get one by geo locating the given postalCode
			if (!LIMIT_REACHED && (lat == null || lon == null || lat == 0.0 || lon == 0.0)) {
				System.out.println("Geocoding.getGeoLocation:  Calling Geocoding lat-lon for postal code " + postalCode);
				loc = Geocoding.geocodePostalCode(postalCode, connection);
			} else {
				// Otherwise, simply put the lat/lon into a geolocation object.
				loc = new VhDirGeoLocation();
				loc.setLatitude(lat);
				loc.setLongitude(lon);
			}
		}
		catch (Exception e) {
			ErrorReport.writeGeoCodeMsg("VhDirGeoLocation", "postalCode: "+postalCode, "Geocoding.getGeoLocation" , "Exception creating geolocation for (" + lat + ", " + lon + ") and postalCode '" + postalCode + "'" + e.getMessage());
			System.err.println("Exception creating geolocation for (" + lat + ", " + lon + ") and postalCode '" + postalCode + "'" + e.getMessage());
			e.printStackTrace();
		}

		return loc;
		
	}
	
	static public VhDirGeoLocation geocodeLocalPostalCode(String postalCode) throws SQLException {
		//System.out.println("Calling local geocoding for " + postalCode);
		if (zipconnection != null) {
			ResultSet resultset = DatabaseUtil.runZipQuery(zipconnection, "select * from zip_codes where zip = ?", postalCode);
			while (resultset.next()) {
				VhDirGeoLocation loc = new VhDirGeoLocation();
				loc.setLatitude(resultset.getDouble("latitude"));
				loc.setLatitude(resultset.getDouble("longitude"));
				return loc; // expecting only one.
			}
			// Too verbose. Don't report this msg.
			//ErrorReport.writeGeoCodeMsg("VhDirGeoLocation", "postalCode: " + postalCode, "Geocoding.geocodeLocalPostalCode", "Postal code not found in local db.");
		}
		else {
			System.err.println("Unable to open connection to zip schema");
		}
		return null;
	}
	
    static public void basicTest() {
    	System.out.println("Running Geocode tests");
		try {
			ErrorReport.writeGeoCodeMsg("Geocode Testing", "46224", "", "We know this is valid");
			Geocoding.geocodePostalCode("46224", null); // We know this is valid;
			ErrorReport.writeGeoCodeMsg("Geocode Testing", "096030300", "", "We know this is not valid");
			Geocoding.geocodePostalCode("096030300", null);
			ErrorReport.writeGeoCodeMsg("Geocode Testing", "96297", "", "We know this is not valid");
			Geocoding.geocodePostalCode("96297", null);
		}
		catch (Exception e) {
			ErrorReport.writeGeoCodeMsg("Geocode Testing", "", "Geocoding error", e.getMessage());
			e.printStackTrace();
		}
    }
			
}
