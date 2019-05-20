package com.esacinc.spd.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
