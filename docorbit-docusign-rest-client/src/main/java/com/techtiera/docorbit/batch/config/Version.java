package com.techtiera.docorbit.batch.config;

import jakarta.xml.bind.annotation.XmlElement;

public class Version {

	private String number;
	private String createdDate;
	private String contentUrl;
	private String documentName;
	private String documentFormat;

	@XmlElement(name = "number")
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@XmlElement(name = "createdDate")
	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	@XmlElement(name = "contentUrl")
	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	@XmlElement(name = "documentName")
	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	@XmlElement(name = "documentFormat")
	public String getDocumentFormat() {
		return documentFormat;
	}

	public void setDocumentFormat(String documentFormat) {
		this.documentFormat = documentFormat;
	}
}
