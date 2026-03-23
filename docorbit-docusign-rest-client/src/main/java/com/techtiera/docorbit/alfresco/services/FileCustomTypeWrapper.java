package com.techtiera.docorbit.alfresco.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.AssociationBody;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.NodeEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techtiera.docorbit.alfresco.constants.Constants;
import com.techtiera.docorbit.batch.config.Record;
import com.techtiera.docorbit.batch.config.Version;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.resource.Property;
import com.techtiera.docorbit.resource.ReportDetails;
import com.techtiera.docorbit.resource.Type;
import com.techtiera.docorbit.util.CommonUtility;

@Component
public class FileCustomTypeWrapper {
	static final Logger LOGGER = LoggerFactory.getLogger(FileCustomTypeWrapper.class);

	private String contentType = "acme:document"; // Set custom content type
	private Boolean autoRename = false;
	private Boolean majorVersion = true;
	private Boolean versioningEnabled = false;
	private String updateComment = null;
	private String updatedName = null;
	private List<String> include = null;
	private List<String> fields = null;

	@Autowired
	NodesApi nodesApi;

	@Autowired
	GetNodeMetadataWrapper getNodeMetadataWrapper;

	@Autowired
	UploadNewFileVersionWrapper uploadNewFileVersionWrapper;

	/**
	 * Upload a file from disk
	 */
	@SuppressWarnings("unused")
	public ReportDetails uploadFile(Record record, String relativeFolderPath, String filePath, String xfdfNodeId) {
		ReportDetails uploadStatus = new ReportDetails();
		String parentFolderId = "-root-";
		LOGGER.info("relative path : " + relativeFolderPath + Constants.SEPARATOR_FORWARD_SLASH + record.getName());

		NodeEntry nodeExists = getNodeMetadataWrapper
				.getNodeDetailed(relativeFolderPath + Constants.SEPARATOR_FORWARD_SLASH + record.getName());

		if (nodeExists == null) {
			if (record.getVersions() != null && !record.getVersions().isEmpty()) {
				String path = CommonUtility.getFileNameWithOutExtension(filePath);
				String extension = CommonUtility.getFileExtension(filePath);
				List<Version> versions = record.getVersions();
				versions.sort((r1, r2) -> r1.getNumber().compareTo(r2.getNumber()));
				int count = 0;
				String fileFullPath = null;
				for (Version version : versions) {
					LOGGER.info("count : " + count);
					LOGGER.info("version number : " + version.getNumber());
					String documentName = version.getDocumentName();
					String documentExtension = FilenameUtils.getExtension(documentName);

					System.out.println("Path ==> " + path);
//					System.out.println("Path ==> "+ path);
					System.out.println("Version Document name => " + documentName);
					System.out.println("Version Document Extension => " + documentExtension);

					if (count == 0) {
						// First version
						String fileFirstPath = path + Constants.SEPARATOR_HYPEN + version.getNumber()
								+ Constants.SEPARATOR_DOT + documentExtension;
						String fileSecondPath = path + Constants.SEPARATOR_DOT + documentExtension;
						File someFile = new File(fileFirstPath);
						File newFile = new File(fileSecondPath);
						File fileToUse = null;

						if (someFile.exists()) {
							fileToUse = someFile;
						} else if (newFile.exists()) {
							fileToUse = newFile;
						} else {
							LOGGER.error("Missing file for version " + version.getNumber() + ": " + fileFirstPath
									+ " or " + fileSecondPath);
//							CommonUtility.logsDisplay(
//									CommonUtility.formatDateTime() + " - ERROR: Missing file for version "
//											+ version.getNumber() + ". Skipping to next version.",
//									PropertiesHolder.getLogTextArea());
							count++;
							continue;
						}

						// File exists, proceed to upload
						Node fileNode = createFileMetadata(parentFolderId, relativeFolderPath, record, version,
								xfdfNodeId);
						try {
							byte[] fileData = FileUtils.readFileToByteArray(fileToUse);
							Node updatedFileNode = nodesApi.updateNodeContent(fileNode.getId(), fileData, majorVersion,
									updateComment, updatedName, include, fields).getBody().getEntry();

							if (updatedFileNode != null) {
								nodeExists = getNodeMetadataWrapper.getNodeDetailed(relativeFolderPath
										+ Constants.SEPARATOR_FORWARD_SLASH + version.getDocumentName());
								uploadStatus.setStatus(Boolean.TRUE);
//								CommonUtility.logsDisplay(CommonUtility.formatDateTime() + " - " + fileToUse.getName()
//										+ " is uploaded Successfully.", PropertiesHolder.getLogTextArea());
							}
						} catch (IOException e) {
							LOGGER.error("Failed to upload content for " + fileToUse.getAbsolutePath() + ": "
									+ e.getMessage());
//							CommonUtility.logsDisplay(
//									CommonUtility.formatDateTime() + " - ERROR: Failed to upload file "
//											+ fileToUse.getName() + ". Skipping version.",
//									PropertiesHolder.getLogTextArea());
							count++;
							continue;
						}
					} else {
						// Other versions
						if (count == versions.size() - 1) {
							fileFullPath = filePath;
						} else {
							fileFullPath = path + Constants.SEPARATOR_HYPEN + version.getNumber()
									+ Constants.SEPARATOR_DOT + documentExtension;
						}

						LOGGER.info("Version fileFullPath : " + fileFullPath);
						File versionFile = new File(fileFullPath);
						if (!versionFile.exists()) {
							LOGGER.error("Version file not found: " + fileFullPath);
//							CommonUtility
//									.logsDisplay(
//											CommonUtility.formatDateTime() + " - ERROR: File not found for version "
//													+ version.getNumber() + ". Skipping.",
//											PropertiesHolder.getLogTextArea());
							count++;
							continue;
						}

						Node node = nodeExists.getEntry();
						node.setProperties(record.getProperties());

						try {

							uploadNewFileVersionWrapper.uploadNewFileVersion(node.getId(), fileFullPath, version);
//							CommonUtility.logsDisplay(CommonUtility.formatDateTime() + " - "
//									+ Paths.get(fileFullPath).getFileName().toString() + " is uploaded Successfully.",
//									PropertiesHolder.getLogTextArea());
						} catch (Exception e) {
							LOGGER.error("Error uploading version file: " + fileFullPath + " - " + e.getMessage());
//							CommonUtility
//									.logsDisplay(
//											CommonUtility.formatDateTime() + " - ERROR: Failed to upload version "
//													+ version.getNumber() + ". Skipping.",
//											PropertiesHolder.getLogTextArea());
						}
					}
					count++;
				}
				if (count == record.getVersions().size()) {
					uploadStatus.setStatus(Boolean.TRUE);
				}
			} else {
				// No versions
				LOGGER.info("No versions are available in the xml property");

				File someFile = new File(filePath);
				if (!someFile.exists()) {
					LOGGER.error("File not found: " + filePath);
//					CommonUtility
//							.logsDisplay(
//									CommonUtility.formatDateTime() + " - ERROR: File not found. Skipping this record: "
//											+ Paths.get(filePath).getFileName().toString(),
//									PropertiesHolder.getLogTextArea());
					uploadStatus.setStatus(Boolean.FALSE);
					uploadStatus.setErrorMessage("File not found: " + filePath);
					return uploadStatus;
				}

				// Create metadata and upload only if file exists
				Node fileNode = createFileMetadata(parentFolderId, relativeFolderPath, record, null, xfdfNodeId);
				try {
					byte[] fileData = FileUtils.readFileToByteArray(someFile);
					Node updatedFileNode = nodesApi.updateNodeContent(fileNode.getId(), fileData, majorVersion,
							updateComment, updatedName, include, fields).getBody().getEntry();

					if (updatedFileNode != null) {
						nodeExists = getNodeMetadataWrapper.getNodeDetailed(
								relativeFolderPath + Constants.SEPARATOR_FORWARD_SLASH + record.getName());
						uploadStatus.setStatus(Boolean.TRUE);
//						CommonUtility.logsDisplay(CommonUtility.formatDateTime() + " - "
//								+ Paths.get(filePath).getFileName().toString() + " uploaded successfully.",
//								PropertiesHolder.getLogTextArea());
					}
				} catch (IOException e) {
					LOGGER.error("Error reading file content: " + filePath + " - " + e.getMessage());
					uploadStatus.setStatus(Boolean.FALSE);
					uploadStatus.setErrorMessage("Failed to read file content: " + filePath);
				}
			}
		} else {
			uploadStatus.setStatus(Boolean.FALSE);
			uploadStatus.setErrorMessage("File already exists in the folder : " + relativeFolderPath
					+ Constants.SEPARATOR_FORWARD_SLASH + record.getName());
			LOGGER.info("File already exists in the folder : " + relativeFolderPath + Constants.SEPARATOR_FORWARD_SLASH
					+ record.getName());
		}
		return uploadStatus;
	}

