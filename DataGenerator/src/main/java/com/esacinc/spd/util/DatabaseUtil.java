package com.esacinc.spd.util;

import java.sql.*;

public class DatabaseUtil {

	// Database credentials...
	public static String dbUsername = "spduser";
	public static String dbPassword = "SpdUs3r45";
	
	//public static String dbUsername = "root";
	//public static String dbPassword = "root";

	//public static String connectionUrl = "jdbc:mysql://localhost:3306/spd";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_scrubbed";
	public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_small";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_small_scrubbed";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_medium";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_medium_scrubbed";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_large";
	//public static String connectionUrl = "jdbc:mysql://65.111.255.73:3306/spd_larged_scrubbed";

	public static String zipConnectionUrl = "jdbc:mysql://65.111.255.73:3306/zipcode";
	
	public static int MAX_CONNECT_ATTEMPTS = 3;
	
	public static int GLOBAL_LIMIT = -1;
	
	
	public DatabaseUtil() { }

	static public Connection getConnection() { 
		// Connect to the DB
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(connectionUrl,	dbUsername, dbPassword);
		} catch (SQLException e) {
			ErrorReport.writeError("DatabaseUtil", "getConnection", "", e.getMessage());
			System.err.println("\nFHIR Resource generation terminated with connection error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			ErrorReport.writeError("DatabaseUtil", "getConnection", "", e.getMessage());
			System.err.println("\nFHIR Resource generation terminated, mysql driver not found");
			e.printStackTrace();
		} 
		return connection;
	}
	
	static public Connection getZipConnection() {
		// Connect to the DB
		System.out.println("Opening SPD Zipcodes connection to " + zipConnectionUrl);
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(zipConnectionUrl,	dbUsername, dbPassword);
		} catch (SQLException e) {
			ErrorReport.writeError("DatabaseUtil", "getZipConnection", "", e.getMessage());
			System.err.println("\nFHIR Zipcode connection terminated with connection error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			ErrorReport.writeError("DatabaseUtil", "getZipConnection", "", e.getMessage());
			System.err.println("\nFHIR  Zipcode connection terminated, mysql driver not found");
			e.printStackTrace();
		} 
		return connection;
	}
	
	static public ResultSet runQuery (Connection connection, String strSql, Integer id) throws SQLException{
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		if (id != null) {
			sqlStatement.setInt(1, id);
		}
		ResultSet resultset = sqlStatement.executeQuery();
		return resultset;
	}
	
	// Last arg is a string, which is why we have a separate query.
	static public ResultSet runZipQuery (Connection connection, String strSql, String id) throws SQLException{
		PreparedStatement sqlStatement = connection.prepareStatement(strSql);
		if (id != null) {
			sqlStatement.setString(1, id);
		}
		ResultSet resultset = sqlStatement.executeQuery();
		return resultset;
	}

	static public void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			}
			catch (SQLException e) {
				System.err.println("Error closing database connection: " + connection + ": " + e.getMessage());
			}
		}
	}
	
	static public Connection openAllConnections() {
		System.out.println("Opening SPD Database connection to " + connectionUrl);
		Connection connection = null;
		for (int attempts = 0; attempts < MAX_CONNECT_ATTEMPTS; attempts++) {
			try {
				connection = getConnection();
				if (connection == null) {
					ErrorReport.writeError("Database","", "SPD Schema",  "Unable to open connection to " + connectionUrl);
				}
			}
			catch (Exception e) {
				ErrorReport.writeError("Database","", "SPD Schema",  e.getMessage());
			}
			if (connection != null) {
				break;
			}
		}
		if (connection == null) {
			ErrorReport.writeError("Database","", "SPD Schema",  "Unable to open connection to " + connectionUrl + " after " + MAX_CONNECT_ATTEMPTS + " attempts");
		}
		try {
			Geocoding.openConnection();  // Open a connection to the zipcode database in case we need it.
		}
		catch (Exception e) {
			// Doesn't matter too much, really, if we can't open a connection to the zipcode db. We seldom need it anyway.
			ErrorReport.writeError("Database","", "Zipcode Schema",  e.getMessage());
		}
		return connection;
	}
	
	static public void closeAllConnections(Connection spdSchemaConnction) {
		System.out.println("Closing SPD Database connection to " + DatabaseUtil.connectionUrl);
		closeConnection(spdSchemaConnction);
		Geocoding.closeConnection();
	}
	
}
