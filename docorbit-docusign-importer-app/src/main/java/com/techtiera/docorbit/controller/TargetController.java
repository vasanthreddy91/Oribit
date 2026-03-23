package com.techtiera.docorbit.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;
import org.alfresco.core.model.Node;
import com.techtiera.docorbit.alfresco.constants.Constants;
import com.techtiera.docorbit.alfresco.services.ListFolderContentWrapper;
import com.techtiera.docorbit.config.PropertiesConfig;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.jfxsupport.FXMLController;
import com.techtiera.docorbit.jfxsupport.ModernAlertUtil;
import com.techtiera.docorbit.resource.AlfrescoProperties;
import com.techtiera.docorbit.util.AppUtil;
import com.techtiera.docorbit.util.CommonUtility;
import com.techtiera.docorbit.alfresco.services.FindNodesWrapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

@FXMLController
public class TargetController {

	private static final Logger logger = LoggerFactory.getLogger(TargetController.class);

	@Autowired
	private PropertiesConfig config;

	@FXML
	private SplitPane targetSplitPane;

	@FXML
	private Label targetPathLabel;

	@Autowired
	private ListFolderContentWrapper listFolderContentWrapper;

	@Autowired
	private FindNodesWrapper findNodesWrapper;

	@Autowired
	private ApplicationContext applicationContext;

	private BooleanProperty targetPathSelected = new SimpleBooleanProperty(false);

	@FXML
	private void initialize() {
		buildTreeStructure();
	}

	private void buildTreeStructure() {
		try {
			List<AlfrescoProperties> nodesList = listFolderContentWrapper
					.getNodesList(Constants.SEPARATOR_FORWARD_SLASH + Constants.rootSite);
			logger.info("nodesList - " + nodesList);
			if (config.getJavafxMainTree()) {
				if (!ObjectUtils.isEmpty(nodesList)) {
					TreeItem<String> rootItem = new TreeItem<>(Constants.rootSite);
					rootItem.setExpanded(true);
					for (AlfrescoProperties node : nodesList) {
						TreeItem<String> itemNode = new TreeItem<>(node.getName());
						itemNode.setExpanded(true);
						rootItem.getChildren().add(itemNode);
						addDynamicChildListeners(node.getName(), node.getParentId(), itemNode);
					}
					TreeView<String> treeView = new TreeView<>(rootItem);
					targetSplitPane.getItems().add(0, treeView);
					treeView.getSelectionModel().selectedItemProperty()
							.addListener((observable, oldValue, newValue) -> {
								if (newValue != null) {
									handleNodeSelection(newValue);
								}
							});
				} else {
					ModernAlertUtil.showErrorAlert("Failed", "Failed while getting nodes");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addDynamicChildListeners(String nodeName, String parentId, TreeItem<String> itemNode) {
		logger.info("itemNode - " + itemNode);
		itemNode.expandedProperty().addListener((observable, oldValue, newValue) -> {
			logger.info("Node " + itemNode.getValue() + " expanded: " + newValue);
			if (newValue) {
				logger.info("Node Expanded: " + itemNode.getValue());
				loadSubfoldersIfNeeded(nodeName, parentId, itemNode);
			}
		});
	}

	private void loadSubfoldersIfNeeded(String nodeName, String parentId, TreeItem<String> parentNode) {
		try {
			logger.info("nodeName - " + nodeName + " parentId - " + parentId + " parentNode - " + parentNode);
			logger.info("PropertiesHolder.getNodePath()---" + PropertiesHolder.getNodePath());
			List<AlfrescoProperties> subfolders = listFolderContentWrapper.getNodesList(nodeName);
			if (subfolders != null && !subfolders.isEmpty()) {
				for (AlfrescoProperties subfolder : subfolders) {
					TreeItem<String> childNode = new TreeItem<>(subfolder.getName());
					parentNode.getChildren().add(childNode);
					String subfolderPath = nodeName + Constants.SEPARATOR_FORWARD_SLASH + subfolder.getName();
					logger.info("Added child node: " + parentNode + " to subfolder.getName(): " + subfolder.getName());
					loadSubfoldersIfNeeded(subfolderPath, subfolder.getParentId(), childNode);
				}
			} else {
				logger.info("No subfolders found for " + parentNode.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getParentIdOfNode(String nodeName) {
		try {
			List<AlfrescoProperties> nodesList = listFolderContentWrapper
					.getNodesList(Constants.SEPARATOR_FORWARD_SLASH + Constants.rootSite);

			for (AlfrescoProperties node : nodesList) {
				if (node.getName().equals(nodeName)) {
					return node.getParentId();
				}
			}
		} catch (Exception e) {
			logger.error("Error while fetching parentId for node: " + nodeName, e);
		}
		return null;
	}

	private void handleNodeSelection(TreeItem<String> selectedNode) {
		logger.info("Selected Node: " + selectedNode.getValue());
		String selectedPath = buildSelectionPath(selectedNode);
		PropertiesHolder.setNodePath(Constants.SEPARATOR_FORWARD_SLASH + Constants.rootSite + selectedPath);
		targetPathLabel.setText(Constants.TARGET_PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
				+ PropertiesHolder.getNodePath() + Constants.SEPARATOR_DOUBLE_QUOTES);
		targetPathLabel.setWrapText(true);
		targetPathLabel.setVisible(true);

		if (targetPathLabel != null) {
			logger.info("Target Path is selected");
			targetPathSelected.set(true);
			notifyMainController();
		}
		String nodeName = selectedNode.getValue();
		;
		String parentId = getParentIdOfNode(nodeName);
		if (parentId != null) {
			loadSubfoldersIfNeeded(PropertiesHolder.getNodePath(), parentId, selectedNode);
		} else {
			logger.warn("Parent ID not found for node: " + nodeName);
		}
	}

	private String buildSelectionPath(TreeItem<String> selectedNode) {
		StringBuilder pathBuilder = new StringBuilder();
		TreeItem<String> currentNode = selectedNode;
		while (currentNode != null) {
			if (!currentNode.getValue().equals(Constants.rootSite)) {
				if (pathBuilder.length() > 0) {
					pathBuilder.insert(0, Constants.SEPARATOR_FORWARD_SLASH);
				}
				pathBuilder.insert(0, currentNode.getValue());
			}
			currentNode = currentNode.getParent();
		}
		return Constants.SEPARATOR_FORWARD_SLASH + pathBuilder.toString();
	}

	private void notifyMainController() {
		if (targetPathSelected.get()) {
			MainController mainController = applicationContext.getBean(MainController.class);
//			mainController.enableTransformationButton(true);
		}
	}
}
