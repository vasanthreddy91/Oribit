package com.techtiera.docorbit.alfresco.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.Node;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techtiera.docorbit.batch.config.Version;

@Component
public class UploadNewFileVersionWrapper {
	static final Logger LOGGER = LoggerFactory.getLogger(UploadNewFileVersionWrapper.class);

	private Boolean majorVersion = true;
	private String updateComment = null;
	private String updatedName = null;
	private List<String> include = null;
	private List<String> fields = null;

	@Autowired
	NodesApi nodesApi;

	public void execute(String textFileNodeId, String binFileNodeId) throws IOException {
		// Update text content for a file
		// Node newTextFile = updateTextFileContent(textFileNodeId, "Some UPDATED text
		// for the file");

		// Upload a file as new content
		// Node newFile = uploadNewFileVersion(binFileNodeId, "updatedpicture.png");
	}

	/**
	 * Upload a file from disk as a new version
	 */
	public boolean uploadNewFileVersion(String fileNodeId, String filePath, Version version) {
		// Get the file bytes
		File someFile = new File(filePath);
		byte[] fileData = null;
		try {
			fileData = FileUtils.readFileToByteArray(someFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("fileNodeId ==> " + fileNodeId);
		updatedName = version.getDocumentName();

		// Update the file node content
		Node updatedFileNode = nodesApi
				.updateNodeContent(fileNodeId, fileData, majorVersion, updateComment, updatedName, include, fields)
				.getBody().getEntry();
		if (updatedFileNode != null) {
			LOGGER.info("Uploaded new content for file: {}", updatedFileNode.toString());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Update text content for a file
	 */
	private Node updateTextFileContent(String fileNodeId, String textContent) {
		// Update the file node content
		Node updatedFileNode = nodesApi.updateNodeContent(fileNodeId, textContent.getBytes(), majorVersion,
				updateComment, updatedName, include, fields).getBody().getEntry();

		LOGGER.info("Updated text content for file: {}", updatedFileNode.toString());

		return updatedFileNode;
	}
}
