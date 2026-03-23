package com.techtiera.docorbit.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.techtiera.docorbit.alfresco.constants.Constants;
import com.techtiera.docorbit.alfresco.services.AlfescoRestServiceUtility;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.jfxsupport.FXMLController;
import com.techtiera.docorbit.jfxsupport.ModernAlertUtil;
import com.techtiera.docorbit.util.CommonUtility;
import com.techtiera.docorbit.util.RecordProperties;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

@FXMLController
public class SourceController {

	private static final Logger logger = LoggerFactory.getLogger(SourceController.class);

	@FXML
	private TabPane setupTabPane;

	@FXML
	private AnchorPane setupAnchorPane;

	@FXML
	private Label contentFilePathLabel;

	@FXML
	private Button btnEnvelopContentFile;

	@FXML
	private Button btnTemplateContentFile;

	@FXML
	private Label csvFilePathLabel;

	@FXML
	private Label totalRecordsCount;

	@FXML
	private Button btnCSVFile;

	@FXML
	private TableView<String> csvTableView;

	@FXML
	private TableView<RecordProperties> recordTableView;

	private File csvFile;

	private File userFile;

	private File lastSelectedDirectory;

	@Autowired
	private ApplicationContext applicationContext;

	private boolean xmlValid = false;

	@FXML
	private Button btnReportsPath;

	@FXML
	private Label ReportsFilePath;

	private File reportsFolderPath;

	private File templateReportsFolderPath;

	private File envelopeReportsFolderPath;

	@FXML
	private Label labelUserLocation;

	@FXML
	private Label labelGroupLocation;

	@FXML
	private Label labelProfilesLocation;

	@FXML
	private Label labelTemplateReportLocation;

	@FXML
	private Label labelEnvelopeReportLocation;

	@FXML
	private Label labelTemplateLocation;

	@FXML
	private Label labelEnvelopeLocation;

	@FXML
	private Label labelEnvelopContentLocation;

	@FXML
	private Label labelTemplateContentLocation;

	@FXML
	private Button importButton;

	@FXML
	private Button importTemplateButton;

	@FXML
	private Button importEnvelopeButton;

	@FXML
	private Button importUserButton;

	@FXML
	private Button groupImportButton;

	@FXML
	private Label totalUserRecordsCount;

	@FXML
	private Label totalTemplateRecordsCount;

	@FXML
	private Label totalEnvelopeRecordsCount;

	@FXML
	private Label totalGroupRecordsCount;

	@FXML
	private TableView<RecordProperties> userRecordTableView;

	@FXML
	private TableView<RecordProperties> templateRecordTableView;

	@FXML
	private TableView<RecordProperties> envelopeRecordTableView;

	@FXML
	private TableView<RecordProperties> groupRecordTableView;

	private File groupFile;

	private File templateFile;

	private File profileFile;

	private File envelopeFile;

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	public Job multithreadedJob;

	@Autowired
	public Job userMultithreadedJob;

	@Autowired
	public Job templateMultithreadedJob;

	@Autowired
	public Job envelopMultithreadedJob;

	@Autowired
	public Job groupMultithreadedJob;

	@Autowired
	private AlfescoRestServiceUtility alfescoRestServiceUtility;

	@FXML
	private TextArea logTextArea;

	@FXML
	private StackPane stackPaneForTextArea;

	ProgressIndicator logLoader = new ProgressIndicator();

	@FXML
	private TabPane homeTabPane;

	@FXML
	private Tab Logtab;

	private ReportController reportController;
	
	private DirectoryChooser envelopeReportDirectoryChooser;
	private boolean isDirectoryChooserOpen = false;
	
	private boolean isFileChooserOpen = false;
	

	// Properties to track file selections
	private BooleanProperty contentFileSelected = new SimpleBooleanProperty(false);
	private BooleanProperty csvFileSelected = new SimpleBooleanProperty(false);
	private BooleanProperty ReportFolderPathSelected = new SimpleBooleanProperty(false);

	// template - track file selections
	private BooleanProperty templateContentFileSelected = new SimpleBooleanProperty(false);
	private BooleanProperty templateXmlFileSelected = new SimpleBooleanProperty(false);
	private BooleanProperty templateReportFolderPathSelected = new SimpleBooleanProperty(false);

	// template - track file selections
	private BooleanProperty envelopeContentFileSelected = new SimpleBooleanProperty(false);
	private BooleanProperty envelopeXmlFileSelected = new SimpleBooleanProperty(false);
	private BooleanProperty envelopeReportFolderPathSelected = new SimpleBooleanProperty(false);

	private BooleanProperty ExportSelected = new SimpleBooleanProperty(false);

	SourceController(ReportController reportController) {
		this.reportController = reportController;
	}

	@FXML
	public void initialize() {
		String accountId = alfescoRestServiceUtility.getUserInfo(PropertiesHolder.getAccessToken());
		PropertiesHolder.setAccountId(accountId);
		String docusignversion = alfescoRestServiceUtility.getBuildVersion();
		PropertiesHolder.setDocusignversion(docusignversion);
		stackPaneForTextArea.getChildren().add(logTextArea); // one time adding logTextArea, logLoader to Stack Pane
		stackPaneForTextArea.getChildren().add(logLoader);
		logLoader.setVisible(false);
	}

