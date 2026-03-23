package com.techtiera.docorbit.alfresco.exception;

import lombok.Getter;

@Getter
public enum ErrorInfo {

	// user errors
	INVALID_INPUT("1000", "invalid.input", "Invalid Input", "Invalid Input"),

	// server errors
	INTERNAL_SERVER("2000", "internal.server", "Internal Server", "Internal Server"),
	RESOURCE_NOT_EXIST("2001", "resource.not.exist", "resource not exist", "resource not exist"),

	CRON_EXPRESSION_NOT_FOUND("2002", "cronexpression.not.exist", "cronexpression not exist",
			"cronexpression not exist"),
	USER_CREATION_ERROR("2003", "user.creation.error", "Error occured while creating user",
			"Error occured while creating user"),
	USER_DETAILS_OKTA_ERROR("2004", "okta.user.not.exist", "User not exist in okta", "User not exist in okta"),

	OKTA_USER_NOT_ACTIVE("2005", "okta.user.not.active", "User not active in Okta", "User not active in Okta"),
	USER_UPDATE_ERROR("2006", "user.update.error", "Error occured while updating user",
			"Error occured while updating user"),
	USER_ACTICATION_ERROR("2007", "user.activation.error", "Error occured while activating user",
			"Error occured while activating user"),
	RESET_PASSWORD_ERROR("2008", "user.reset.password.error", "Error occured while resetting user password",
			"Error occured while resetting user password"),
	OKTA_ID_NOT_FIND("2009", "okta.id.not.exist", "okta id not mapped to user", "okta id not mapped to user"),
	AUTHORIZATION_HEADER_MISSING("2010", "oauthorization.id.not.exist", "oauthorization id not found",
			"oauthorization id not found"),
	OTCS_AUTH_TICKET_ERROR("2011", "otcs.auth.ticket.error", "otcs.auth.ticket.error", "OTCS ticket is not generated."),
	OTCS_TICKET_HEADER_NULL("2012", "otcs.ticket.header.null", "otcs.ticket.header.null",
			"OTCS ticket header is null. Please provide Header."),
	FOLDER_CREATION_ERROR("2013", "folder.creation.error", "folder.creation.error", "Folder creation error in xECM."),
	DOCUMENT_CREATION_ERROR("2014", "document.creation.error", "document.creation.error", "Document creation error in xECM."),
	IS_NODE_EXISTS_ERROR("2015", "node.exists.error", "node.exists.error", "Node is not avaiable in xECM."),
	APPLY_CATEGORY_TO_DOCUMENT_ERROR("2014", "apply.category.to.document.error", "apply.category.to.document.error", "Category is not applied to the Document in xECM."),
	LIST_OF_NODES_ERROR("2015", "list.of.nodes.error", "list.of.nodes.error", "Error while getting list of nodes from xECM.")
	;

	ErrorInfo(final String errorCode, final String errorI18NKey, final String errorMessage,
			final String errorDescription) {
		this.errorCode = errorCode;
		this.errorI18NKey = errorI18NKey;
		this.errorMessage = errorMessage;
		this.errorDescription = errorDescription;
	}

	public final String errorCode;

	public final String errorI18NKey;

	public final String errorMessage;

	public final String errorDescription;
}
