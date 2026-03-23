package com.techtiera.docorbit.alfresco.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.NodeBodyCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateFolderWrapper {
	static final Logger LOGGER = LoggerFactory.getLogger(CreateFolderWrapper.class);

	@Autowired
	NodesApi nodesApi;

	/**
	 * Make the remote call to create a folder in the repository, if it does not
	 * exist.
	 *
	 * @param folderName         the name of the folder
	 * @param folderTitle        the title of the folder
	 * @param folderDescription  the description of the folder
	 * @param relativeFolderPath path relative to /Company Home
	 * @return a node object for the newly created node, contains the ID, such as
	 *         e859588c-ae81-4c5e-a3b6-4c6109b6c905
	 */
	public boolean createFolder(String folderName, String folderTitle, String folderDescription,
			String relativeFolderPath) throws IOException {
		Objects.requireNonNull(folderName);

		String rootPath = "-root-"; // /Company Home
		String folderType = "cm:folder"; // Standard out-of-the-box folder type

		List<String> folderAspects = new ArrayList<String>();
		folderAspects.add("cm:titled");
		Map<String, String> folderProps = new HashMap<>();
		folderProps.put("cm:title", folderTitle);
		folderProps.put("cm:description", folderDescription);

		String nodeId = rootPath; // The id of a node. You can also use one of these well-known aliases: -my-,
									// -shared-, -root-
		NodeBodyCreate nodeBodyCreate = new NodeBodyCreate();
		nodeBodyCreate.setName(folderName);
		nodeBodyCreate.setNodeType(folderType);
		nodeBodyCreate.setAspectNames(folderAspects);
		nodeBodyCreate.setProperties(folderProps);
		nodeBodyCreate.setRelativePath(relativeFolderPath);

		List<String> include = null;
		List<String> fields = null;
		Boolean autoRename = false;
		Boolean majorVersion = true;
		// Should versioning be enabled at all?
		Boolean versioningEnabled = false;

		try {
			nodesApi.createNode(nodeId, nodeBodyCreate, autoRename, majorVersion, versioningEnabled, include, fields)
					.getBody().getEntry();
			return true;
		} catch (Exception e) {
			LOGGER.error("Error while creating node folder : "+folderName);
			return false;
		}
	}

}
