package com.techtiera.docorbit.util;

import java.util.Map;

public class RecordProperties {
	private Map<String, String> properties;

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public boolean getDownloadStatus() {
		// Retrieve the downloadStatus as a String and parse it to boolean
		String downloadStatus = properties.get("uploadStatus");
		return Boolean.parseBoolean(downloadStatus); // Return true/false based on the string value
	}


	@Override
	public String toString() {
		return "RecordProperties [properties=" + properties + "]";
	}
}
