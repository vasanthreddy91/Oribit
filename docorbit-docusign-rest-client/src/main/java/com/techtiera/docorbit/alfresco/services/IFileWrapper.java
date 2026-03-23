package com.techtiera.docorbit.alfresco.services;

import java.io.IOException;

import org.alfresco.core.model.Node;

public interface IFileWrapper {

	public boolean execute(String fileName, String title, String description, String relativeFolderPath,
			String filePath) throws IOException;

	public boolean uploadFile(String parentFolderId, String fileName, String title, String description,
			String relativeFolderPath, String filePath);

	public Node createTextFile(String parentFolderId, String fileName, String title, String description,
			String relativeFolderPath, String textContent);

	public Node createFileMetadata(String parentFolderId, String fileName, String title, String description,
			String relativeFolderPath);

}