	/**
	 * Create metadata for a file node
	 *
	 * @param parentFolderId     the parent folder node ID where the file should be
	 *                           stored
	 * @param fileName           the name for the new file
	 * @param title              the title property value for the new file
	 * @param description        the description property value for the new file
	 * @param relativeFolderPath path relative to /Company Home
	 * @return a Node object with file metadata and the Node ID
	 */
	private Node createFileMetadata(String parentFolderId, String relativeFolderPath, Record record, Version version,
			String xfdfNodeId) {
		Node fileNode = null;
		Type transXmlData = PropertiesHolder.getTransXmlData();
		if (transXmlData != null) {
			Map<String, Object> fileProps = new HashMap<>();
			for (Property property : transXmlData.getProperties()) {
				for (com.techtiera.docorbit.batch.config.Property recordProperties : record.getProperties()) {
					if (property.getSource().equals(recordProperties.getName())) {
						fileProps.put(property.getTarget(), recordProperties.getValue());
					}
					// FileNet DocumentTitle Property
					if (recordProperties.getName().equalsIgnoreCase("DocumentTitle")) {
						fileProps.put("cm:title", recordProperties.getValue());
					}
				}
			}
			if (record.getIsFileAnnotated().equals("true")) {
				fileProps.put("oa:isAnnotated", Boolean.TRUE);
			}
			NodeBodyCreate nodeBodyCreate = new NodeBodyCreate();
			if (version != null) {
				nodeBodyCreate.setName(version.getDocumentName());
			} else {
				nodeBodyCreate.setName(record.getName());
			}
			nodeBodyCreate.setNodeType(transXmlData.getTargetType());
			// nodeBodyCreate.setAspectNames(fileAspects);
			nodeBodyCreate.setProperties(fileProps);
			nodeBodyCreate.setRelativePath(relativeFolderPath);

			if (record.getIsFileAnnotated().equals("true")) {
				List<AssociationBody> peer2peerAssociations = new ArrayList<>();
				AssociationBody peer2peerAssoc = new AssociationBody();
				peer2peerAssoc.assocType("oa:annotates");
				peer2peerAssoc.setTargetId(xfdfNodeId);
				peer2peerAssociations.add(peer2peerAssoc);
				nodeBodyCreate.setTargets(peer2peerAssociations);
			}

			// Create the file node metadata
			fileNode = nodesApi.createNode(parentFolderId, nodeBodyCreate, autoRename, majorVersion, versioningEnabled,
					include, fields).getBody().getEntry();
		} else {
			LOGGER.info("Transformation xml file data is empty");
		}
		return fileNode;
	}

