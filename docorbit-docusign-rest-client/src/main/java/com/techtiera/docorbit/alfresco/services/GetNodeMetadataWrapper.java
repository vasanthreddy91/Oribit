package com.techtiera.docorbit.alfresco.services;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.NodeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetNodeMetadataWrapper {
	static final Logger LOGGER = LoggerFactory.getLogger(GetNodeMetadataWrapper.class);

	@Autowired
	NodesApi nodesApi;

	/**
	 * Get a node (file/folder).
	 *
	 * @param nodeId             the id of the node that we want to fetch metadata
	 *                           for. If relativeFolderPath is specified, then
	 *                           metadata for this node will be returned. Besides
	 *                           node ID the aliases -my-, -root- and -shared- are
	 *                           also supported.
	 * @param relativeFolderPath A path relative to the nodeId, if this is not null,
	 *                           then metadata is returned on the node resolved by
	 *                           this path
	 * @return Node object if exist, or null if does not exist
	 */
	public boolean getNode(String relativeFolderPath) {
		String nodeId = "-root-";
		List<String> include = null;
		List<String> fields = null;
		
		try {
			NodeEntry result = nodesApi.getNode(nodeId, include, relativeFolderPath, fields).getBody();
			if (result != null) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	public NodeEntry getNodeDetailed(String relativeFolderPath) {
		String nodeId = "-root-";
		NodeEntry result = null;
		List<String> include = new ArrayList<>();
        include.add("properties");
		List<String> fields = null;
		
		try {
			result = nodesApi.getNode(nodeId, include, relativeFolderPath, fields).getBody();
			if (result != null) {
				return result;
			}
		} catch (Exception e) {
			return result;
		}
		return result;
	}
}
