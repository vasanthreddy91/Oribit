package com.techtiera.docorbit.resource;

import jakarta.xml.bind.annotation.XmlElement;

public class Types {
	private Type type;

	@XmlElement(name = "type")
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
