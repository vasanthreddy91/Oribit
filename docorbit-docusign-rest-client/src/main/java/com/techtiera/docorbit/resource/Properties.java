package com.techtiera.docorbit.resource;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

public class Properties {
	private List<Property> property;

	@XmlElement(name = "property")
	public List<Property> getProperty() {
		return property;
	}

	public void setProperty(List<Property> property) {
		this.property = property;
	}
}
