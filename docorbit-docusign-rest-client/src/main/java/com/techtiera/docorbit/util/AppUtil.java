package com.techtiera.docorbit.util;

import com.techtiera.docorbit.alfresco.constants.Constants;
import com.techtiera.docorbit.alfresco.exception.ErrorInfo;
import com.techtiera.docorbit.alfresco.exception.ErrorInfoDynamic;
import com.techtiera.docorbit.resource.DocorbitResponse;
import com.techtiera.docorbit.resource.ErrorData;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AppUtil {

	public static DocorbitResponse prepareSuccessResponse(final Object data) {
		final var response = new DocorbitResponse();
		response.setData(data);
		response.setStatus(Constants.SUCCESS);
		return response;
	}

	public static DocorbitResponse prepareFailResponse(final ErrorInfo errorInfo, final String message) {
		final var errorResponse = new DocorbitResponse();
		errorResponse.setData(null);
		errorResponse.setStatus(Constants.FAIL);
		final var errorData = new ErrorData();
		errorData.setErrorKey(errorInfo.getErrorI18NKey());
		errorData.setErrorMessage(errorInfo.getErrorMessage());
		errorData.setErrorDescription(message);
		errorResponse.setError(errorData);
		return errorResponse;
	}

	public static DocorbitResponse prepareFailResponses(final ErrorInfoDynamic errorInfo, final String message) {
		final var errorResponse = new DocorbitResponse();
		errorResponse.setData(null);
		errorResponse.setStatus(Constants.FAIL);
		final var errorData = new ErrorData();
		errorData.setErrorKey(errorInfo.getErrorKey());
		errorData.setErrorMessage(errorInfo.getErrorMessage());
		errorData.setErrorDescription(message);
		errorResponse.setError(errorData);
		return errorResponse;
	}

	public static void showErrorAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.show();
	}

	public static void infoBox(String infoMessage, String headerText, String title) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setContentText(infoMessage);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.showAndWait();
	}
	
	public static ButtonType infoResponseBox(String infoMessage, String headerText, String title) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setContentText(infoMessage);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		return alert.showAndWait().orElse(ButtonType.CANCEL);
	}

}