	@FXML
	public void browseUserFile(ActionEvent event) {
		 if (isFileChooserOpen) {
		        return; 
		    }
		 try {
		        isFileChooserOpen = true;
		        
		        FileChooser fc = new FileChooser();
		        if (lastSelectedDirectory != null) {
		            fc.setInitialDirectory(lastSelectedDirectory);
		        }
		        fc.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
		        
		        // Use the Window from the event source to make it properly modal
		        Window stage = ((Node) event.getSource()).getScene().getWindow();
		        userFile = fc.showOpenDialog(stage);

		        if (userFile != null) {
		            lastSelectedDirectory = userFile.getParentFile();
		            labelUserLocation.setText(Constants.PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
		                    + userFile.getAbsolutePath() + Constants.SEPARATOR_DOUBLE_QUOTES);
		            labelUserLocation.setWrapText(true);
		            labelUserLocation.setVisible(true);
		            PropertiesHolder.setUserFilePath(userFile.getAbsolutePath());
		            importUserButton.setDisable(false);
		        }
		    } finally {
		        // 3. Reset the flag so it can be opened again later
		        isFileChooserOpen = false;
		    }
		}

		@FXML
		public void browseGroupFile(ActionEvent event) {
			if (isFileChooserOpen) {
				return;
			}
			try {
				isFileChooserOpen = true;
				FileChooser fc = new FileChooser();
				if (lastSelectedDirectory != null) {
					fc.setInitialDirectory(lastSelectedDirectory);
				}
				fc.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
				Window stage = ((Node) event.getSource()).getScene().getWindow();
				groupFile = fc.showOpenDialog(stage);
				if (groupFile != null) {
					lastSelectedDirectory = groupFile.getParentFile();
					labelGroupLocation.setText(Constants.PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
							+ groupFile.getAbsolutePath() + Constants.SEPARATOR_DOUBLE_QUOTES);
					labelGroupLocation.setWrapText(true);
					labelGroupLocation.setVisible(true);
					PropertiesHolder.setGroupFilePath(groupFile.getAbsolutePath());
					groupImportButton.setDisable(false);
				}
			} finally {
				// 3. Always reset the flag
				isFileChooserOpen = false;
			}
		}

	@FXML
	public void browseTemplateFile(ActionEvent event) {
		if (isFileChooserOpen) {
			return;
		}
		try {
			isFileChooserOpen = true;
		FileChooser fc = new FileChooser();
		if (lastSelectedDirectory != null) {
			fc.setInitialDirectory(lastSelectedDirectory);
		}
		fc.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
		Window stage = ((Node) event.getSource()).getScene().getWindow();
		templateFile = fc.showOpenDialog(stage);
		if (templateFile != null) {
			lastSelectedDirectory = templateFile.getParentFile();
			labelTemplateLocation.setText(Constants.PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
					+ templateFile.getAbsolutePath() + Constants.SEPARATOR_DOUBLE_QUOTES);
			labelTemplateLocation.setWrapText(true);
			labelTemplateLocation.setVisible(true);
			PropertiesHolder.setTemplateFilePath(templateFile.getAbsolutePath());
//			csvFileSelected.set(true); // Mark CSV file as selected
			templateXmlFileSelected.set(true);
			enableTemplateImportButton();
		}
		} finally {
			isFileChooserOpen = false;
		}
	}

	@FXML
	public void browseEnvelopeFile(ActionEvent event) {
		if (isFileChooserOpen) {
			return;
		}
		try {
			isFileChooserOpen = true;
			FileChooser fc = new FileChooser();
			if (lastSelectedDirectory != null) {
				fc.setInitialDirectory(lastSelectedDirectory);
			}
			fc.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
			Window stage = ((Node) event.getSource()).getScene().getWindow();
			envelopeFile = fc.showOpenDialog(stage);
			if (envelopeFile != null) {
				lastSelectedDirectory = envelopeFile.getParentFile();
				labelEnvelopeLocation.setText(Constants.PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
						+ envelopeFile.getAbsolutePath() + Constants.SEPARATOR_DOUBLE_QUOTES);
				labelEnvelopeLocation.setWrapText(true);
				labelEnvelopeLocation.setVisible(true);
				PropertiesHolder.setEnvelopeFilePath(envelopeFile.getAbsolutePath());
				envelopeXmlFileSelected.set(true);
				enableEnvelopeImportButton();
			}
		} finally {
			isFileChooserOpen = false;
		}
	}

