package com.techtiera.docorbit.alfresco.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.NodeChildAssociationEntry;
import org.alfresco.core.model.NodeChildAssociationPagingList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techtiera.docorbit.resource.AlfrescoProperties;

@Component
public class ListFolderContentWrapper {
	static final Logger LOGGER = LoggerFactory.getLogger(ListFolderContentWrapper.class);

	@Autowired
	NodesApi nodesApi;

	@SuppressWarnings("unchecked")
	public List<AlfrescoProperties> getNodesList(String relativeFolderPath) {
		List<AlfrescoProperties> alfrescoPropertiesList = new ArrayList<AlfrescoProperties>();
		String rootNodeId = "-root-";
		Integer skipCount = 0;
		Integer maxItems = 100;
        List<String> include = new ArrayList<>();
        include.add("properties");
		List<String> fields = null;
		List<String> orderBy = null;
		String where = "(isFolder=true)";
		Boolean includeSource = false;

		LOGGER.info("Listing folder {}{}", rootNodeId, relativeFolderPath);
		NodeChildAssociationPagingList result = nodesApi.listNodeChildren(rootNodeId, skipCount, maxItems, orderBy,
				where, include, relativeFolderPath, includeSource, fields).getBody().getList();
		for (NodeChildAssociationEntry childNodeAssoc : result.getEntries()) {
			AlfrescoProperties alfrescoProperties = new AlfrescoProperties();
			alfrescoProperties.setNodeId(childNodeAssoc.getEntry().getId());
			alfrescoProperties.setName(childNodeAssoc.getEntry().getName());
			alfrescoProperties.setParentId(childNodeAssoc.getEntry().getParentId());
			Map<String, Object> nodeProperties = (Map<String, Object>) childNodeAssoc.getEntry().getProperties();
			if (nodeProperties != null && nodeProperties.containsKey("cm:title")) {
		        alfrescoProperties.setTitle(nodeProperties.get("cm:title").toString());
		    } else {
		        LOGGER.info("ChildNodeAssoc cm:title not found or properties are null.");
		    }
			alfrescoPropertiesList.add(alfrescoProperties);
		}
		return alfrescoPropertiesList;
	}
	
}
