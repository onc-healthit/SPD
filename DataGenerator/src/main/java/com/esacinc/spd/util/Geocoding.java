package com.esacinc.spd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.esacinc.spd.model.VhDirGeoLocation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Geocoding {
	
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
			System.err.println("Geocoding Error: Invalid postal code: " + postalCode + ". Must be at least 5 characters. Unable to geolocate");
			return null;
		}
		postalCode = postalCode.substring(0,5);
		// If first 5 chars of the postal code aren't digits, then we also have a problem.
		if (!postalCode.matches("[0-9]+")) {
			System.err.println("Geocoding Error: Invalid postal code: " + postalCode + ". Must be all digits. Unable to geolocate");
			return null;
		}
		VhDirGeoLocation loc = new VhDirGeoLocation();
		String baseUrl = "http://api.geonames.org/postalCodeSearchJSON?country=US&postalcode=";
		String fullUrl = baseUrl + postalCode + "&username=mholck";
		URL postResource;
		
		try {
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
				JsonElement result = new JsonParser().parse(l);
			    JsonObject resultObj = result.getAsJsonObject();
			    JsonArray postalCodes = resultObj.getAsJsonArray("postalCodes");
			    if (postalCodes == null || !postalCodes.isJsonArray()) {
			    	System.err.println("Geocoding Error: Error parsing results from http://api.geonames.org with postal code: " + postalCode + " Returned line: '" + l + "'");
			    }
			    else if (postalCodes.size() == 0 ) {
			    	System.err.println("Geocoding Error: No valid values returned from http://api.geonames.org  for postal code " + postalCode );
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
			ErrorReport.writeError("VhDirGeocodeLocation", "postalCode: " + postalCode, "URL Exception", e.getMessage());
			System.err.println("Geocoding Error: MalformedURLException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			ErrorReport.writeError("VhDirGeocodeLocation", "postalCode: " + postalCode, "IO Exception", e.getMessage());
			System.err.println("Geocoding Error: IOException: " + e.getMessage());
			e.printStackTrace();
		}
		
		return loc;
	}
	
	
	static public VhDirGeoLocation getGeoLocation(Double lat, Double lon, String postalCode, Connection connection)  {
		VhDirGeoLocation loc = new VhDirGeoLocation();
		try {
			// If we don't have a valid lat/lon, the get one by geo locating the given postalCode
			if (lat == null || lon == null || lat == 0.0 || lon == 0.0) {
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
				System.err.println("Exception creating geolocation for (" + lat + ", " + lon + ") and postalCode '" + postalCode + "'" + e.getMessage());
				e.printStackTrace();
		}

		return loc;
		
	}
}
