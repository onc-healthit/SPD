package com.esacinc.spd.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

	private static Properties properties = null;
	public static String PROPERTIES_FILENAME = "SPD_Settings.properties";
	
	public PropertiesUtil() { }


	static private Properties loadProperties() {
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(new FileInputStream(PROPERTIES_FILENAME));
			} catch (FileNotFoundException e) {
				ErrorReport.writeError("Properties File", "File Not Found",PROPERTIES_FILENAME , e.getMessage());
			} catch (IOException e) {
				ErrorReport.writeError("Properties File", "IO Exception",PROPERTIES_FILENAME , e.getMessage());
			}
		}
		return properties;

	}

	static public String getPropertyString(String key, String defaultVal) {
        try {
            Properties props = loadProperties();
            if (props == null) {
    			return null;
    		}
    		
            String res = props.getProperty(key); 
            if (res == null ){
            	return defaultVal;
            }
            else {
            	return res.trim();
            }
        }
        catch(Exception e) {
             return defaultVal;
        }
	}
	
	static public Integer getPropertyInteger(String key, Integer defaultVal) {
		String val = getPropertyString(key, null);
		if (val == null) {
			return defaultVal;
		}
		else {
			Integer intVal = Integer.valueOf(val);
			return intVal;
		}
	}
	
	static public Boolean getPropertyBoolean(String key, Boolean defaultVal) {
		String val = getPropertyString(key, null);
		if (val == null) {
			return defaultVal;
		}
		else {
			Boolean bVal = Boolean.parseBoolean(val);
			return bVal;
		}
	}
	

}
