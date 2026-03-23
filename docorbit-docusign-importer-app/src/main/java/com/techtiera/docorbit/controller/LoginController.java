package com.techtiera.docorbit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.techtiera.docorbit.JavaFxApplicationSupport;
import com.techtiera.docorbit.alfresco.constants.Constants;
import com.techtiera.docorbit.alfresco.services.AlfescoRestServiceUtility;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.config.UserSession;
import com.techtiera.docorbit.jfxsupport.FXMLController;
import com.techtiera.docorbit.jfxsupport.ModernAlertUtil;
import com.techtiera.docorbit.util.AppUtil;
import com.techtiera.docorbit.views.MainView;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaView;

@FXMLController
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@FXML
	private TextField userName;

	@FXML
	private PasswordField password;

	@FXML
	protected Button loginButton;

	@Autowired
	private AlfescoRestServiceUtility alfescoRestServiceUtility;

	@FXML
	protected Button cancelButton;

	@FXML
	protected ImageView bgImage;

	@FXML
	protected AnchorPane loginAnchorPane;

	@FXML
	private ImageView techTieraImage;

	@FXML
	private ImageView docOrbitImage;

	@FXML
	private HBox parentLogin;

	@FXML
	private Label loginText;

	@FXML
	private Label copyRightText;

	@FXML
	private Label allRightsText;

	@FXML
	private ImageView alfrescoImage;

	@FXML
	private AnchorPane loaderPane;

	@FXML
	private ImageView loaderGif;

	@FXML
	private MediaView loaderMp4;

	@FXML
	private AnchorPane loginParentAnchorPane;

	@FXML
	private void initialize() {

//		String videoPath = getClass().getResource("/videos/loader.mp4").toExternalForm();
//		Media media = new Media(videoPath);
//
//		// Create the MediaPlayer
//		MediaPlayer mediaPlayer = new MediaPlayer(media);
//
//		// Set up the MediaView
//		loaderMp4.setMediaPlayer(mediaPlayer);
//		mediaPlayer.setAutoPlay(true); // Start playing automatically
//
//		// Make sure the video resizes with the parent container
//		loaderMp4.fitWidthProperty().bind(loaderPane.widthProperty());
//		loaderMp4.fitHeightProperty().bind(loaderPane.heightProperty());
//		loaderMp4.setPreserveRatio(false);
//		loaderMp4.setSmooth(true);
//
//		// Handle end of video to show the login screen
//		mediaPlayer.setOnEndOfMedia(() -> {
//			loaderPane.setVisible(false); // Hide the loader video
//			loginAnchorPane.setVisible(true); // Show the login page
//		});

		loaderPane.setVisible(false);
		loginAnchorPane.setVisible(true);

		loginAnchorPane.prefWidthProperty().bind(loginParentAnchorPane.widthProperty());
		loginAnchorPane.prefHeightProperty().bind(loginParentAnchorPane.heightProperty());

		bgImage.fitWidthProperty().bind(loginAnchorPane.widthProperty());
		bgImage.fitHeightProperty().bind(loginAnchorPane.heightProperty());

		userName.setOnKeyPressed(this::handleEnterKeyPress);
//		password.setOnKeyPressed(this::handleEnterKeyPress);
		loginButton.setOnAction((event) -> {
			loginButtonHandleAction();
		});
		loginButton.defaultButtonProperty().bind(loginButton.focusedProperty());
		cancelButton.setOnAction((event) -> {
			userName.clear();
			password.clear();
		});

		/* Code for Login FXML maximizing -- Start */

		loginAnchorPane.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newWidth = newValue.doubleValue() * 0.16; // Adjust for padding/margins
			double newWidth1 = newValue.doubleValue() * 0.26;
			techTieraImage.setFitWidth(newWidth);
			docOrbitImage.setFitWidth(newWidth1);
			double newFontSize1 = Math.max(13, newWidth * 0.054);
			copyRightText.setStyle("-fx-font-size: " + newFontSize1 + "px;");
			allRightsText.setStyle("-fx-font-size: " + newFontSize1 + "px;");
			if (newFontSize1 != 13) {
				double newWidth2 = newValue.doubleValue() * 0.146;
				copyRightText.setPrefWidth(newWidth2);
				allRightsText.setPrefWidth(newWidth2);
			} else {
				copyRightText.setPrefWidth(220);
				allRightsText.setPrefWidth(145);
			}
		});

		loginAnchorPane.heightProperty().addListener((observable, oldValue, newValue) -> {
			double newHeight = newValue.doubleValue() * 0.06;
			double newHeight1 = newValue.doubleValue() * 0.24;
			techTieraImage.setFitHeight(newHeight);
			docOrbitImage.setFitHeight(newHeight1);
		});

		parentLogin.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newWidth = newValue.doubleValue() * 0.24;
			double newFontSize = Math.max(18, newWidth * 0.13);
			loginText.setStyle("-fx-font-size: " + newFontSize + "px;");
			alfrescoImage.setFitWidth(newWidth);
		});

		parentLogin.heightProperty().addListener((observable, oldValue, newValue) -> {
			double newHeight = newValue.doubleValue() * 0.12;
			double newHeigth1 = newValue.doubleValue() * 0.05;
			double newFontSize = Math.max(16, newHeight * 0.11);
			userName.setStyle("-fx-font-size: " + newFontSize + "px;");
//			password.setStyle("-fx-font-size: " + newFontSize + "px;");
			userName.setPrefHeight(newHeigth1);
//			password.setPrefHeight(newHeigth1);
			alfrescoImage.setFitHeight(newHeight);
		});

		/* Code for Login FXML maximizing -- End */

	}

	private void loginButtonHandleAction() {
		if (userName.getText() != null) {
//			logger.info("Selected Repository : " + PropertiesHolder.getRepositorySelected());
			boolean response = alfescoRestServiceUtility.getLoginAuth(userName.getText());
			System.out.println(PropertiesHolder.getAccessToken());
			if (response) {
				PropertiesHolder.setLoginUser(userName.getText());
				if (!UserSession.getIsUserLogout()) {
					JavaFxApplicationSupport.showView(MainView.class);
				} else {
					MainController mainController = (MainController) JavaFxApplicationSupport.getApplicationContext()
							.getBean(MainController.class);
					mainController.reset();
					JavaFxApplicationSupport.showView(MainView.class);
				}
				JavaFxApplicationSupport.showView(MainView.class);
			} else {
				ModernAlertUtil.showErrorAlert(Constants.LOGIN_FAILURE_TITLE, Constants.LOGIN_FAILURE_MESSAGE);
			}
		} else {
			ModernAlertUtil.showErrorAlert(Constants.LOGIN_VALIDATION_TITLE, Constants.LOGIN_VALIDATION_MESSAGE);
		}
	}

	private void handleEnterKeyPress(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			loginButtonHandleAction();
		}
	}

}
