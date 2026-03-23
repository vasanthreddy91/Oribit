package com.techtiera.docorbit.batch.config;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "record")
public class Record {

    private String name;
    private String extension;
    private String author;
    private String contentUrl;
    private String documentType;
    private String associationId;
    private String isFileAnnotated;
    private List<Property> properties;
    private List<Version> versions;

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "extension")
    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @XmlElement(name = "author")
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @XmlElement(name = "contentUrl")
    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    @XmlElement(name = "documentType")
    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    
    @XmlElement(name = "associationId")
	public String getAssociationId() {
		return associationId;
	}

	public void setAssociationId(String associationId) {
		this.associationId = associationId;
	}
	
	@XmlElement(name = "isFileAnnotated")
    public String getIsFileAnnotated() {
		return isFileAnnotated;
	}

	public void setIsFileAnnotated(String isFileAnnotated) {
		this.isFileAnnotated = isFileAnnotated;
	}

	// Use @XmlElementWrapper for the list of properties
    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

	// Use @XmlElementWrapper for the list of versions
    @XmlElementWrapper(name = "versions")
    @XmlElement(name = "version")
	public List<Version> getVersions() {
		return versions;
	}

	public void setVersions(List<Version> versions) {
		this.versions = versions;
	}
    
}
