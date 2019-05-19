package com.esacinc.spd.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ErrorReport {

	public static String REPORT_FILENAME = "VhDirGeneration-Report.csv";
	public static int errors = 0;
	public static int warnings = 0;
	public static int geocodes = 0;
	public static int info = 0;
	public static String CURSOR = "";
	private static BufferedWriter writer = null;
	
	private static boolean IGNORE_ERRORS = false;
	private static boolean IGNORE_WARNINGS = false;
	private static boolean IGNORE_INFO = false;
	private static boolean IGNORE_GEOCODES = false;
		
	public ErrorReport() { }

	static public void messageThrottles(boolean ignoreE, boolean ignoreW, boolean ignoreI, boolean ignoreG) {
		IGNORE_ERRORS = ignoreE;
		IGNORE_WARNINGS = ignoreW;
		IGNORE_INFO = ignoreI;
		IGNORE_GEOCODES = ignoreG;
	}
	
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
		if (writer == null || IGNORE_ERRORS) return;
		writeMessage("E",CURSOR,resourceType,resourceId, note, errorMsg);
		errors++;
	}
	
	static public void writeWarning(String resourceType, String resourceId, String note, String errorMsg) {
		if (writer == null || IGNORE_WARNINGS ) return;
		writeMessage("W",CURSOR,resourceType,resourceId, note, errorMsg);
		warnings++;
	}
	
	static public void writeInfo(String resourceType, String resourceId, String note, String errorMsg) {
		if (writer == null || IGNORE_INFO) return;
		writeMessage("I",CURSOR,resourceType,resourceId, note, errorMsg);
		info++;
	}

	static public void writeGeoCodeMsg(String resourceType, String resourceId, String note, String errorMsg) {
		if (writer == null || IGNORE_GEOCODES) return;
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
