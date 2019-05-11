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

	public DatabaseUtil() { }

	static public Connection getConnection() {
		// Connect to the DB
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(connectionUrl,	dbUsername, dbPassword);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("\nFHIR Resource generation terminated with connection error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("\nFHIR Resource generation terminated, mysql driver not found");
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

}
