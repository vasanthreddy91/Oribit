package com.techtiera.docorbit.jfxsupport;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.StageStyle;

import java.util.Optional;

/**
 * Modern Alert Utility Class with Professional Styling
 * Provides styled dialogs matching the application theme
 */
public class ModernAlertUtil {

	private static final String ALERT_CSS_PATH = "/css/alert-modern.css";
    
    /**
     * Apply modern styling to an alert
     */
    private static void applyModernStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        
        // Apply custom CSS
        try {
            String cssPath = ModernAlertUtil.class.getResource(ALERT_CSS_PATH).toExternalForm();
            dialogPane.getStylesheets().add(cssPath);
        } catch (Exception e) {
            // Fallback if CSS not found - log but don't crash
            System.err.println("Warning: Could not load alert CSS: " + e.getMessage());
        }
        
        // Add style class based on alert type
        switch (alert.getAlertType()) {
            case INFORMATION:
                dialogPane.getStyleClass().add("information");
                break;
            case CONFIRMATION:
                dialogPane.getStyleClass().add("confirmation");
                break;
            case WARNING:
                dialogPane.getStyleClass().add("warning");
                break;
            case ERROR:
                dialogPane.getStyleClass().add("error");
                break;
        }
        
        // Remove default JavaFX styling
        alert.initStyle(StageStyle.UNDECORATED);
        
        // Make dialog moveable (optional)
        makeDraggable(alert);
    }
    
    /**
     * Make alert draggable
     */
    private static void makeDraggable(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        final double[] xOffset = {0};
        final double[] yOffset = {0};
        
        dialogPane.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });
        
        dialogPane.setOnMouseDragged(event -> {
            alert.setX(event.getScreenX() - xOffset[0]);
            alert.setY(event.getScreenY() - yOffset[0]);
        });
    }
    
    /**
     * Show modern information dialog
     */
    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        applyModernStyle(alert);
        alert.showAndWait();
    }
    
    /**
     * Show modern success/confirmation dialog
     */
    public static void showSuccess(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        applyModernStyle(alert);
        alert.getDialogPane().getStyleClass().add("confirmation");
        alert.showAndWait();
    }
    
    /**
     * Show modern warning dialog
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        applyModernStyle(alert);
        alert.showAndWait();
    }
    
    /**
     * Show modern error dialog
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        applyModernStyle(alert);
        alert.showAndWait();
    }
    
    /**
     * Show modern confirmation dialog with Yes/No buttons
     * @return true if user clicked Yes/OK, false otherwise
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        applyModernStyle(alert);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Show modern confirmation dialog with custom buttons
     * @return true if user clicked the positive button, false otherwise
     */
    public static boolean showConfirmation(String title, String header, String content, 
                                          String positiveButton, String negativeButton) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        // Set custom button types
        ButtonType yesButton = new ButtonType(positiveButton);
        ButtonType noButton = new ButtonType(negativeButton);
        alert.getButtonTypes().setAll(yesButton, noButton);
        
        applyModernStyle(alert);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }
    
    /**
     * Legacy method - for backward compatibility
     * Replaces old AppUtil.infoBox()
     */
    public static void infoBox(String infoMessage, String headerText, String title) {
        showSuccess(title, headerText, infoMessage);
    }
    
    /**
     * Legacy method - for backward compatibility
     * Replaces old AppUtil.showErrorAlert()
     */
    public static void showErrorAlert(String title, String message) {
        showError(title, "Error", message);
    }
    public static ButtonType infoResponseBox(String infoMessage, String headerText, String title) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setContentText(infoMessage);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		applyModernStyle(alert);
		return alert.showAndWait().orElse(ButtonType.CANCEL);
	}
}