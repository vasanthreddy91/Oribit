package com.techtiera.docorbit.util;

import java.util.HashMap;
import java.util.Map;

import com.techtiera.docorbit.resource.TemplateReportDetails;

public class RecordTemplateProperties {

    private Map<String, String> properties = new HashMap<>();

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    // ✅ Add helper to map EnvelopeXml → RecordProperties
    public void fromEnvelopeXml(TemplateReportDetails env) {
        properties.put("name", env.getTemplateName());
        properties.put("extension", env.getExtension());
        properties.put("exportLocation", env.getLocation());
        properties.put("downloadStatus", String.valueOf(env.isStatus()));
        properties.put("errorMessage", env.getErrorMessage());
    }

    public boolean getDownloadStatus() {
        String downloadStatus = properties.get("downloadStatus");
        return Boolean.parseBoolean(downloadStatus);
    }

    @Override
    public String toString() {
        return "RecordProperties [properties=" + properties + "]";
    }
}
