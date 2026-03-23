package com.techtiera.docorbit.alfresco.constants;

public class Constants {

	// Front end Constants
	public static final String SEPARATOR_DOT = ".";
	public static final String SEPARATOR_HYPEN = "-";
	public static final String SEPARATOR_FORWARD_SLASH = "/";
	public static final String SEPARATOR_BACKWARD_SLASH = "\\";
	public static final String SEPARATOR_DOUBLE_QUOTES = "\"";
	public static final String PATH_TXT = "Path : - ";
	public static final String TOTAL_RECORDS_TXT = "Total Records : - ";
	public static final String POPPINS_FONT = "Poppins";
	public static final String CSV_FILE_PATH_TXT = "CSV File Path : ";
	public static final String CONTENT_FOLD_PATH_TXT = "Content Folder Path : ";
	public static final String TARGET_PATH_TXT = "Target Path : ";

	// Back end Constants
	public static String SUCCESS = "success";
	public static String FAIL = "fail";
	public static String DOC_UPLOAD_STATUS = "Document uploaded successfully.";
	public static String rootSite = "Sites";
	public static String LOGIN_FAILURE_MESSAGE = "Please provide valid Username and Password to login";
	public static String LOGIN_FAILURE_TITLE = "Login Failed";
	public static String IMPORT_SUCCESS_MESSAGE = "Templates imported to the DocuSign Successfully. Total Count - ";
	public static String IMPORT_SUCCESS_TITLE = "Information";
	public static String IMPORT_SUCCESS_HEADER = "Import Status";
	public static String IMPORT_FAILURE_MESSAGE = "Templates failed to imported to the DocuSign.. Please check report";
	public static String IMPORT_FAILURE_MESSAGE_ENVELOPE = "Envelope failed to imported to the DocuSign.. Please check report";
	public static String IMPORT_FAILURE_TITLE = "Import Failed";
	public static String LOGOUT_MESSAGE = "Are you sure you want to log out?";
	public static String LOGOUT_TITLE = "Logout Confirmation !!!";
	public static String LOGOUT_HEADER = "Confirm Logout";
	public static String XML_RECORD_TAG = "record";
	public static String XML_PROPERTIES_TAG = "properties";
	public static String XML_PROPERTY_TAG = "property";
	public static String XML_NAME_TAG = "name";
	public static String XML_DOCUMENT_TYPE_TAG = "documentType";
	public static String TRANS_ADD_ATTR_MESSAGE = "Please select the Source and Target Types before adding attributes/properties.";
	public static String TRANS_ADD_ATTR_TITLE = "Select Type";
	public static String TRANS_ADD_ATTR_HEADER = "Select Type";
	public static String TRANS_CSV_SELECTION_MESSAGE = "Please select CSV/XML File in the Source Tab..!!!.";
	public static String TRANS_CSV_SELECTION_TITLE = "Select a CSV/XML";
	public static String TRANS_CSV_SELECTION_HEADER = "Select CSV/XML";
	public static String XML_TRANS_TAG = "transformation";
	public static String XML_TRANS_TYPES_TAG = "types";
	public static String XML_TRANS_TYPE_TAG = "type";
	public static String XML_TRANS_SOURCE_TYPE_TAG = "sourceType";
	public static String XML_TRANS_TARGET_TYPE_TAG = "targetType";
	public static String XML_TRANS_SOURCE_TAG = "source";
	public static String XML_TRANS_TARGET_TAG = "target";
	public static String XML_TRANS_FILE_NAME = "DocOrbit-transformation-xml.xml";
	public static String TRANS_DUPLICATE_VALUES_MESSAGE = "Duplicate attribute-property pairs found, Please ensure each pair is unique.";
	public static String TRANS_DUPLICATE_VALUES_HEADER = "Duplicate Pairs Found";
	public static String TRANS_DUPLICATE_VALUES_TITTLE = "Error - Duplicates";
	public static String TRANS_USED_VALUES_MESSAGE = "An attribute or property value has been used more than once, Please ensure each value is used only once.";
	public static String TRANS_USED_VALUES_HEADER = "Values Reused";
	public static String TRANS_USED_VALUES_TITTLE = "Error - Values Reused";
	public static String ALF_ANNOTATION_PROPERTY = "oa:isAnnotated";
	public static String ALF_ANNOTATION_TYPE = "oa:annotation";
	public static String ALF_ANNOTATION_FILE_EXTENSION = ".xfdf";

	public static String TRANSFORMATION_SUCCESS_MESSAGE = "Transformation properties are mapped successfully.";
	public static String TRANSFORMATION_SUCCESS_TITLE = "Mapping Transformation Properties";
	public static String TRANSFORMATION_SUCCESS_HEADER = "Mapping Status";
	public static String TRANSFORMATION_FAILURE_MESSAGE = "Failed to map transformation properties. Please check";
	public static String TRANSFORMATION_FAILURE_TITLE = "Mapping Failed";

	public static String XML_FILE_NAME = "DocOrbit-metadata-xml.xml";
	public static String XFDF_EXT = ".xfdf";
	public static String XML_VERSIONS_TAG = "versions";
	public static String RECORD_VERSIONS_NUMBER = "number";
	public static String XML_XSD_VALIDATION_MESSAGE = "XML and XSD Validation failed. Please check";
	public static String XML_XSD_VALIDATION_TITLE = "XML XSD Validation Failed";

	public static String XML_VERSION_TAG = "version";
	public static String XML_VALIDATION_SUCCESS_MESSAGE = "XML and XSD are Validated Successfully";
	public static String XML_VALIDATION_SUCCESS_TITLE = "Information";
	public static String XML_VALIDATION_SUCCESS_HEADER = "XML Validation Status";

	public static String XML_REPORT_FILE_NAME = "DocOrbit-report-status-xml.xml";
	public static String REPORTS_DOWNLOAD_FORMAT_PDF = "PDF";
	public static String REPORTS_DOWNLOAD_FORMAT_EXCEL = "Excel";
	public static String XML_SOURCE_TAG = "source";
	public static String RECORD_PROP_NAME = "name";
	public static String XML_ROOT_TAG = "records";
	public static String XML_SUB_TAG = "record";
	public static String RECORD_PROP_EXT = "extension";
	public static String RECORD_PROP_URL = "contentUrl";
	public static String RECORD_PROP_DOC_TYPE = "documentType";
	public static String RECORD_PROP_DOWNLOAD_STATUS = "uploadStatus";
	public static String RECORD_PROP_ERROR_MSG = "errorMessage";

	public static String LOGIN_VALIDATION_TITLE = "Login Validation";
	public static String LOGIN_VALIDATION_MESSAGE = "Please provide userid to login";

	// Docusign
	public static String AUDIENCE = "account-d.docusign.com";
	public static String SCOPE = "signature impersonation";
	public static String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";

	public static String USER_IMPORT_SUCCESS_MESSAGE = "Users imported to the Account Successfully. Total Count - ";
	public static String USER_IMPORT_FAILURE_MESSAGE = "Users failed to imported to the Account.. Please check report";
	public static String GROUP_IMPORT_SUCCESS_MESSAGE = "Groups imported to the Account Successfully. Total Count - ";
	public static String GROUP_IMPORT_FAILURE_MESSAGE = "Groups failed to imported to the Account.. Please check report";

	public static String XML_ENVELOP_REPORT_FILE_NAME = "DocOrbit-docusign-envelop-report-status";
	public static String XML_TEMPLATE_REPORT_FILE_NAME = "DocOrbit-docusign-template-report-status";
	public static String XML_EXT = ".xml";

	public static String RECORD_NAME = "template";
}
