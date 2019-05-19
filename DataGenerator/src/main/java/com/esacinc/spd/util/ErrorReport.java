package com.esacinc.spd.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ErrorReport {

	public static String REPORT_FILENAME = "VhDirGeneration-Report.csv";
	public static int errors = 0;
	public static int warnings = 0;
	public static int geocodes = 0;
	public static int info = 0;
	public static String CURSOR = "";
	private static BufferedWriter writer = null;
		
	public ErrorReport() { }

	static public void setCursor(String resource, String id) {
		if (resource.isEmpty()) {
			CURSOR = " ";
		}
		else {
			CURSOR = String.format("%s(id: %s) ", resource, id);
		}
	}
	
	static public void open() {
		
		try {
			writer = new BufferedWriter(new FileWriter(REPORT_FILENAME));
			errors = 0;
			warnings = 0;
			info = 0;
			Date now = new Date();
			// Write out column headers...
			writeMessage("MSG TYPE","PROCESS CURSOR","RESOURCE","ID","SHORT NOTE","MESSAGE");
			ErrorReport.setCursor("", "");
		    // Write some bookkeeping...
			writeInfo("ErrorReport", "", "","Generation Time: " + now.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 static public void close() {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static public void writeError(String resourceType, String resourceId, String note, String errorMsg) {
		if (writer == null ) return;
		writeMessage("E",CURSOR,resourceType,resourceId, note, errorMsg);
		errors++;
	}
	
	static public void writeWarning(String resourceType, String resourceId, String note, String errorMsg) {
		if (writer == null ) return;
		writeMessage("W",CURSOR,resourceType,resourceId, note, errorMsg);
		warnings++;
	}
	
	static public void writeInfo(String resourceType, String resourceId, String note, String errorMsg) {
		if (writer == null ) return;
		writeMessage("I",CURSOR,resourceType,resourceId, note, errorMsg);
		info++;
	}

	static public void writeGeoCodeMsg(String resourceType, String resourceId, String note, String errorMsg) {
		if (writer == null ) return;
		writeMessage("G",CURSOR,resourceType,resourceId, note, errorMsg);
		geocodes++;
	}

	
	static public void writeMessage(String msgType, String cursor, String resourceType, String resourceId, String note, String errorMsg) {
		if (writer != null) {
			String msg = String.format("%s |%s |%s |%s |%s |%s", msgType,cursor, resourceType, resourceId, note, errorMsg);
			try {
				writer.write(msg);
				writer.write("\n");
			}
			catch (Exception e) {
				System.err.println("Error writing report message: " + msg);
			}
		}
	}
	
	static public String getSummaryNote() {
		String note = "";		
		if (writer != null) {
			note = String.format("Wrote %d error, %d warning, %d geocode, and %d info messages", errors, warnings, geocodes, info);
			writeInfo("END","","",note);
		}
		return note;
	}

}
