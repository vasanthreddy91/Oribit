package com.techtiera.docorbit.resource;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "type")
public class Type {
	private String sourceType;
	private String targetType;

	private List<Property> properties;

	@XmlElement(name = "sourceType")
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	@XmlElement(name = "targetType")
	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	@XmlElementWrapper(name = "properties")
	@XmlElement(name = "property")
	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

}
