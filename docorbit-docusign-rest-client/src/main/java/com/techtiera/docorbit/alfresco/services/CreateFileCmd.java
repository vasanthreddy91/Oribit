package com.techtiera.docorbit.alfresco.services;

import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class CreateFileCmd {
	static final Logger LOGGER = LoggerFactory.getLogger(CreateFileCmd.class);

	@Autowired
	NodesApi nodesApi;

	public void execute(String parentFolderId, String fileName) throws IOException {
		// Get the parent folder where file should be stored
		Node parentFolderNode = Objects.requireNonNull(nodesApi.getNode(parentFolderId, null, null, null).getBody())
				.getEntry();
		LOGGER.info("Got parent folder node: {}", parentFolderNode.toString());

		// Create the file node metadata
		Node fileNode = Objects.requireNonNull(nodesApi.createNode(parentFolderNode.getId(),
				new NodeBodyCreate().nodeType("cm:content").name(fileName), null, null, null, null, null).getBody())
				.getEntry();

		// Add the file node content
		Node updatedFileNode = Objects.requireNonNull(nodesApi.updateNodeContent(fileNode.getId(),
				"Some text for this file...".getBytes(), true, null, null, null, null).getBody()).getEntry();

		LOGGER.info("Created file with content: {}", updatedFileNode.toString());
	}
}
