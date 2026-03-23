package com.techtiera.docorbit.batch.config;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.techtiera.docorbit.alfresco.constants.Constants;
import com.techtiera.docorbit.alfresco.services.AlfescoRestServiceUtility;
import com.techtiera.docorbit.alfresco.services.CreateFolderWrapper;
import com.techtiera.docorbit.alfresco.services.FileCustomTypeWrapper;
import com.techtiera.docorbit.alfresco.services.GetNodeMetadataWrapper;
import com.techtiera.docorbit.alfresco.services.UploadNewFileVersionWrapper;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.resource.ReportDetails;
import com.techtiera.docorbit.resource.VersionDetails;
import com.techtiera.docorbit.util.CommonUtility;
import com.techtiera.docorbit.util.RecordReport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
public class TargetWriter implements ItemStreamWriter<Record> {

	public static final Logger logger = LoggerFactory.getLogger(TargetWriter.class);

	public FileCustomTypeWrapper fileCustomTypeWrapper;

	public GetNodeMetadataWrapper getNodeMetadataWrapper;

	public CreateFolderWrapper createFolderWrapper;

	public UploadNewFileVersionWrapper uploadNewFileVersionWrapper;

	public StepExecution stepExecution;

	public String sourceType;

	public String targetType;

	public long recordsExecutionCount;

	private List<RecordReport> reportList;

	private Document reportDocument;

	@Autowired
	private AlfescoRestServiceUtility alfescoRestServiceUtility;

	public TargetWriter(FileCustomTypeWrapper fileCustomTypeWrapper, GetNodeMetadataWrapper getNodeMetadataWrapper,
			CreateFolderWrapper createFolderWrapper, UploadNewFileVersionWrapper uploadNewFileVersionWrapper) {
		this.fileCustomTypeWrapper = fileCustomTypeWrapper;
		this.getNodeMetadataWrapper = getNodeMetadataWrapper;
		this.createFolderWrapper = createFolderWrapper;
		this.uploadNewFileVersionWrapper = uploadNewFileVersionWrapper;
		this.reportList = new ArrayList<>();
	}

