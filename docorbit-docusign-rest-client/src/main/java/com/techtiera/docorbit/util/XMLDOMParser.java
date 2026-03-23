package com.techtiera.docorbit.util;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.techtiera.docorbit.alfresco.constants.Constants;

public class XMLDOMParser {

	public static final Logger logger = LoggerFactory.getLogger(XMLDOMParser.class);

	public static List<RecordProperties> readUsersAsKeyValue(String xmlPath, String type) {

		List<RecordProperties> usersList = new ArrayList<>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(xmlPath));
			document.getDocumentElement().normalize();

			NodeList userNodes = null;
			if (type.equals("user")) {
				userNodes = document.getElementsByTagName("user");
			} else if (type.equals("template")) {
				userNodes = document.getElementsByTagName("template");
			} else if (type.equals("envelop")) {
				userNodes = document.getElementsByTagName("envelop");
			} else if (type.equals("group")) {
				userNodes = document.getElementsByTagName("group");
			}

			for (int i = 0; i < userNodes.getLength(); i++) {

				Element userElement = (Element) userNodes.item(i);
				Map<String, String> userMap = new LinkedHashMap<>();

				NodeList children = userElement.getChildNodes();

				for (int j = 0; j < children.getLength(); j++) {
					Node node = children.item(j);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						flattenElement((Element) node, userMap);
					}
				}

