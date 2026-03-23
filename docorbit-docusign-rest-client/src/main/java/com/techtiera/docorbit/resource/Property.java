package com.techtiera.docorbit.resource;

import jakarta.xml.bind.annotation.XmlElement;

public class Property {
	private String source;
	private String target;

	@XmlElement(name = "source")
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@XmlElement(name = "target")
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
