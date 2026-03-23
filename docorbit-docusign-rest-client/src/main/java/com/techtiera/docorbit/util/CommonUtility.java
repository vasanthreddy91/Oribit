package com.techtiera.docorbit.util;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.techtiera.docorbit.resource.EnvelopReportDetails;
import com.techtiera.docorbit.resource.Source;
import com.techtiera.docorbit.resource.TemplateReportDetails;
import com.techtiera.docorbit.resource.VersionDetails;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommonUtility {

	public static final Logger logger = LoggerFactory.getLogger(CommonUtility.class);

	public static void main(String[] args) {

		Path filePath1 = Paths.get("C:\\Users\\Documents\\document.pdf");
		Path filePath2 = Paths.get("C:\\Users\\Documents\\report"); // no extension
		Path filePath3 = Paths.get("C:\\Users\\Documents\\config.sys.old"); // multiple dots

		logger.info("Extension of " + filePath1 + ": " + isFileHasExtension(filePath1));
		logger.info("Extension of " + filePath2 + ": " + isFileHasExtension(filePath2));
		logger.info("Extension of " + filePath3 + ": " + isFileHasExtension(filePath3));
	}

	public static void getFolders() {
		// Creating a File object for directory
		File directoryPath = new File(
				"D:\\cms-docs\\training\\Rajamohan\\Extractor Code\\Output\\content\\raj_cabinet");
		// List of all files and directories
		String contents[] = directoryPath.list();
		logger.info("List of files and directories in the specified directory:");
		for (int i = 0; i < contents.length; i++) {
			logger.info(contents[i]);
		}
	}

	public static List<File> getFiles(String directoryName, List<File> files) {
		File directory = new File(directoryName);
		// Get all files from a directory.
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					files.add(file);
				}
			}
		}
		for (File f : files) {
			logger.info("file Name : " + f.getName());
		}
		return files;
	}

	public static int convertToJson(String jsonData, String nodeName) {
		int node = 0;
		JSONObject json = new JSONObject(jsonData);
		JSONArray jsonArray = json.getJSONArray("data");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject explrObject = jsonArray.getJSONObject(i);
			if (explrObject.get("name").equals(nodeName)) {
				node = (int) explrObject.get("id");
				break;
			}
		}
		return node;
	}

	public static List<String> getAlfrescoTypes(String jsonData) {
		List<String> types = new ArrayList<>();
		JSONArray jsonArray = new JSONArray(jsonData);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject.has("parent")) {
				JSONObject parent = jsonObject.getJSONObject("parent");
				if (parent.has("name") && "cm:content".equals(parent.getString("name"))) {
					types.add(jsonObject.getString("name"));
				}
			}
		}
		if (types.size() > 0) {
			types = types.stream().sorted().collect(Collectors.toList());
		}
		return types;
	}

	public static List<String> getAlfrescoTypesAttributes(String jsonData) {
		List<String> typeAttributes = new ArrayList<>();
		JSONObject jsonObject = new JSONObject(jsonData);
		JSONObject properties = jsonObject.getJSONObject("properties");
		Iterator<String> keys = properties.keys();
		while (keys.hasNext()) {
			typeAttributes.add(keys.next());
		}
		if (typeAttributes.size() > 0) {
			typeAttributes = typeAttributes.stream().sorted().collect(Collectors.toList());
		}
		return typeAttributes;
	}

	public static String alfrescoTicketParsing(String jsonData) {
		String id = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(jsonData);
			id = rootNode.path("entry").path("id").asText();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return id;
	}

	public static void readAllDataAtOnce(String file) {
		try {
			FileReader filereader = new FileReader(file);
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
			List<String[]> allData = csvReader.readAll();

			for (String[] row : allData) {
				for (String cell : row) {
					logger.info(cell + "\t");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Map<String, String>> readCsvFile(String csvFile) {
		List<Map<String, String>> dataList = new ArrayList<>();
		try {
			FileReader filereader = new FileReader(csvFile);
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
			String[] headers = csvReader.readNext();

			int docPathIndex = -1;

			// Find the indices for Name and Doc Path
			for (int i = 0; i < headers.length; i++) {
				if (headers[i].equalsIgnoreCase("Doc Path")) {
					docPathIndex = i;
				}
			}

			if (docPathIndex == -1) {
				throw new IllegalArgumentException("CSV file does not contain the required headers.");
			}

			String[] values;
			while ((values = csvReader.readNext()) != null) {
				Map<String, String> dataMap = new HashMap<>();
				String docPath = values[docPathIndex];

				// Remove the first slash if it exists
				if (docPath.startsWith("/")) {
					docPath = docPath.substring(1);
				}

				dataMap.put("Doc Path", docPath);
				dataList.add(dataMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}

	public static boolean hasFileExtension(String fileName) {
		int lastIndexOfDot = fileName.lastIndexOf('.');
		if (lastIndexOfDot == -1) {
			return false;
		} else if (lastIndexOfDot == fileName.length() - 1) {
			return false;
		} else {
			return true;
		}
	}

	public static String getFileExtension(String filePath) {
		int lastIndexOfDot = filePath.lastIndexOf('.');
		int lastIndexOfSeparator = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
		if (lastIndexOfDot == -1 || lastIndexOfDot < lastIndexOfSeparator) {
			return null; // No extension found
		} else {
			return filePath.substring(lastIndexOfDot + 1);
		}
	}

	public static String isFileHasExtension(Path path) {
		String extension = null;
		String fileName = path.getFileName().toString();
		int dotIndex = fileName.lastIndexOf('.');

		// handle cases with no extension or multiple dots
		if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
			extension = null; // no extension found
		} else {
			extension = fileName.substring(dotIndex + 1);
		}
		return extension;
	}

	public static String removeFirstPathSegment(String path) {
		int firstSlashIndex = path.indexOf('/', 1); // Find the index of the second slash
		if (firstSlashIndex != -1) {
			return path.substring(firstSlashIndex); // Return the string from the second slash onward
		}
		return path; // If there's no second slash, return the original path
	}

	public static void createElementTag(Document document, Element parentTag, String name, String value) {
		try {
			Element element = document.createElement(name);
			element.appendChild(document.createTextNode(value));
			parentTag.appendChild(element);
		} catch (Exception e) {
			logger.error("createElementTag failed :: Element : " + name);
		}
	}

	public static List<String> removeMatchingFirstAndLast(String string1, String string2) {
		List<String> list = new ArrayList<String>();
		String[] parts1 = string1.split("/");
		String[] parts2 = string2.split("/");
		String lastPart1 = parts1[parts1.length - 1];
		String firstPart2 = parts2[1];
		if (lastPart1.equals(firstPart2)) {
			string1 = string1.substring(0, string1.lastIndexOf("/"));
			list.add(string1);
			list.add(string2);
			return list;
		} else {
			list.add(string1);
			list.add(string2);
		}
		return list;
	}

//	public static void logsDisplay(String message, TextArea logTextArea) {
//		logger.info(message);
//		Platform.runLater(() -> {
//			logTextArea.appendText(message + "\n");
//			logTextArea.setScrollTop(Double.MAX_VALUE);
//		});
//	}

	public static String getFileNameWithOutExtension(String filePath) {
		// Find the position of the last dot to remove the extension
		int lastDotIndex = filePath.lastIndexOf(".");

		// If there's a dot, get the full path without the extension
		String fullPathWithoutExtension = (lastDotIndex != -1) ? filePath.substring(0, lastDotIndex) : filePath;
		return fullPathWithoutExtension;
	}

	public static boolean validateXMLSchema(String xsdPath, String xmlPath) {
		try {
			InputStream xsdStream = CommonUtility.class.getClassLoader().getResourceAsStream(xsdPath);
			if (xsdStream == null) {
				System.out.println("XSD file not found in resources!");
				return false;
			}
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(xsdStream));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new File(xmlPath)));
			return true;
		} catch (SAXException e) {
			System.out.println("XML validation error: " + e.getMessage());
			return false;
		} catch (Exception e) {
			System.out.println("XML validation error: " + e.getMessage());
			return false;
		}
	}

	public static VersionDetails parsingDiscoveryDetails(String jsonData) {
		VersionDetails versionDetails = new VersionDetails();
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			versionDetails.setName(jsonObject.getJSONObject("entry").getJSONObject("repository").getString("edition"));
			String version = jsonObject.getJSONObject("entry").getJSONObject("repository").getJSONObject("version")
					.getString("display");
			versionDetails.setVersion(version.split(" ")[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionDetails;
	}

	public static String formatDateTime() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return now.format(formatter);
	}

	public static void logProcessLoader(boolean status, ProgressIndicator logLoader) {
		logLoader.setVisible(status);
	}

	public static String parsingUserInfo(String jsonData) {
		String accountId = null;
		try {
			JSONObject json = new JSONObject(jsonData);

			JSONArray accounts = json.optJSONArray("accounts");
			if (accounts == null || accounts.isEmpty()) {
				return null;
			}

			// Prefer default account if available
			for (int i = 0; i < accounts.length(); i++) {
				JSONObject acc = accounts.getJSONObject(i);
				if (acc.optBoolean("is_default")) {
					return acc.optString("account_id", null);
				}
			}

			// fallback → first account
			JSONObject firstAcc = accounts.getJSONObject(0);
			accountId = firstAcc.optString("account_id", null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return accountId;
	}

	public static void templatexmlwrite(Source source, Path outputPath) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element sourceEl = doc.createElement("source");
		doc.appendChild(sourceEl);

		Element nameEl = doc.createElement("name");
		nameEl.setTextContent(source.getName());
		sourceEl.appendChild(nameEl);

		Element versionEl = doc.createElement("version");
		versionEl.setTextContent(source.getVersion());
		sourceEl.appendChild(versionEl);

		Element templatesEl = doc.createElement("templateItems");
		sourceEl.appendChild(templatesEl);

		for (TemplateReportDetails env : source.getTemplates()) {

			Element templateEl = doc.createElement("template");
			templatesEl.appendChild(templateEl);

			append(doc, templateEl, "name", env.getTemplateName());
			append(doc, templateEl, "extension", env.getExtension());
			append(doc, templateEl, "exportLocation", env.getLocation());
			append(doc, templateEl, "downloadStatus", String.valueOf(env.isStatus()));
			append(doc, templateEl, "errorMessage", env.getErrorMessage());
		}

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		transformer.transform(new DOMSource(doc), new StreamResult(outputPath.toFile()));
	}

	public static void envelopexmlwrite(Source source, Path outputPath) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element sourceEl = doc.createElement("source");
		doc.appendChild(sourceEl);

		Element nameEl = doc.createElement("name");
		nameEl.setTextContent(source.getName());
		sourceEl.appendChild(nameEl);

		Element versionEl = doc.createElement("version");
		versionEl.setTextContent(source.getVersion());
		sourceEl.appendChild(versionEl);

		Element templatesEl = doc.createElement("envelopeItems");
		sourceEl.appendChild(templatesEl);

		for (EnvelopReportDetails env : source.getEnvelops()) {

			Element templateEl = doc.createElement("envelope");
			templatesEl.appendChild(templateEl);

			append(doc, templateEl, "name", env.getEnvelopName());
			append(doc, templateEl, "extension", env.getExtension());
			append(doc, templateEl, "exportLocation", env.getLocation());
			append(doc, templateEl, "downloadStatus", String.valueOf(env.isStatus()));
			append(doc, templateEl, "errorMessage", env.getErrorMessage());
		}

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		transformer.transform(new DOMSource(doc), new StreamResult(outputPath.toFile()));
	}

	private static void append(Document doc, Element parent, String tag, String value) {
		Element el = doc.createElement(tag);
		el.setTextContent(value == null ? "" : value);
		parent.appendChild(el);
	}

	public static String formatColumnName(String key) {

		if (key == null || key.isEmpty()) {
			return key;
		}

		// Insert space before capital letters: exportLocation → export Location
		String text = key.replaceAll("([a-z])([A-Z])", "$1 $2");

		// Capitalize first letter: export Location → Export Location
		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}

	public static void logsDisplay(String message, TextArea logTextArea) {
		logger.info(message);
		Platform.runLater(() -> {
			logTextArea.appendText(message + "\n");
			logTextArea.setScrollTop(Double.MAX_VALUE);
		});
	}

}
