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

	private static String REPORT_FILENAME = "VhDirGeneration-Report.csv";
	private static BufferedWriter writer = null;
	public static int errors = 0;
	public static int warnings = 0;
	public static int info = 0;
	
	public ErrorReport() { }

	static public void open() {
		
		try {
			writer = new BufferedWriter(new FileWriter(REPORT_FILENAME));
			errors = 0;
			warnings = 0;
			info = 0;
			Date now = new Date();
			// Write out column headers...
			writeMessage("MSG TYPE","RESOURCE","ID","NOTE","ERROR MESSAGE");
		    // Write some bookkeeping...
			writeInfo("ErrorReport", "", "Generation Time: " + now.toString(), "");
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
		writeMessage("E",resourceType,resourceId, note, errorMsg);
		errors++;
	}
	
	static public void writeWarning(String resourceType, String resourceId, String note, String errorMsg) {
		writeMessage("W",resourceType,resourceId, note, errorMsg);
		warnings++;
	}
	
	static public void writeInfo(String resourceType, String resourceId, String note, String errorMsg) {
		writeMessage("I",resourceType,resourceId, note, errorMsg);
		info++;
	}

	static public void writeMessage(String msgType,String resourceType, String resourceId, String note, String errorMsg) {
		if (writer != null) {
			String msg = String.format("%s,%s,%s,%s,%s", msgType,resourceType, resourceId, note, errorMsg);
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
		String note = String.format("Wrote %d error, %d warning and %d info messages to file %s", errors, warnings, info, REPORT_FILENAME);
		if (writer != null) {
			writeInfo("END","","",note);
		}
		return note;
	}

}
