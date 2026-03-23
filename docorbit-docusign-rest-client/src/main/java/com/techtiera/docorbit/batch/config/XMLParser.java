package com.techtiera.docorbit.batch.config;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import com.techtiera.docorbit.resource.Type;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class XMLParser {

	public static void main(String[] args) throws Exception {
		String mainXmlPath = "D:\\Alfresco\\Output\\xml\\DocOrbit-metadata-xml-single.xml";
		String transXmlPath = "D:/Alfresco/Output/content/DocOrbit-transformation-xml.xml";
		System.out.println(isValidXML(transXmlPath));

	//	 readMainxml(mainXmlPath);
	  	readTransxml(transXmlPath);
	}

	public static void readMainxml(String path) {
		try {
			File file = new File(path);
			JAXBContext jaxbContext = JAXBContext.newInstance(Records.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			Records records = (Records) unmarshaller.unmarshal(file);

			for (Record record : records.getRecords()) {
				System.out.println("Record Name: " + record.getName());
				for (Property property : record.getProperties()) {
					System.out.println("Property Name: " + property.getName() + " | Value: " + property.getValue());
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static void readTransxml(String path) {
		try {
			File file = new File(path);
			JAXBContext jaxbContext = JAXBContext.newInstance(Type.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			Type type = (Type) unmarshaller.unmarshal(file);

			for (com.techtiera.docorbit.resource.Property property : type.getProperties()) {
				System.out.println("Source: " + property.getSource() + " | Target: " + property.getTarget());
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static boolean isValidXML(String filePath) {
		try {
			DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filePath);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}