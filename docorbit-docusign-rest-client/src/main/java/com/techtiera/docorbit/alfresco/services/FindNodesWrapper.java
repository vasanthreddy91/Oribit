package com.techtiera.docorbit.alfresco.services;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.core.handler.QueriesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeEntry;
import org.alfresco.core.model.NodePagingList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindNodesWrapper {
    static final Logger LOGGER = LoggerFactory.getLogger(FindNodesWrapper.class);

    @Autowired
    QueriesApi queriesApi;

    public Node execute(String nodeName, String parentId) throws IOException {
    	Node resultNode = null;
    	String rootNodeId = "-root-"; // The id of the node to start the search from. Supports the aliases -my-, -root- and -shared-.
        Integer skipCount = 0;
        Integer maxItems = 100;

        // Restrict the returned results to only those of the given node type and its sub-types
        String nodeType = "cm:folder";
        List<String> orderBy = null;
        List<String> fields = null;
        List<String> include = new ArrayList<>();
        include.add("path");

        NodePagingList result = queriesApi.findNodes(
        		nodeName, rootNodeId, skipCount, maxItems, nodeType, include, orderBy, fields).getBody().getList();
        for (NodeEntry node: result.getEntries()) {
            if(node.getEntry().getName().equals(nodeName) && node.getEntry().getParentId().equals(parentId)) {
            	resultNode = node.getEntry();
            	break;
            }
        }
		return resultNode;
    }
}
