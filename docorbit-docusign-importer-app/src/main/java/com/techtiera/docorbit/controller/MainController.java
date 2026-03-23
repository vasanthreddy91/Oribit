package com.techtiera.docorbit.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import com.techtiera.docorbit.JavaFxApplicationSupport;
import com.techtiera.docorbit.alfresco.constants.Constants;
import com.techtiera.docorbit.alfresco.services.AlfescoRestServiceUtility;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.config.UserSession;
import com.techtiera.docorbit.jfxsupport.FXMLController;
import com.techtiera.docorbit.jfxsupport.ModernAlertUtil;
import com.techtiera.docorbit.resource.VersionDetails;
import com.techtiera.docorbit.util.AppUtil;
import com.techtiera.docorbit.views.LoginView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

@FXMLController
public class MainController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@FXML
	private VBox mainVBox;

	@FXML
	private Label contetFilePathLaebl;

	@FXML
	private Button btnContentFile;

	@FXML
	private Label CSVFilePathLaebl;

	@FXML
	private Label totalRecordsCount;

	@FXML
	private Button btnCSVFile;

	@FXML
	private TableView<String> csvTableView;

	CheckBoxTreeItem<String> treeView;

	@FXML
	private AnchorPane setupAnchorPane;

	@FXML
	private Button sourceButton;

	@FXML
	private Button targetButton;

	@FXML
	private Button importButton;

	@FXML
	private Button reportsButton;

	private TabPane sourcetabPane;

	@FXML
	private MenuButton closeButton;

	@FXML
	private ImageView sourceImage;

	@FXML
	private ImageView targetImage;

	@FXML
	private ImageView importImage;

	@FXML
	private ImageView reportsImage;

	@FXML
	private Button transformationButton;

	@FXML
	private ImageView transformationImage;

	@FXML
	private Label loginUserName;

	private TabPane targettabPane;

	private TabPane transformation;

	private TabPane importtabPane;

	@FXML
	private AnchorPane setupParentAnchorPane;

	@FXML
	private Label versionId;

	@Autowired
	private AlfescoRestServiceUtility alfescoRestServiceUtility;

	@FXML
	private TabPane reporttabPane;

	@FXML
	public void initialize() {

		PropertiesHolder.setNewTansformationTabStatus(true);

		String loginuser = PropertiesHolder.getLoginUser();
		logger.info(loginuser);
		loginUserName.setText(loginuser);

		sourceButton.setCursor(Cursor.HAND);
//		targetButton.setCursor(Cursor.HAND);
//		importButton.setCursor(Cursor.HAND);
		reportsButton.setCursor(Cursor.HAND);
		closeButton.setCursor(Cursor.HAND);
//		transformationButton.setCursor(Cursor.HAND);

		try {
			sourceImage.setImage(new Image("/images/Source-blue.png"));
			sourceButton.getStyleClass().add("selected");
			FXMLLoader sourceFxmlLoader = new FXMLLoader(getClass().getResource("/fxml/source-modern.fxml"));
			sourceFxmlLoader.setControllerFactory(applicationContext::getBean);
			sourcetabPane = sourceFxmlLoader.load();
			setupAnchorPane.getChildren().clear();
			setupAnchorPane.getChildren().add(sourcetabPane);
			AnchorPane.setTopAnchor(sourcetabPane, 0.0);
			AnchorPane.setBottomAnchor(sourcetabPane, 0.0);
			AnchorPane.setLeftAnchor(sourcetabPane, 0.0);
			AnchorPane.setRightAnchor(sourcetabPane, 0.0);
			UserSession.setIsUserLogout(false);

//			VersionDetails versionDetails = alfescoRestServiceUtility.getVersionDetails();
//			if(versionDetails != null) {
//				versionId.setText(versionDetails.getVersion());
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onWindowShownEvent() {
		if (treeView != null) {

		}
		;
	}

	@FXML
	public void sourceButtonAction(ActionEvent event) {
		logger.info("Clicked on Source Option");
		removeSelectedStyle();
		sourceImage.setImage(new Image("/images/Source-blue.png"));
		sourceButton.getStyleClass().add("selected");
		if (sourcetabPane == null) {
			try {
				FXMLLoader sourceFxmlLoader = new FXMLLoader(getClass().getResource("/fxml/source-modern.fxml"));
				sourceFxmlLoader.setControllerFactory(applicationContext::getBean);
				sourcetabPane = sourceFxmlLoader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setupAnchorPane.getChildren().clear();
		setupAnchorPane.getChildren().add(sourcetabPane);
		AnchorPane.setTopAnchor(sourcetabPane, 0.0);
		AnchorPane.setBottomAnchor(sourcetabPane, 0.0);
		AnchorPane.setLeftAnchor(sourcetabPane, 0.0);
		AnchorPane.setRightAnchor(sourcetabPane, 0.0);
	}

	@FXML
	public void targetButtonAction(ActionEvent event) {
		logger.info("Clicked on Target Option");
		removeSelectedStyle();
		targetImage.setImage(new Image("/images/target-blue.png"));
		targetButton.getStyleClass().add("selected");
		if (targettabPane == null) {
			try {
				FXMLLoader targetFxmlLoader = new FXMLLoader(getClass().getResource("/fxml/target.fxml"));
				targetFxmlLoader.setControllerFactory(applicationContext::getBean);
				targettabPane = targetFxmlLoader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setupAnchorPane.getChildren().clear();
		setupAnchorPane.getChildren().add(targettabPane);
		AnchorPane.setTopAnchor(targettabPane, 0.0);
		AnchorPane.setBottomAnchor(targettabPane, 0.0);
		AnchorPane.setLeftAnchor(targettabPane, 0.0);
		AnchorPane.setRightAnchor(targettabPane, 0.0);
	}

	@FXML
	public void importButtonAction(ActionEvent event) {
		logger.info("Clicked on Import Option");
		removeSelectedStyle();
		importImage.setImage(new Image("/images/import-blue.png"));
		importButton.getStyleClass().add("selected");
		try {
			FXMLLoader importFxmlLoader = new FXMLLoader(getClass().getResource("/fxml/import.fxml"));
			importFxmlLoader.setControllerFactory(applicationContext::getBean);
			importtabPane = importFxmlLoader.load();
			setupAnchorPane.getChildren().clear();
			setupAnchorPane.getChildren().add(importtabPane);
			AnchorPane.setTopAnchor(importtabPane, 0.0);
			AnchorPane.setBottomAnchor(importtabPane, 0.0);
			AnchorPane.setLeftAnchor(importtabPane, 0.0);
			AnchorPane.setRightAnchor(importtabPane, 0.0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void removeSelectedStyle() {
		sourceButton.getStyleClass().remove("selected");
//		targetButton.getStyleClass().remove("selected");
//		importButton.getStyleClass().remove("selected");
		reportsButton.getStyleClass().remove("selected");
//		transformationButton.getStyleClass().remove("selected");

		sourceImage.setImage(new Image("/images/Source-white.png"));
//		targetImage.setImage(new Image("/images/target-white.png"));
//		importImage.setImage(new Image("/images/import-white.png"));
		reportsImage.setImage(new Image("/images/reports-white.png"));
//		transformationImage.setImage(new Image("/images/transformation-white.png"));
	}

	@FXML
	public void closeButtonAction(ActionEvent event) {
		String loginuser = PropertiesHolder.getLoginUser();
		logger.info(loginuser);
		loginUserName.setText(loginuser);
		ButtonType result = ModernAlertUtil.infoResponseBox(Constants.LOGOUT_MESSAGE, Constants.LOGOUT_HEADER,
				Constants.LOGOUT_TITLE);
		if (result == ButtonType.OK) {
			UserSession.cleanUserSession();
			closeButton.getScene().getWindow().hide();
			UserSession.setIsUserLogout(true);
			JavaFxApplicationSupport.showView(LoginView.class);
		}
	}

	@FXML
	public void reportsButtonAction(ActionEvent event) {
		logger.info("Clicked on Reports Option");
		removeSelectedStyle();
		reportsImage.setImage(new Image("/images/reports-blue.png"));
		reportsButton.getStyleClass().add("selected");
		if (reporttabPane == null) {
			try {
				FXMLLoader reportFxmlLoader = new FXMLLoader(getClass().getResource("/fxml/reports-modern.fxml"));
				reportFxmlLoader.setControllerFactory(applicationContext::getBean);
				reporttabPane = reportFxmlLoader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setupAnchorPane.getChildren().clear();
		setupAnchorPane.getChildren().add(reporttabPane);
		AnchorPane.setTopAnchor(reporttabPane, 0.0);
		AnchorPane.setBottomAnchor(reporttabPane, 0.0);
		AnchorPane.setLeftAnchor(reporttabPane, 0.0);
		AnchorPane.setRightAnchor(reporttabPane, 0.0);
	}

	@FXML
	public void transformationButtonAction(ActionEvent event) {
		logger.info("Clicked on Transformation Option");
//		if (PropertiesHolder.getCsvFilePath() == null) {
//			AppUtil.infoBox(Constants.TRANS_CSV_SELECTION_MESSAGE, Constants.TRANS_CSV_SELECTION_HEADER,
//					Constants.TRANS_CSV_SELECTION_TITLE);
//		} else {
		removeSelectedStyle();
		transformationImage.setImage(new Image("/images/transformation-blue.png"));
		transformationButton.getStyleClass().add("selected");
		if (transformation == null || !PropertiesHolder.isNewTansformationTabStatus()) {
			try {
				FXMLLoader transformationButtonFxmlLoader = new FXMLLoader(
						getClass().getResource("/fxml/transformation.fxml"));
				transformationButtonFxmlLoader.setControllerFactory(applicationContext::getBean);
				transformation = transformationButtonFxmlLoader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setupAnchorPane.getChildren().clear();
		setupAnchorPane.getChildren().add(transformation);
		AnchorPane.setTopAnchor(transformation, 0.0);
		AnchorPane.setBottomAnchor(transformation, 0.0);
		AnchorPane.setLeftAnchor(transformation, 0.0);
		AnchorPane.setRightAnchor(transformation, 0.0);
//		}
	}

//	public void enableTargetButton(boolean enable) {
//		targetButton.setDisable(!enable);
//	}
//
//	public void enableTransformationButton(boolean enable) {
//		transformationButton.setDisable(!enable);
//	}
//
//	public void enableExportButton(boolean enable) {
//		importButton.setDisable(!enable);
//	}
//	
//	public void enableReportsButton(boolean enable) {
//		reportsButton.setDisable(!enable);
//	}

	public void reset() {
		loginUserName.setText(PropertiesHolder.getLoginUser());
		removeSelectedStyle();

		try {
			sourceImage.setImage(new Image("/images/Source-blue.png"));
			sourceButton.getStyleClass().add("selected");
			FXMLLoader sourceFxmlLoader = new FXMLLoader(getClass().getResource("/fxml/source-modern.fxml"));
			sourceFxmlLoader.setControllerFactory(applicationContext::getBean);
			sourcetabPane = sourceFxmlLoader.load();
			setupAnchorPane.getChildren().clear();
			setupAnchorPane.getChildren().add(sourcetabPane);
			AnchorPane.setTopAnchor(sourcetabPane, 0.0);
			AnchorPane.setBottomAnchor(sourcetabPane, 0.0);
			AnchorPane.setLeftAnchor(sourcetabPane, 0.0);
			AnchorPane.setRightAnchor(sourcetabPane, 0.0);
			UserSession.setIsUserLogout(false);

			FXMLLoader targetFxmlLoader = new FXMLLoader(getClass().getResource("/fxml/target.fxml"));
			targetFxmlLoader.setControllerFactory(applicationContext::getBean);
			targettabPane = targetFxmlLoader.load();

			FXMLLoader importFxmlLoader = new FXMLLoader(getClass().getResource("/fxml/import.fxml"));
			importFxmlLoader.setControllerFactory(applicationContext::getBean);
			importtabPane = importFxmlLoader.load();

			FXMLLoader reportFxmlLoader = new FXMLLoader(getClass().getResource("/fxml/reports-modern.fxml"));
			reportFxmlLoader.setControllerFactory(applicationContext::getBean);
			reporttabPane = reportFxmlLoader.load();

//			FXMLLoader transformationButtonFxmlLoader = new FXMLLoader(
//					getClass().getResource("/fxml/transformation.fxml"));
//			transformationButtonFxmlLoader.setControllerFactory(applicationContext::getBean);
//			transformation = transformationButtonFxmlLoader.load();

			PropertiesHolder.setNewTansformationTabStatus(false);

//			targetButton.setDisable(true);
//			transformationButton.setDisable(true);
//			importButton.setDisable(true);
			reportsButton.setDisable(true);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void enableReportButton(boolean enable) {
		reportsButton.setDisable(!enable);
	}
}
