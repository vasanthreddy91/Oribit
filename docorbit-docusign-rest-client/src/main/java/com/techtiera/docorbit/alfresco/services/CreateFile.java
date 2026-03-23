package com.techtiera.docorbit.alfresco.services;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CreateFile {
    static final Logger LOGGER = LoggerFactory.getLogger(CreateFile.class);

    private List<String> include = null;
    private List<String> fields = null;
    private String contentType = "cm:content"; // Out-of-the-box content type for generic file content
    private Boolean autoRename = true;
    private Boolean majorVersion = true;
    private Boolean versioningEnabled = true;
    private String updateComment = null;
    private String updatedName = null;

    @Autowired
    NodesApi nodesApi;

    public void execute() throws IOException {
        // Create a text file under the /Company Home/Guest Home folder
        Node newTextFile = createTextFile("-root-", "somestuff.txt", "TextfileTitle",
                "TextfileDesc", "/Guest Home", "Some text for the file");

        // Upload a file from disk to the '/Company Home/Guest Home' folder, the file that is being uploaded, 'somepicture.png' 
        // in this case, is located in the same directory as we are running the app from
        Node newFile = uploadFile("-root-", "somepicture.png", "PicturefileTitle",
                "PicturefileDesc", "/Guest Home", "somepicture.png");
    }

    /**
     * Upload a file from disk
     */
    private Node uploadFile(String parentFolderId, String fileName, String title, String description,
                            String relativeFolderPath, String filePath) {
        Node fileNode = createFileMetadata(parentFolderId, fileName, title, description, relativeFolderPath);

        // Get the file bytes
        File someFile = new File(filePath);
        byte[] fileData = null;
        try {
            fileData = FileUtils.readFileToByteArray(someFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the file node content
        Node updatedFileNode = nodesApi.updateNodeContent(fileNode.getId(),
                fileData, majorVersion, updateComment, updatedName, include, fields).getBody().getEntry();

        LOGGER.info("Created file with content: {}", updatedFileNode.toString());

        return updatedFileNode;
    }

    /**
     * Create a text file
     */
    private Node createTextFile(String parentFolderId, String fileName, String title, String description,
                                String relativeFolderPath, String textContent) {
        Node fileNode = createFileMetadata(parentFolderId, fileName, title, description, relativeFolderPath);

        // Add the file node content
        Node updatedFileNode = nodesApi.updateNodeContent(fileNode.getId(),
                textContent.getBytes(), majorVersion, updateComment, updatedName, include, fields).getBody().getEntry();

        LOGGER.info("Created file with content: {}", updatedFileNode.toString());

        return updatedFileNode;
    }

    /**
     * Create metadata for a file node
     *
     * @param parentFolderId the parent folder node ID where the file should be stored
     * @param fileName the name for the new file
     * @param title the title property value for the new file
     * @param description the description property value for the new file
     * @param relativeFolderPath path relative to /Company Home
     * @return a Node object with file metadata and the Node ID
     */
    private Node createFileMetadata(String parentFolderId, String fileName, String title, String description,
                                    String relativeFolderPath) {
        List<String> fileAspects = new ArrayList<String>();
        fileAspects.add("cm:titled");
        Map<String, String> fileProps = new HashMap<>();
        fileProps.put("cm:title", title);
        fileProps.put("cm:description", description);
        
        NodeBodyCreate nodeBodyCreate = new NodeBodyCreate();
        nodeBodyCreate.setName(fileName);
        nodeBodyCreate.setNodeType(contentType);
        nodeBodyCreate.setAspectNames(fileAspects);
        nodeBodyCreate.setProperties(fileProps);
        nodeBodyCreate.setRelativePath(relativeFolderPath);

        // Create the file node metadata
        Node fileNode = nodesApi.createNode(parentFolderId, nodeBodyCreate, autoRename, majorVersion, versioningEnabled,
                include, fields).getBody().getEntry();

        return fileNode;
    }
}