				RecordProperties recordProperties = new RecordProperties();
				recordProperties.setProperties(userMap);
				usersList.add(recordProperties);
			}

		} catch (Exception e) {
			throw new RuntimeException("Failed to parse users XML", e);
		}

		return usersList;
	}

	public static List<RecordTemplateProperties> readTemplateRecordProperties(String path) {

		List<RecordTemplateProperties> recordPropertiesList = new ArrayList<>();

		try {
			File xmlFile = new File(path);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			document.getDocumentElement().normalize();

			NodeList nodes = null;
			String nodeType = null;

			// ✅ Priority check
			if (document.getElementsByTagName("envelop").getLength() > 0) {
				nodes = document.getElementsByTagName("envelop");
				nodeType = "envelop";
			} else if (document.getElementsByTagName("template").getLength() > 0) {
				nodes = document.getElementsByTagName("template");
				nodeType = "template";
			} else if (document.getElementsByTagName("user").getLength() > 0) {
				nodes = document.getElementsByTagName("user");
				nodeType = "user";
			} else if (document.getElementsByTagName("group").getLength() > 0) {
				nodes = document.getElementsByTagName("group");
				nodeType = "group";
			}

			// ❌ Nothing found
			if (nodes == null) {
				return recordPropertiesList;
			}

			// ✅ Common parsing logic
			for (int i = 0; i < nodes.getLength(); i++) {

				Node node = nodes.item(i);
				if (node.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element element = (Element) node;
				Map<String, String> properties = new LinkedHashMap<>();

				properties.put("type", nodeType);
				properties.put("name", getTagValue(element, "name"));
				properties.put("extension", getTagValue(element, "extension"));
				properties.put("exportLocation", getTagValue(element, "exportLocation"));
				properties.put("downloadStatus", getTagValue(element, "downloadStatus"));
				properties.put("errorMessage", getTagValue(element, "errorMessage"));

				RecordTemplateProperties recordProperties = new RecordTemplateProperties();
				recordProperties.setProperties(properties);

				recordPropertiesList.add(recordProperties);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return recordPropertiesList;
	}

	public static List<RecordEnvelopeProperties> readEnvelopeRecordProperties(String path) {

		List<RecordEnvelopeProperties> recordPropertiesList = new ArrayList<>();

		try {
			File xmlFile = new File(path);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			document.getDocumentElement().normalize();

			NodeList nodes = null;
			String nodeType = null;

			// ✅ Priority check
			if (document.getElementsByTagName("envelope").getLength() > 0) {
				nodes = document.getElementsByTagName("envelope");
				nodeType = "envelop";
			} else if (document.getElementsByTagName("template").getLength() > 0) {
				nodes = document.getElementsByTagName("template");
				nodeType = "template";
			} else if (document.getElementsByTagName("user").getLength() > 0) {
				nodes = document.getElementsByTagName("user");
				nodeType = "user";
			} else if (document.getElementsByTagName("group").getLength() > 0) {
				nodes = document.getElementsByTagName("group");
				nodeType = "group";
			}

			// ❌ Nothing found
			if (nodes == null) {
				return recordPropertiesList;
			}

			// ✅ Common parsing logic
			for (int i = 0; i < nodes.getLength(); i++) {

				Node node = nodes.item(i);
				if (node.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element element = (Element) node;
				Map<String, String> properties = new LinkedHashMap<>();

				properties.put("type", nodeType);
				properties.put("name", getTagValue(element, "name"));
				properties.put("extension", getTagValue(element, "extension"));
				properties.put("exportLocation", getTagValue(element, "exportLocation"));
				properties.put("downloadStatus", getTagValue(element, "downloadStatus"));
				properties.put("errorMessage", getTagValue(element, "errorMessage"));

				RecordEnvelopeProperties recordProperties = new RecordEnvelopeProperties();
				recordProperties.setProperties(properties);

				recordPropertiesList.add(recordProperties);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return recordPropertiesList;
	}

	private static String getTagValue(Element element, String tagName) {
		NodeList nodeList = element.getElementsByTagName(tagName);
		if (nodeList != null && nodeList.getLength() > 0) {
			return nodeList.item(0).getTextContent();
		}
		return null;
	}

	private static void flattenElement(Element element, Map<String, String> map) {

		NodeList children = element.getChildNodes();
		boolean hasElementChild = false;

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				hasElementChild = true;
				break;
			}
		}

		// Leaf node → add to map
		if (!hasElementChild) {
			String key = element.getNodeName();
			String value = element.getTextContent().trim();
			if (map.containsKey(key)) {
				String existing = map.get(key);
				map.put(key, existing + "\n" + value); // newline separator
			} else {
				map.put(key, value);
			}

		}

		// Non-leaf → recurse
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				flattenElement((Element) node, map);
			}
		}
	}

	public static List<RecordProperties> readRecordProperties(String path) {
		List<RecordProperties> recordPropertiesList = new ArrayList<RecordProperties>();
		try {
			// Load XML file
			File xmlFile = new File(path);

			// Create DocumentBuilderFactory and DocumentBuilder
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Parse XML file and create Document
			Document document = builder.parse(xmlFile);
			document.getDocumentElement().normalize();

			// Get all "record" elements
			NodeList nodeList = document.getElementsByTagName(Constants.XML_RECORD_TAG);
			RecordProperties recordProperties = null;
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					Map<String, String> properties = new LinkedHashMap<>();
					// Get all child elements of the record and store them in the map
					NodeList childNodes = element.getChildNodes();
					for (int j = 0; j < childNodes.getLength(); j++) {
						Node child = childNodes.item(j);
						if (child.getNodeType() == Node.ELEMENT_NODE) {
							Element childElement = (Element) child;
							String key = childElement.getTagName();
							if (Constants.XML_PROPERTIES_TAG.equals(key)) {
								NodeList propertyNodes = childElement.getElementsByTagName(Constants.XML_PROPERTY_TAG);
								for (int k = 0; k < propertyNodes.getLength(); k++) {
									Element propertyElement = (Element) propertyNodes.item(k);
									String propertyName = propertyElement.getAttribute(Constants.XML_NAME_TAG);
									String propertyValue = propertyElement.getTextContent();
									properties.put(propertyName, propertyValue);
								}
							} else if (Constants.XML_VERSIONS_TAG.equals(key)) {
								NodeList versionsNodes = childElement.getElementsByTagName(Constants.XML_VERSION_TAG);
								String value = formatVersionData(versionsNodes);
								System.out.println("Version value : " + value);
								properties.put(key, value);
							} else {
								String value = childElement.getTextContent();
								properties.put(key, value);
							}
						}
					}
					recordProperties = new RecordProperties();
					recordProperties.setProperties(properties);
					recordPropertiesList.add(recordProperties);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordPropertiesList;

	}

	public static List<String> getSourceDocumentTypes(String path) {
		List<String> documentTypes = new ArrayList<String>();
		try {
			File xmlFile = new File(path);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName(Constants.XML_RECORD_TAG);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					NodeList childNodes = element.getChildNodes();
					for (int j = 0; j < childNodes.getLength(); j++) {
						Node child = childNodes.item(j);
						if (child.getNodeType() == Node.ELEMENT_NODE) {
							Element childElement = (Element) child;
							String key = childElement.getTagName();
							String value = childElement.getTextContent();
							if (key.equals(Constants.XML_DOCUMENT_TYPE_TAG) && (!documentTypes.contains(value))) {
								documentTypes.add(value);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (documentTypes.size() > 0) {
			documentTypes = documentTypes.stream().sorted().collect(Collectors.toList());
		}
		return documentTypes;
	}

	public static List<String> getSourceAttributes(String path, String documentType) {
		List<String> sourceAttributes = new ArrayList<>();
		try {
			File xmlFile = new File(path);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			document.getDocumentElement().normalize();
			// Get all records (assuming records are under XML_RECORD_TAG)
			NodeList nodeList = document.getElementsByTagName(Constants.XML_RECORD_TAG);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					// Get the documentType element
					NodeList documentTypeNodes = element.getElementsByTagName(Constants.XML_DOCUMENT_TYPE_TAG);
					if (documentTypeNodes.getLength() > 0
							&& documentTypeNodes.item(0).getTextContent().equals(documentType)) {
						// Now get the properties associated with this documentType
						NodeList propertyNodes = element.getElementsByTagName(Constants.XML_PROPERTY_TAG);
						for (int k = 0; k < propertyNodes.getLength(); k++) {
							Element propertyElement = (Element) propertyNodes.item(k);
							String propertyName = propertyElement.getAttribute(Constants.XML_NAME_TAG);
							// Add propertyName to the sourceAttributes list if not already present
							if (!sourceAttributes.contains(propertyName)) {
								sourceAttributes.add(propertyName);
							}
						}
					} else {
						logger.info("DocumentType mismatch or not found.");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sourceAttributes.size() > 0) {
			sourceAttributes = sourceAttributes.stream().sorted().collect(Collectors.toList());
		}
		return sourceAttributes;
	}

	public static void main(String[] args) {
		// List<RecordProperties> recordProperties = readRecordProperties(
		// "D:\\Alfresco\\Output\\xml\\DocOrbit-metadata-xml.xml");
		// logger.info("record size : " + recordProperties.size());

		// List<String> documentTypes =
		// getDocumentTypes("D:\\Alfresco\\Output\\xml\\DocOrbit-metadata-xml.xml");
		// logger.info("documentTypes size : " + documentTypes.size());

		// List<String> documentAttributes = getSourceAttributes(
		// "D:\\Alfresco\\Output\\xml\\DocOrbit-metadata-xml-single.xml",
		// "DOC:EngineeringCType");
		// logger.info("documentTypes size : " + documentAttributes.size());
		// documentAttributes.forEach(str -> logger.info(str));
	}

	private static String formatVersionData(NodeList versionsNodes) {
		StringBuilder valueBuilder = new StringBuilder();

		for (int i = 0; i < versionsNodes.getLength(); i++) {
			Node versionNode = versionsNodes.item(i);
			if (versionNode.getNodeType() == Node.ELEMENT_NODE) {
				Element versionElement = (Element) versionNode;
				String versionNumber = versionElement.getElementsByTagName("number").item(0).getTextContent();
				String contentUrl = versionElement.getElementsByTagName("contentUrl").item(0).getTextContent();
				if (i > 0) {
					// valueBuilder.append("\t"); //Same line with space
					valueBuilder.append("\n"); // For Getting versions in new line
				}
				valueBuilder.append(versionNumber).append(", ").append(contentUrl);
			}
		}
		return valueBuilder.toString();
	}
}