	@FXML
	void browseTemplateContentFile(ActionEvent event) {
		if (isFileChooserOpen) {
			return;
		}
		try {
			isFileChooserOpen = true;
			DirectoryChooser directoryChooser = new DirectoryChooser();
			if (lastSelectedDirectory != null) {
				directoryChooser.setInitialDirectory(lastSelectedDirectory);
			}
			Window stage = ((Node) event.getSource()).getScene().getWindow();
			File selectedFolder = directoryChooser.showDialog(stage);
			if (selectedFolder != null) {
				lastSelectedDirectory = selectedFolder.getParentFile();
				labelTemplateContentLocation.setText(Constants.PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
						+ selectedFolder.getAbsolutePath() + Constants.SEPARATOR_DOUBLE_QUOTES);
				labelTemplateContentLocation.setWrapText(true);
				labelTemplateContentLocation.setVisible(true);
				PropertiesHolder.setTemplateContentFolderPath(selectedFolder.getAbsolutePath());
				templateContentFileSelected.set(true);
				enableTemplateImportButton();
			}
		} finally {
			isFileChooserOpen = false;
		}
	}

	@FXML
	void browseEnvelopContentFile(ActionEvent event) {
		if (isFileChooserOpen) {
			return;
		}
		try {
			isFileChooserOpen = true;
			DirectoryChooser directoryChooser = new DirectoryChooser();
			if (lastSelectedDirectory != null) {
				directoryChooser.setInitialDirectory(lastSelectedDirectory);
			}
			Window stage = ((Node) event.getSource()).getScene().getWindow();
			File selectedFolder = directoryChooser.showDialog(stage);
			if (selectedFolder != null) {
				lastSelectedDirectory = selectedFolder.getParentFile();
				labelEnvelopContentLocation.setText(Constants.PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
						+ selectedFolder.getAbsolutePath() + Constants.SEPARATOR_DOUBLE_QUOTES);
				labelEnvelopContentLocation.setWrapText(true);
				labelEnvelopContentLocation.setVisible(true);
				PropertiesHolder.setEnvelopContentFolderPath(selectedFolder.getAbsolutePath());
				envelopeContentFileSelected.set(true);
				enableEnvelopeImportButton();
			}
		} finally {
			isFileChooserOpen = false;
		}
	}

	@FXML
	public void browseCSVFile(ActionEvent event) {
		if (isFileChooserOpen) {
	        return;
	    }

	    try {
	        isFileChooserOpen = true;
		FileChooser fc = new FileChooser();
		if (lastSelectedDirectory != null) {
			fc.setInitialDirectory(lastSelectedDirectory);
		}
		fc.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
		Window stage = ((Node) event.getSource()).getScene().getWindow();
		csvFile = fc.showOpenDialog(stage);
		if (csvFile != null) {
			lastSelectedDirectory = csvFile.getParentFile();
			csvFilePathLabel.setText(Constants.PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES + csvFile.getAbsolutePath()
					+ Constants.SEPARATOR_DOUBLE_QUOTES);
			csvFilePathLabel.setWrapText(true);
			csvFilePathLabel.setVisible(true);
			PropertiesHolder.setCsvFilePath(csvFile.getAbsolutePath());
			csvFileSelected.set(true); // Mark CSV file as selected
			enableMainControllerButtons();
		}
	    } finally {
	        isFileChooserOpen = false;
	    }
	}

	@FXML
	public void browseProfileFile(ActionEvent event) {
		if (isFileChooserOpen) {
	        return;
	    }

	    try {
	        isFileChooserOpen = true;
		FileChooser fc = new FileChooser();
		if (lastSelectedDirectory != null) {
			fc.setInitialDirectory(lastSelectedDirectory);
		}
		fc.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
		Window stage = ((Node) event.getSource()).getScene().getWindow();
		csvFile = fc.showOpenDialog(stage);
		if (csvFile != null) {
			lastSelectedDirectory = csvFile.getParentFile();
			labelProfilesLocation.setText(Constants.PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
					+ csvFile.getAbsolutePath() + Constants.SEPARATOR_DOUBLE_QUOTES);
			labelProfilesLocation.setWrapText(true);
			labelProfilesLocation.setVisible(true);
			PropertiesHolder.setProfileFilePath(csvFile.getAbsolutePath());
			csvFileSelected.set(true); // Mark CSV file as selected
			enableMainControllerButtons();
		}
	    } finally {
	        isFileChooserOpen = false;
	    }
	}

	@FXML
	public void browseReportsPath(ActionEvent event) {
		if (isFileChooserOpen) {
	        return;
	    }

	    try {
	        isFileChooserOpen = true;
		DirectoryChooser reportsDirectoryChooser = new DirectoryChooser();
		if (lastSelectedDirectory != null) {
			reportsDirectoryChooser.setInitialDirectory(lastSelectedDirectory.getParentFile());
		}
		Window stage = ((Node) event.getSource()).getScene().getWindow();
		reportsFolderPath = reportsDirectoryChooser.showDialog(stage);
		if (reportsFolderPath != null) {
			lastSelectedDirectory = reportsFolderPath.getAbsoluteFile();
			ReportsFilePath.setText(Constants.PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
					+ reportsFolderPath.getAbsolutePath() + Constants.SEPARATOR_DOUBLE_QUOTES);
			ReportsFilePath.setWrapText(true);
			ReportsFilePath.setVisible(true);
			PropertiesHolder.setReportFolderPath(reportsFolderPath.getAbsolutePath());
			ReportFolderPathSelected.set(true);
			enableMainControllerButtons();
		}
	    } finally {
	        isFileChooserOpen = false;
	    }
	}