	@Override
	public void write(Chunk<? extends Record> items) throws Exception {
		logger.info(Thread.currentThread().getName() + "- AlfrescoWriter :: write() start...");
		List<? extends Record> recordProperties = items.getItems().stream().collect(Collectors.toList());
		logger.info("csvPropertiesList size : " + recordProperties.size());
		try {
			boolean pathIssue = false;
			for (Record record : recordProperties) {
				RecordReport report = new RecordReport(record.getName(), record.getExtension(), record.getContentUrl(),
						record.getDocumentType(), false, "");
				reportList.add(report);
				
				// File Extension validation
				if (!CommonUtility.hasFileExtension(record.getName())) {
					record.setName(record.getName() + Constants.SEPARATOR_DOT + record.getExtension());
					// Need to add code for separating the extension
				}
				boolean tempNodeExists = false;
				String relativeFolderPath = PropertiesHolder.getNodePath();
				String folderPath = record.getContentUrl();
				if (folderPath.contains("documentLibrary")) {
					String[] docSplits = folderPath.split("documentLibrary");
					folderPath = docSplits[1];
				} else {
					logger.info("documentLibrary word was not found in the path.");
				}

				logger.info(" updated folder path : " + folderPath);
				if (StringUtils.hasText(folderPath) && StringUtils.hasText(relativeFolderPath)) {
					List<String> updatedPaths = CommonUtility.removeMatchingFirstAndLast(relativeFolderPath,
							folderPath);
					relativeFolderPath = updatedPaths.get(0);
					folderPath = updatedPaths.get(1);
					String updatedPath = relativeFolderPath + folderPath;
					tempNodeExists = getNodeMetadataWrapper.getNode(updatedPath);
				}
				if (!tempNodeExists) {
					String[] folders = folderPath.split(Constants.SEPARATOR_FORWARD_SLASH);
					for (String folder : folders) {
						logger.info(Thread.currentThread().getName() + "- folder Name with path : " + relativeFolderPath
								+ Constants.SEPARATOR_FORWARD_SLASH + folder);

						boolean nodeExists = getNodeMetadataWrapper
								.getNode(relativeFolderPath + Constants.SEPARATOR_FORWARD_SLASH + folder);
						if (!nodeExists) {
							logger.info("Node not exists " + folder);
							boolean nodeCreated = createFolderWrapper.createFolder(folder, folder, folder,
									relativeFolderPath);
							if (nodeCreated) {
								relativeFolderPath = relativeFolderPath + Constants.SEPARATOR_FORWARD_SLASH + folder;
							} else {
								logger.error("Error while creating node folder : " + folder);
								pathIssue = true;
								break;
							}
						} else {
							relativeFolderPath = relativeFolderPath + Constants.SEPARATOR_FORWARD_SLASH + folder;
						}
					}
				} else {
					relativeFolderPath = relativeFolderPath + Constants.SEPARATOR_FORWARD_SLASH + folderPath;
				}
				if (!pathIssue) {
					if (PropertiesHolder.isLogProcessIndicatorStatus()) {
						CommonUtility.logProcessLoader(false, PropertiesHolder.getLogProcessLoader());
						PropertiesHolder.setLogProcessIndicatorStatus(false);
					}
					// boolean isFileAnnotated = false;
					String filePath = PropertiesHolder.getTemplateContentFolderPath() + Constants.SEPARATOR_FORWARD_SLASH
							+ folderPath + Constants.SEPARATOR_FORWARD_SLASH + record.getName();
					/*
					 * for(com.techtiera.docorbit.batch.config.Property properties :
					 * record.getProperties()) {
					 * if(properties.getName().equals(Constants.ALF_ANNOTATION_PROPERTY)) { filePath
					 * = filePath + record.getName().replace(" ", "+") +
					 * Constants.ALF_ANNOTATION_FILE_EXTENSION;
					 * record.setName(record.getName().replace(" ", "+")); isFileAnnotated = true; }
					 * } if(!isFileAnnotated) { filePath = filePath + record.getName(); }
					 */
					logger.info("filePath : " + filePath);
//					boolean fileUploadStatus = false;
					ReportDetails fileUploadStatus = null;
					if (record.getAssociationId() != null && record.getIsFileAnnotated().equals("true")) {
						String xfdfFilePath = PropertiesHolder.getTemplateContentFolderPath()
								+ Constants.SEPARATOR_FORWARD_SLASH + folderPath + Constants.SEPARATOR_FORWARD_SLASH
								+ record.getAssociationId() + Constants.ALF_ANNOTATION_FILE_EXTENSION;
						String xfdfNodeId = fileCustomTypeWrapper.uploadXfdfFile(record, relativeFolderPath,
								xfdfFilePath);
						fileUploadStatus = fileCustomTypeWrapper.uploadFile(record, relativeFolderPath, filePath,
								xfdfNodeId);
					} else {
						fileUploadStatus = fileCustomTypeWrapper.uploadFile(record, relativeFolderPath, filePath, null);
					}
					if (fileUploadStatus.isStatus()) {
						recordsExecutionCount++;
						PropertiesHolder.setRecordsExecutionCount(recordsExecutionCount);
						logger.info(Thread.currentThread().getName() + "- Document created in Alfresco.....");
						report.setStatus(Boolean.TRUE);
					} else {
						report.setStatus(Boolean.FALSE);
						report.setErrorMessage(fileUploadStatus.getErrorMessage());
//						CommonUtility.logsDisplay(
//								CommonUtility.formatDateTime() + " - " + record.getName()
//							 			+ " is failed to Upload. Please check the report",
//								PropertiesHolder.getLogTextArea());
					}
				}
			}
			logger.info(Thread.currentThread().getName()
					+ "- AlfrescoService :: uploadDocumentsFromWriterListener() :: end");
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("AlfrescoWriter :: write() end...");
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		logger.info("Opening TargetWriter...");
		this.reportList = new ArrayList<>();
		try {
			// Initialize the XML document for the report
			DocumentBuilderFactory dbReportFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dReportBuilder = dbReportFactory.newDocumentBuilder();
			reportDocument = dReportBuilder.newDocument();

			// Create the root XML elements
			Element sourceRootReport = reportDocument.createElement(Constants.XML_SOURCE_TAG);
			reportDocument.appendChild(sourceRootReport);
			VersionDetails versionDetails = alfescoRestServiceUtility.getVersionDetails();

			if (versionDetails != null) {
				CommonUtility.createElementTag(reportDocument, sourceRootReport, Constants.RECORD_PROP_NAME,
						versionDetails.getName());
				CommonUtility.createElementTag(reportDocument, sourceRootReport, Constants.XML_VERSION_TAG,
						versionDetails.getVersion());
			}
			Element reportsRootRecords = reportDocument.createElement(Constants.XML_ROOT_TAG);
			sourceRootReport.appendChild(reportsRootRecords);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method to write XML after processing all records
	@Override
	public void close() throws ItemStreamException {
		logger.info("Closing TargetWriter and writing the final report...");
		try {
			// Write the accumulated report data (RecordReport objects) to XML tags
			if(!reportList.isEmpty()) {
				generateXmlReportFromList(reportList);
				writeXmlToFile(reportDocument, PropertiesHolder.getReportFolderPath(), Constants.XML_REPORT_FILE_NAME);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Generate XML tags for each record in the reportList
	private void generateXmlReportFromList(List<RecordReport> reportList) {
		try {
			Element reportsRootRecords = (Element) reportDocument.getElementsByTagName(Constants.XML_ROOT_TAG).item(0);

			for (RecordReport report : reportList) {
				Element reportXmlRecord = reportDocument.createElement(Constants.XML_SUB_TAG);
				reportsRootRecords.appendChild(reportXmlRecord);

				CommonUtility.createElementTag(reportDocument, reportXmlRecord, Constants.RECORD_PROP_NAME,
						report.getName());
				CommonUtility.createElementTag(reportDocument, reportXmlRecord, Constants.RECORD_PROP_EXT,
						report.getExtension());
				CommonUtility.createElementTag(reportDocument, reportXmlRecord, Constants.RECORD_PROP_URL,
						report.getContentUrl());
				CommonUtility.createElementTag(reportDocument, reportXmlRecord, Constants.RECORD_PROP_DOC_TYPE,
						report.getDocumentType());
				CommonUtility.createElementTag(reportDocument, reportXmlRecord, Constants.RECORD_PROP_DOWNLOAD_STATUS,
						String.valueOf(report.isStatus()));
				CommonUtility.createElementTag(reportDocument, reportXmlRecord, Constants.RECORD_PROP_ERROR_MSG,
						report.getErrorMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeXmlToFile(Document document, String filePath, String fileName) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);

		File xmlFile = new File(filePath + Constants.SEPARATOR_FORWARD_SLASH + fileName);
		if (!xmlFile.exists()) {
			boolean status = xmlFile.createNewFile();
			logger.info("File Created Status: " + status);
		}
		StreamResult result = new StreamResult(xmlFile);
		transformer.transform(source, result);
	}

}