	public String uploadXfdfFile(Record record, String relativeFolderPath, String filePath) {
		String parentFolderId = "-root-";
		Node fileNode = null;
		LOGGER.info("relative path : " + relativeFolderPath + Constants.SEPARATOR_FORWARD_SLASH
				+ record.getAssociationId() + Constants.ALF_ANNOTATION_FILE_EXTENSION);
		boolean nodeExists = getNodeMetadataWrapper.getNode(relativeFolderPath + Constants.SEPARATOR_FORWARD_SLASH
				+ record.getAssociationId() + Constants.ALF_ANNOTATION_FILE_EXTENSION);
		if (!nodeExists) {
			fileNode = createXfdfFileMetadata(parentFolderId, relativeFolderPath, record);
			// Get the file bytes
			File someFile = new File(filePath);
			byte[] fileData = null;
			try {
				fileData = FileUtils.readFileToByteArray(someFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Add the file node content
			Node updatedFileNode = nodesApi.updateNodeContent(fileNode.getId(), fileData, majorVersion, updateComment,
					updatedName, include, fields).getBody().getEntry();
			if (updatedFileNode != null) {
				LOGGER.info("XFDF file is created successfully with Node Id : " + fileNode.getId());
				return fileNode.getId();
			}
		} else {
			LOGGER.info("Read file multiple time : " + relativeFolderPath + "/" + record.getName());
		}
		return fileNode.getId();
	}

	private Node createXfdfFileMetadata(String parentFolderId, String relativeFolderPath, Record record) {
		String xfdfFileName = record.getAssociationId() + Constants.ALF_ANNOTATION_FILE_EXTENSION;
		Map<String, String> fileProps = new HashMap<>();
		fileProps.put("cm:autoVersion", "false");
		fileProps.put("cm:versionLabel", "1.0");
		fileProps.put("oa:annotatedVersion", "1.0");

		NodeBodyCreate nodeBodyCreate = new NodeBodyCreate();
		nodeBodyCreate.setName(xfdfFileName);
		nodeBodyCreate.setNodeType(Constants.ALF_ANNOTATION_TYPE);
		nodeBodyCreate.setProperties(fileProps);
		nodeBodyCreate.setRelativePath(relativeFolderPath);

		// Create the file node metadata
		Node fileNode = nodesApi.createNode(parentFolderId, nodeBodyCreate, autoRename, majorVersion, versioningEnabled,
				include, fields).getBody().getEntry();
		return fileNode;
	}
}
