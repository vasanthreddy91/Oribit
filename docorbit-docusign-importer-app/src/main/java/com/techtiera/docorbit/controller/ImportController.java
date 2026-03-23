package com.techtiera.docorbit.controller;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techtiera.docorbit.alfresco.constants.Constants;
import com.techtiera.docorbit.alfresco.services.FileWrapper;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.jfxsupport.FXMLController;
import com.techtiera.docorbit.jfxsupport.ModernAlertUtil;
import com.techtiera.docorbit.util.AppUtil;
import com.techtiera.docorbit.util.CommonUtility;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

@Component
@FXMLController
public class ImportController {

	public static final Logger logger = LoggerFactory.getLogger(ImportController.class);

	@FXML
	private Label csvFilePathLabel;

	@FXML
	private Label contetFilePathLabel;

	@FXML
	private Label targetFilePathLabel;

	@Autowired
	FileWrapper fileWrapper;

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	public Job multithreadedJob;

	@FXML
	private Button importButton;

	@FXML
	private AnchorPane anchorPaneImport;

	@FXML
	private TextArea logTextArea;

	@FXML
	private TabPane setupTabPane;

	@FXML
	private Tab Logtab;

	ProgressIndicator logLoader = new ProgressIndicator();

	@FXML
	private StackPane stackPaneForTextArea;

	@FXML
	private AnchorPane logAnchorPane;

	private BooleanProperty ExportSelected = new SimpleBooleanProperty(false);

	@Autowired
	private ApplicationContext applicationContext;

	@FXML
	private void initialize() {

		String csvFilePath = PropertiesHolder.getCsvFilePath();
		String contentFolderPath = PropertiesHolder.getContentFolderPath();
		String targetFilePath = PropertiesHolder.getNodePath();

		if (csvFilePath != null) {
			csvFilePathLabel.setText(Constants.CSV_FILE_PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES + csvFilePath
					+ Constants.SEPARATOR_DOUBLE_QUOTES);
			csvFilePathLabel.setWrapText(true);
		} else {
			logger.info("CSV File Path is null");
		}

		if (contentFolderPath != null) {
			contetFilePathLabel.setText(Constants.CONTENT_FOLD_PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
					+ contentFolderPath + Constants.SEPARATOR_DOUBLE_QUOTES);
			contetFilePathLabel.setWrapText(true);
		} else {
			logger.info("Content Folder Path is null");
		}

		if (targetFilePath != null) {
			targetFilePathLabel.setText(Constants.TARGET_PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES + targetFilePath
					+ Constants.SEPARATOR_DOUBLE_QUOTES);
			targetFilePathLabel.setWrapText(true);
		} else {
			logger.info("Target Folder Path is null");
		}
		importButton.getStyleClass().remove("selected");

		stackPaneForTextArea.getChildren().add(logTextArea); // one time adding logTextArea, logLoader to Stack Pane
		stackPaneForTextArea.getChildren().add(logLoader);
	}

	@FXML
	public void importButtonAction(ActionEvent event) {
		PropertiesHolder.setLogTextArea(logTextArea);
		PropertiesHolder.setLogTextArea(logTextArea);
		PropertiesHolder.setLogProcessLoader(logLoader);
		importButton.getStyleClass().add("selected");
		logger.info("Clicked on Import Button ---------------------------");
		logger.info("CSV File Path : --- " + PropertiesHolder.getCsvFilePath());
		logger.info("Content Folder Path : --- " + PropertiesHolder.getContentFolderPath());
		logger.info("Target Path : --- " + PropertiesHolder.getNodePath());
//		CommonUtility.logsDisplay("Importing Documents to the Target Path - " + PropertiesHolder.getNodePath(),
//				PropertiesHolder.getLogTextArea());
		StackPane.setAlignment(logLoader, Pos.CENTER);
		stackPaneForTextArea.setPrefSize(logTextArea.getPrefWidth(), logTextArea.getPrefHeight());
		logTextArea.widthProperty().addListener((obs, oldWidth, newWidth) -> {
			stackPaneForTextArea.setPrefWidth(newWidth.doubleValue());
		});
		logTextArea.heightProperty().addListener((obs, oldHeight, newHeight) -> {
			stackPaneForTextArea.setPrefHeight(newHeight.doubleValue());
		});
		logLoader.setVisible(true);
		PropertiesHolder.setLogProcessIndicatorStatus(true);
		importButton.setDisable(true);
		setupTabPane.getSelectionModel().select(Logtab);
		// Create a background task
		Task<Void> task = new Task<Void>() {

			long executionCount = 0;
			long csvRecordCount = 0;

			@Override
			protected Void call() throws Exception {
				try {
					File fileToImport = new File(PropertiesHolder.getCsvFilePath());
					String xmlPath = PropertiesHolder.getContentFolderPath() + Constants.SEPARATOR_FORWARD_SLASH
							+ Constants.XML_TRANS_FILE_NAME;
					File transformationXml = new File(xmlPath);
					// Launch the Batch Job
					JobParametersBuilder builder = new JobParametersBuilder();
					builder.addDate("date", new Date());
					builder.addString("fullPathFileName", fileToImport.getAbsolutePath()).toJobParameters();
					builder.addString("transformationXml", transformationXml.getAbsolutePath()).toJobParameters();
					JobExecution jobExecution = jobLauncher.run(multithreadedJob, builder.toJobParameters());
					logger.info("Execution Status : " + jobExecution.getStatus());
					csvRecordCount = PropertiesHolder.getCsvRecordsCount();
					executionCount = PropertiesHolder.getRecordsExecutionCount();
					logger.info("########## Execution Count :" + executionCount + " ###########");
					logger.info("########## XML Record Count :" + csvRecordCount + " ###########");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				importButton.setDisable(false);
//				CommonUtility.logsDisplay("Total Documents Uploaded -  " + executionCount,
//						PropertiesHolder.getLogTextArea());
				if (csvRecordCount == executionCount) {
					ModernAlertUtil.infoBox(Constants.IMPORT_SUCCESS_MESSAGE + executionCount, Constants.IMPORT_SUCCESS_HEADER,
							Constants.IMPORT_SUCCESS_TITLE);
				} else {
					ModernAlertUtil.showErrorAlert(Constants.IMPORT_FAILURE_TITLE, Constants.IMPORT_FAILURE_MESSAGE);
				}
				ExportSelected.set(true);
				notifyMainController();
			}

			@Override
			protected void failed() {
				super.failed();
				importButton.setDisable(false);
			}
		};
		new Thread(task).start();
	}

	public void notifyMainController() {
		if (ExportSelected.get()) {
			MainController mainController = applicationContext.getBean(MainController.class);
//			mainController.enableReportsButton(true);
		}
	}
}
