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
	 * @param postalCode
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static VhDirGeoLocation geocodePostalCode(String postalCode, Connection connection) throws SQLException {
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
			br.close();
	
			con.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return loc;
	}
}