	@FXML
	public void browseTemplateReportPath(ActionEvent event) {
		if (isFileChooserOpen) {
	        return;
	    }

	    try {
	        isFileChooserOpen = true;
		DirectoryChooser reportsDirectoryChooser = new DirectoryChooser();
		if (lastSelectedDirectory != null) {
			reportsDirectoryChooser.setInitialDirectory(lastSelectedDirectory.getParentFile());
		}
		Window stage = ((Node) event.getSource()).getScene().getWindow();
		templateReportsFolderPath = reportsDirectoryChooser.showDialog(stage);
		if (templateReportsFolderPath != null) {
			lastSelectedDirectory = templateReportsFolderPath.getAbsoluteFile();
			labelTemplateReportLocation.setText(Constants.PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
					+ templateReportsFolderPath.getAbsolutePath() + Constants.SEPARATOR_DOUBLE_QUOTES);
			labelTemplateReportLocation.setWrapText(true);
			labelTemplateReportLocation.setVisible(true);
			PropertiesHolder.setTemplateReportFolderPath(templateReportsFolderPath.getAbsolutePath());
			templateReportFolderPathSelected.set(true);
			enableTemplateImportButton();
		}
	    } finally {
	        isFileChooserOpen = false;
	    }
	}

	@FXML
	public void browseEnvelopeReportPath(ActionEvent event) {
		if (isFileChooserOpen) {
	        return;
	    }

	    try {
	        isFileChooserOpen = true;
		DirectoryChooser reportsDirectoryChooser = new DirectoryChooser();
		if (lastSelectedDirectory != null) {
			reportsDirectoryChooser.setInitialDirectory(lastSelectedDirectory.getParentFile());
		}
		Window stage = ((Node) event.getSource()).getScene().getWindow();
		envelopeReportsFolderPath = reportsDirectoryChooser.showDialog(stage);
		if (envelopeReportsFolderPath != null) {
			lastSelectedDirectory = envelopeReportsFolderPath.getAbsoluteFile();
			labelEnvelopeReportLocation.setText(Constants.PATH_TXT + Constants.SEPARATOR_DOUBLE_QUOTES
					+ envelopeReportsFolderPath.getAbsolutePath() + Constants.SEPARATOR_DOUBLE_QUOTES);
			labelEnvelopeReportLocation.setWrapText(true);
			labelEnvelopeReportLocation.setVisible(true);
			PropertiesHolder.setEnvelopeReportFolderPath(envelopeReportsFolderPath.getAbsolutePath());
			envelopeReportFolderPathSelected.set(true);
			enableEnvelopeImportButton();
		}
	    } finally {
	        isFileChooserOpen = false;
	    }
	}

