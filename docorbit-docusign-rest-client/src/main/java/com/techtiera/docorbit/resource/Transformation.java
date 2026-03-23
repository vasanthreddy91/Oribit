package com.techtiera.docorbit.resource;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "transformation")
public class Transformation {
	private Types types;

	@XmlElement(name = "types")
	public Types getTypes() {
		return types;
	}

	public void setTypes(Types types) {
		this.types = types;
	}
}