	@FXML
	public void getDataCSV(Event e) {
		if (csvFile != null) {
			try {
				CSVTableView tableView = new CSVTableView(",", csvFile);
				csvTableView.getColumns().clear();
				csvTableView.getColumns().addAll(tableView.getColumns());
				csvTableView.setItems(tableView.getItems());
				int recordCount = tableView.getItems().size();
				logger.info("Total Records Returned : " + recordCount);
				totalRecordsCount.setText(Constants.TOTAL_RECORDS_TXT + recordCount);
				PropertiesHolder.setCsvRecordsCount(recordCount);
				totalRecordsCount.setFont(Font.font(Constants.POPPINS_FONT, FontWeight.BOLD, 12));
				csvTableView.setVisible(true);
				totalRecordsCount.setVisible(true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@FXML
	public void readXMLData(Event e) {
		if (csvFile != null) {
			XMLTableView tableView = new XMLTableView();
			TableView<RecordProperties> recordView = tableView.readXMLData(csvFile, "");
			recordTableView.getColumns().clear();
			recordTableView.getColumns().addAll(recordView.getColumns());
			recordTableView.setItems(recordView.getItems());
			int recordCount = recordView.getItems().size();
			logger.info("Total Records Returned : " + recordCount);
			totalRecordsCount.setText(Constants.TOTAL_RECORDS_TXT + recordCount);
			PropertiesHolder.setCsvRecordsCount(recordCount);
			totalRecordsCount.setFont(Font.font(Constants.POPPINS_FONT, FontWeight.BOLD, 12));
			recordTableView.setVisible(true);
			totalRecordsCount.setVisible(true);
		}
	}

	@FXML
	public void readUserXMLData(Event e) {
		if (userFile != null) {
			XMLTableView tableView = new XMLTableView();
			TableView<RecordProperties> recordView = tableView.readXMLData(userFile, "user");
			userRecordTableView.getColumns().clear();
			userRecordTableView.getColumns().addAll(recordView.getColumns());
			userRecordTableView.setItems(recordView.getItems());
			int recordCount = recordView.getItems().size();
			logger.info("Total Records Returned : " + recordCount);
			totalUserRecordsCount.setText(Constants.TOTAL_RECORDS_TXT + recordCount);
			PropertiesHolder.setUserRecordsCount(recordCount);
			totalUserRecordsCount.setFont(Font.font(Constants.POPPINS_FONT, FontWeight.BOLD, 12));
			userRecordTableView.setVisible(true);
			totalUserRecordsCount.setVisible(true);
		}
	}

	@FXML
	public void readTemplateXMLData(Event e) {
		if (templateFile != null) {
			XMLTableView tableView = new XMLTableView();
			TableView<RecordProperties> recordView = tableView.readXMLData(templateFile, "template");
			templateRecordTableView.getColumns().clear();
			templateRecordTableView.getColumns().addAll(recordView.getColumns());
			templateRecordTableView.setItems(recordView.getItems());
			int recordCount = recordView.getItems().size();
			logger.info("Total Records Returned : " + recordCount);
			totalTemplateRecordsCount.setText(Constants.TOTAL_RECORDS_TXT + recordCount);
			PropertiesHolder.setTemplateRecordsCount(recordCount);
			totalTemplateRecordsCount.setFont(Font.font(Constants.POPPINS_FONT, FontWeight.BOLD, 12));
			templateRecordTableView.setVisible(true);
			totalTemplateRecordsCount.setVisible(true);
		}
	}

	@FXML
	public void readEnvelopeXMLData(Event e) {
		if (envelopeFile != null) {
			XMLTableView tableView = new XMLTableView();
			TableView<RecordProperties> recordView = tableView.readXMLData(envelopeFile, "envelop");
			envelopeRecordTableView.getColumns().clear();
			envelopeRecordTableView.getColumns().addAll(recordView.getColumns());
			envelopeRecordTableView.setItems(recordView.getItems());
			int recordCount = recordView.getItems().size();
			logger.info("Total Records Returned : " + recordCount);
			totalEnvelopeRecordsCount.setText(Constants.TOTAL_RECORDS_TXT + recordCount);
			PropertiesHolder.setEnvelopRecordsCount(recordCount);
			totalEnvelopeRecordsCount.setFont(Font.font(Constants.POPPINS_FONT, FontWeight.BOLD, 12));
			envelopeRecordTableView.setVisible(true);
			totalEnvelopeRecordsCount.setVisible(true);
		}
	}

	@FXML
	public void readGroupXMLData(Event e) {
		if (groupFile != null) {
			XMLTableView tableView = new XMLTableView();
			TableView<RecordProperties> recordView = tableView.readXMLData(groupFile, "group");
			groupRecordTableView.getColumns().clear();
			groupRecordTableView.getColumns().addAll(recordView.getColumns());
			groupRecordTableView.setItems(recordView.getItems());
			int recordCount = recordView.getItems().size();
			logger.info("Total Records Returned : " + recordCount);
			totalGroupRecordsCount.setText(Constants.TOTAL_RECORDS_TXT + recordCount);
			PropertiesHolder.setGroupRecordsCount(recordCount);
			totalGroupRecordsCount.setFont(Font.font(Constants.POPPINS_FONT, FontWeight.BOLD, 12));
			groupRecordTableView.setVisible(true);
			totalGroupRecordsCount.setVisible(true);
		}
	}

	private void enableMainControllerButtons() {
		if (contentFileSelected.get() && csvFileSelected.get()) {
			MainController mainController = applicationContext.getBean(MainController.class);
//			mainController.enableTargetButton(true);
		}
	}

	@FXML
	public void validateXMLData(Event e) {
		if (csvFile != null) {
			xmlValid = CommonUtility.validateXMLSchema("DocOrbit-metadata.xsd", PropertiesHolder.getCsvFilePath());
			if (xmlValid) {
				logger.info("XML document is valid !!!!");
				ModernAlertUtil.infoBox(Constants.XML_VALIDATION_SUCCESS_MESSAGE, Constants.XML_VALIDATION_SUCCESS_HEADER,
						Constants.XML_VALIDATION_SUCCESS_TITLE);
			} else {
				logger.info("Not Valid document");
				ModernAlertUtil.showErrorAlert(Constants.XML_XSD_VALIDATION_TITLE, Constants.XML_XSD_VALIDATION_MESSAGE);
			}
		}
	}

	@FXML
	public void validateUserXMLData(Event e) {
		if (userFile != null) {
			System.out.println(PropertiesHolder.getUserFilePath());
			xmlValid = CommonUtility.validateXMLSchema("DocOrbit-user-metadata.xsd",
					PropertiesHolder.getUserFilePath());
			if (xmlValid) {
				logger.info("XML document is valid !!!!");
				ModernAlertUtil.infoBox(Constants.XML_VALIDATION_SUCCESS_MESSAGE, Constants.XML_VALIDATION_SUCCESS_HEADER,
						Constants.XML_VALIDATION_SUCCESS_TITLE);
			} else {
				logger.info("Not Valid document");
				ModernAlertUtil.showErrorAlert(Constants.XML_XSD_VALIDATION_TITLE, Constants.XML_XSD_VALIDATION_MESSAGE);
			}
		}
	}

	@FXML
	public void validateGroupXMLData(Event e) {
		if (groupFile != null) {
			xmlValid = CommonUtility.validateXMLSchema("DocOrbit-group-metadata.xsd",
					PropertiesHolder.getGroupFilePath());
			if (xmlValid) {
				logger.info("XML document is valid !!!!");
				ModernAlertUtil.infoBox(Constants.XML_VALIDATION_SUCCESS_MESSAGE, Constants.XML_VALIDATION_SUCCESS_HEADER,
						Constants.XML_VALIDATION_SUCCESS_TITLE);
			} else {
				logger.info("Not Valid document");
				ModernAlertUtil.showErrorAlert(Constants.XML_XSD_VALIDATION_TITLE, Constants.XML_XSD_VALIDATION_MESSAGE);
			}
		}
	}

	@FXML
	public void validateProfileXMLData(Event e) {
		if (profileFile != null) {
			xmlValid = CommonUtility.validateXMLSchema("DocOrbit-profiles-metadata.xsd",
					PropertiesHolder.getProfileFilePath());
			if (xmlValid) {
				logger.info("XML document is valid !!!!");
				ModernAlertUtil.infoBox(Constants.XML_VALIDATION_SUCCESS_MESSAGE, Constants.XML_VALIDATION_SUCCESS_HEADER,
						Constants.XML_VALIDATION_SUCCESS_TITLE);
			} else {
				logger.info("Not Valid document");
				ModernAlertUtil.showErrorAlert(Constants.XML_XSD_VALIDATION_TITLE, Constants.XML_XSD_VALIDATION_MESSAGE);
			}
		}
	}

	@FXML
	public void validateTemplateXMLData(Event e) {
		if (templateFile != null) {
			xmlValid = CommonUtility.validateXMLSchema("DocOrbit-template-metadata.xsd",
					PropertiesHolder.getTemplateFilePath());
			if (xmlValid) {
				logger.info("XML document is valid !!!!");
				ModernAlertUtil.infoBox(Constants.XML_VALIDATION_SUCCESS_MESSAGE, Constants.XML_VALIDATION_SUCCESS_HEADER,
						Constants.XML_VALIDATION_SUCCESS_TITLE);
			} else {
				logger.info("Not Valid document");
				ModernAlertUtil.showErrorAlert(Constants.XML_XSD_VALIDATION_TITLE, Constants.XML_XSD_VALIDATION_MESSAGE);
			}
		}
	}

	@FXML
	public void validateEnvelopeXMLData(Event e) {
		if (envelopeFile != null) {
			xmlValid = CommonUtility.validateXMLSchema("DocOrbit-envelope-metadata.xsd",
					PropertiesHolder.getEnvelopeFilePath());
			if (xmlValid) {
				logger.info("XML document is valid !!!!");
				ModernAlertUtil.infoBox(Constants.XML_VALIDATION_SUCCESS_MESSAGE, Constants.XML_VALIDATION_SUCCESS_HEADER,
						Constants.XML_VALIDATION_SUCCESS_TITLE);
			} else {
				logger.info("Not Valid document");
				ModernAlertUtil.showErrorAlert(Constants.XML_XSD_VALIDATION_TITLE, Constants.XML_XSD_VALIDATION_MESSAGE);
			}
		}
	}

	@FXML
	public void importButtonAction(ActionEvent event) {
		logLoader.setVisible(true);
		PropertiesHolder.setLogTextArea(logTextArea);
		PropertiesHolder.setLogProcessLoader(logLoader);
		importTemplateButton.getStyleClass().add("selected");
		logger.info("Clicked on Template Import Button ---------------------------");
		logger.info("CSV File Path : --- " + PropertiesHolder.getTemplateFilePath());
		logger.info("Content Folder Path : --- " + PropertiesHolder.getTemplateContentFolderPath());
		logger.info("Target Path : --- " + PropertiesHolder.getNodePath());
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
//		btnDownloadEnvelopsReport.setDisable(true);
		setupTabPane.getSelectionModel().select(Logtab);
		CommonUtility.logsDisplay("============ Importing Templates ==================",
				PropertiesHolder.getLogTextArea());
		CommonUtility.logsDisplay("Importing Template Documents to the Target Account",
				PropertiesHolder.getLogTextArea());
//		 Create a background task
		Task<Void> task = new Task<Void>() {

			long executionCount = 0;
			long csvRecordCount = 0;

			@Override
			protected Void call() throws Exception {
				try {
					File fileToImport = new File(PropertiesHolder.getTemplateFilePath());
//					String xmlPath = PropertiesHolder.getTemplateContentFolderPath() + Constants.SEPARATOR_FORWARD_SLASH
//							+ Constants.XML_TRANS_FILE_NAME;
//					File transformationXml = new File(xmlPath);
					System.out.println("fileToImport--->" + fileToImport);
					// Launch the Batch Job
					JobParametersBuilder builder = new JobParametersBuilder();
					builder.addDate("date", new Date());
					builder.addString("fullPathFileName", fileToImport.getAbsolutePath()).toJobParameters();
//					builder.addString("transformationXml", transformationXml.getAbsolutePath()).toJobParameters();
					JobExecution jobExecution = jobLauncher.run(templateMultithreadedJob, builder.toJobParameters());
					logger.info("Execution Status : " + jobExecution.getStatus());
					csvRecordCount = PropertiesHolder.getTemplateRecordsCount();
					executionCount = PropertiesHolder.getTemplateRecordsExecutionCount();
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
				importTemplateButton.setDisable(false);
				CommonUtility.logsDisplay("Total Template Uploaded -  " + executionCount,
						PropertiesHolder.getLogTextArea());
				if (csvRecordCount == executionCount) {
					ModernAlertUtil.infoBox(Constants.IMPORT_SUCCESS_MESSAGE + executionCount, Constants.IMPORT_SUCCESS_HEADER,
							Constants.IMPORT_SUCCESS_TITLE);
				} else {
					ModernAlertUtil.showErrorAlert(Constants.IMPORT_FAILURE_TITLE, Constants.IMPORT_FAILURE_MESSAGE);
				}

				Platform.runLater(() -> {
					if (reportController.isTemplateInitialized()) {
						reportController.refreshTemplateTableView();
					} else {
						logger.info("ReportController not initialized yet, skipping refresh.");
					}
				});
				ExportSelected.set(true);
				notifyMainController();

			}

			@Override
			protected void failed() {
				super.failed();
				importTemplateButton.setDisable(false);
			}
		};
		new Thread(task).start();
	}

	@FXML
	public void importUserButtonAction(ActionEvent event) {
		// Create a background task
		Task<Void> task = new Task<Void>() {

			long executionCount = 0;
			long userRecordCount = 0;

			@Override
			protected Void call() throws Exception {
				try {
					File fileToImport = new File(PropertiesHolder.getUserFilePath());
					String xmlPath = PropertiesHolder.getTemplateContentFolderPath() + Constants.SEPARATOR_FORWARD_SLASH
							+ Constants.XML_TRANS_FILE_NAME;
					File transformationXml = new File(xmlPath);
					// Launch the Batch Job
					JobParametersBuilder builder = new JobParametersBuilder();
					builder.addDate("date", new Date());
					builder.addString("fullPathFileName", fileToImport.getAbsolutePath()).toJobParameters();
					builder.addString("transformationXml", transformationXml.getAbsolutePath()).toJobParameters();
					JobExecution jobExecution = jobLauncher.run(userMultithreadedJob, builder.toJobParameters());
					logger.info("Execution Status : " + jobExecution.getStatus());
					userRecordCount = PropertiesHolder.getUserRecordsCount();
					executionCount = PropertiesHolder.getUserRecordsExecutionCount();
					logger.info("########## Execution Count :" + executionCount + " ###########");
					logger.info("########## XML Record Count :" + userRecordCount + " ###########");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void succeeded() {
				super.succeeded();

				if (userRecordCount == executionCount) {
					ModernAlertUtil.infoBox(Constants.USER_IMPORT_SUCCESS_MESSAGE + executionCount,
							Constants.IMPORT_SUCCESS_HEADER, Constants.IMPORT_SUCCESS_TITLE);
				} else {
					ModernAlertUtil.showErrorAlert(Constants.IMPORT_FAILURE_TITLE, Constants.IMPORT_FAILURE_MESSAGE);
				}

			}

			@Override
			protected void failed() {
				super.failed();
				importButton.setDisable(false);
			}
		};
		new Thread(task).start();
	}

	@FXML
	public void importEnvelopButtonAction(ActionEvent event) {
		logLoader.setVisible(true);
		PropertiesHolder.setLogTextArea(logTextArea);
		PropertiesHolder.setLogProcessLoader(logLoader);
		importEnvelopeButton.getStyleClass().add("selected");
		logger.info("Clicked on Envelope Import Button ---------------------------");
		logger.info("CSV File Path : --- " + PropertiesHolder.getEnvelopeFilePath());
		logger.info("Content Folder Path : --- " + PropertiesHolder.getEnvelopContentFolderPath());
		logger.info("Target Path : --- " + PropertiesHolder.getNodePath());
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
//		btnDownloadEnvelopsReport.setDisable(true);
		setupTabPane.getSelectionModel().select(Logtab);
		CommonUtility.logsDisplay("============ Importing Envelopes ==================",
				PropertiesHolder.getLogTextArea());
		CommonUtility.logsDisplay("Importing Envelope Documents to the Target Account",
				PropertiesHolder.getLogTextArea());
		// Create a background task
		Task<Void> task = new Task<Void>() {

			long executionCount = 0;
			long envelopRecordCount = 0;

			@Override
			protected Void call() throws Exception {
				try {
					File fileToImport = new File(PropertiesHolder.getEnvelopeFilePath());
//					String xmlPath = PropertiesHolder.getEnvelopContentFolderPath() + Constants.SEPARATOR_FORWARD_SLASH
//							+ Constants.XML_TRANS_FILE_NAME;
//					File transformationXml = new File(xmlPath);
					// Launch the Batch Job
					JobParametersBuilder builder = new JobParametersBuilder();
					builder.addDate("date", new Date());
					builder.addString("fullPathFileName", fileToImport.getAbsolutePath()).toJobParameters();
//					builder.addString("transformationXml", transformationXml.getAbsolutePath()).toJobParameters();
					JobExecution jobExecution = jobLauncher.run(envelopMultithreadedJob, builder.toJobParameters());
					logger.info("Execution Status : " + jobExecution.getStatus());
					envelopRecordCount = PropertiesHolder.getEnvelopRecordsCount();
					executionCount = PropertiesHolder.getEnvelopRecordsExecutionCount();
					logger.info("########## Execution Count :" + executionCount + " ###########");
					logger.info("########## XML Record Count :" + envelopRecordCount + " ###########");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void succeeded() {
				super.succeeded();
//				importButton.setDisable(false);
				CommonUtility.logsDisplay("Total Envelope Uploaded -  " + executionCount,
						PropertiesHolder.getLogTextArea());
				if (envelopRecordCount == executionCount) {
					ModernAlertUtil.infoBox(Constants.USER_IMPORT_SUCCESS_MESSAGE + executionCount,
							Constants.IMPORT_SUCCESS_HEADER, Constants.IMPORT_SUCCESS_TITLE);
				} else {
					ModernAlertUtil.showErrorAlert(Constants.IMPORT_FAILURE_TITLE, Constants.IMPORT_FAILURE_MESSAGE);
				}

				Platform.runLater(() -> {
					if (reportController.isEnvelopeInitialized()) {
						reportController.refreshEnvelopeTableView();
					} else {
						logger.info("ReportController not initialized yet, skipping refresh.");
					}
				});
				ExportSelected.set(true);
				notifyMainController();
			}

			@Override
			protected void failed() {
				super.failed();
				importEnvelopeButton.setDisable(false);
			}
		};
		new Thread(task).start();
	}

	@FXML
	public void importGroupButtonAction(ActionEvent event) {
		Task<Void> task = new Task<Void>() {

			long executionCount = 0;
			long groupRecordCount = 0;

			@Override
			protected Void call() throws Exception {
				try {
					File fileToImport = new File(PropertiesHolder.getGroupFilePath());
					String xmlPath = PropertiesHolder.getTemplateContentFolderPath() + Constants.SEPARATOR_FORWARD_SLASH
							+ Constants.XML_TRANS_FILE_NAME;
					File transformationXml = new File(xmlPath);
					// Launch the Batch Job
					JobParametersBuilder builder = new JobParametersBuilder();
					builder.addDate("date", new Date());
					builder.addString("fullPathFileName", fileToImport.getAbsolutePath()).toJobParameters();
					builder.addString("transformationXml", transformationXml.getAbsolutePath()).toJobParameters();
					JobExecution jobExecution = jobLauncher.run(groupMultithreadedJob, builder.toJobParameters());
					logger.info("Execution Status : " + jobExecution.getStatus());
					groupRecordCount = PropertiesHolder.getGroupRecordsCount();
					executionCount = PropertiesHolder.getGroupRecordsExecutionCount(); // because by default
																						// administration and
																						// everyone groups are
																						// available
					logger.info("########## Execution Count :" + executionCount + " ###########");
					logger.info("########## XML Record Count :" + groupRecordCount + " ###########");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void succeeded() {
				super.succeeded();
//				importButton.setDisable(false);
//				CommonUtility.logsDisplay("Total Documents Uploaded -  " + executionCount,
//						PropertiesHolder.getLogTextArea());
				if (groupRecordCount == executionCount) {
					ModernAlertUtil.infoBox(Constants.GROUP_IMPORT_SUCCESS_MESSAGE + executionCount,
							Constants.IMPORT_SUCCESS_HEADER, Constants.IMPORT_SUCCESS_TITLE);
				} else {
					ModernAlertUtil.showErrorAlert(Constants.IMPORT_FAILURE_TITLE, Constants.GROUP_IMPORT_FAILURE_MESSAGE);
				}
//				ExportSelected.set(true);
//				notifyMainController();
			}

			@Override
			protected void failed() {
				super.failed();
				importButton.setDisable(false);
			}
		};
		new Thread(task).start();
	}

	private void enableTemplateImportButton() {
		if (templateContentFileSelected.get() && templateXmlFileSelected.get()
				&& templateReportFolderPathSelected.get()) {
			importTemplateButton.setDisable(false);
		}
	}

	private void enableEnvelopeImportButton() {
		if (envelopeContentFileSelected.get() && envelopeXmlFileSelected.get()
				&& envelopeReportFolderPathSelected.get()) {
			importEnvelopeButton.setDisable(false);
		}
	}

	public void notifyMainController() {
		if (ExportSelected.get()) {
			MainController mainController = applicationContext.getBean(MainController.class);
			mainController.enableReportButton(true);
		}
	}
}
