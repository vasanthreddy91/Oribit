package com.techtiera.docorbit.config;

import java.util.Map;

import org.springframework.batch.core.Job;

import com.techtiera.docorbit.resource.Type;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;

public class PropertiesHolder {

	private static String csvFilePath;
	private static String nodeId;
	private static String nodePath;
	private static Job batchJob;
	private static long csvRecordsCount;
	private static String loginUser;
	private static String basicAuth;
	private static Type transXmlData;
	private static long recordsExecutionCount;
	private static TextArea logTextArea;
	private static ProgressIndicator logProcessLoader;
	private static boolean logProcessIndicatorStatus;
	private static boolean newTansformationTabStatus;
	private static String reportFolderPath;
	private static String selectedReportDownloadFormat;
	// Docusign
	private static String accessToken;
	private static String userFilePath;
	private static String groupFilePath;
	private static String profileFilePath;
	private static String templateFilePath;
	private static String envelopeFilePath;
	private static String templateContentFolderPath;
	private static String envelopContentFolderPath;
	private static String contentFolderPath;
	private static long templateRecordsExecutionCount;
	private static long templateRecordsCount;
	private static String accountId;
	private static String docusignversion;
	private static long envelopRecordsExecutionCount;
	private static long envelopRecordsCount;
	private static long groupRecordsExecutionCount;
	private static long groupRecordsCount;
	private static String selectedEnvelopeReportDownloadFormat;
	private static String templateReportFolderPath;
	private static String envelopeReportFolderPath;

	private static Map<String, String> userDetails;

	public static Map<String, String> getUserDetails() {
		return userDetails;
	}

	public static void setUserDetails(Map<String, String> userDetails) {
		PropertiesHolder.userDetails = userDetails;
	}

	public static long getGroupRecordsCount() {
		return groupRecordsCount;
	}

	public static void setGroupRecordsCount(long groupRecordsCount) {
		PropertiesHolder.groupRecordsCount = groupRecordsCount;
	}

	public static long getGroupRecordsExecutionCount() {
		return groupRecordsExecutionCount;
	}

	public static void setGroupRecordsExecutionCount(long groupRecordsExecutionCount) {
		PropertiesHolder.groupRecordsExecutionCount = groupRecordsExecutionCount;
	}

	public static long getTemplateRecordsCount() {
		return templateRecordsCount;
	}

	public static void setTemplateRecordsCount(long templateRecordsCount) {
		PropertiesHolder.templateRecordsCount = templateRecordsCount;
	}

	public static long getEnvelopRecordsCount() {
		return envelopRecordsCount;
	}

	public static void setEnvelopRecordsCount(long envelopRecordsCount) {
		PropertiesHolder.envelopRecordsCount = envelopRecordsCount;
	}

	public static long getEnvelopRecordsExecutionCount() {
		return envelopRecordsExecutionCount;
	}

	public static void setEnvelopRecordsExecutionCount(long envelopRecordsExecutionCount) {
		PropertiesHolder.envelopRecordsExecutionCount = envelopRecordsExecutionCount;
	}

	public static String getDocusignversion() {
		return docusignversion;
	}

	public static void setDocusignversion(String docusignversion) {
		PropertiesHolder.docusignversion = docusignversion;
	}

	public static String getAccountId() {
		return accountId;
	}

	public static void setAccountId(String accountId) {
		PropertiesHolder.accountId = accountId;
	}

	public static long getTemplateRecordsExecutionCount() {
		return templateRecordsExecutionCount;
	}

	public static void setTemplateRecordsExecutionCount(long templateRecordsExecutionCount) {
		PropertiesHolder.templateRecordsExecutionCount = templateRecordsExecutionCount;
	}

	private static long userRecordsExecutionCount;
	private static long userRecordsCount;

	public static String getTemplateContentFolderPath() {
		return templateContentFolderPath;
	}

	public static void setTemplateContentFolderPath(String templateContentFolderPath) {
		PropertiesHolder.templateContentFolderPath = templateContentFolderPath;
	}

	public static String getEnvelopContentFolderPath() {
		return envelopContentFolderPath;
	}

	public static void setEnvelopContentFolderPath(String envelopContentFolderPath) {
		PropertiesHolder.envelopContentFolderPath = envelopContentFolderPath;
	}

	public static void setAccessToken(String accessToken) {
		PropertiesHolder.accessToken = accessToken;
	}

	public static String getAccessToken() {
		return accessToken;
	}

	public static String getContentFolderPath() {
		return contentFolderPath;
	}

	public static void setContentFolderPath(String contentFolderPath) {
		PropertiesHolder.contentFolderPath = contentFolderPath;
	}

	public static String getCsvFilePath() {
		return csvFilePath;
	}

	public static void setCsvFilePath(String csvFilePath) {
		PropertiesHolder.csvFilePath = csvFilePath;
	}

	public static void setUserFilePath(String userFilePath) {
		PropertiesHolder.userFilePath = userFilePath;
	}

	public static String getUserFilePath() {
		return userFilePath;
	}

	public static void setGroupFilePath(String groupFilePath) {
		PropertiesHolder.groupFilePath = groupFilePath;
	}

	public static String getGroupFilePath() {
		return groupFilePath;
	}

	public static void setProfileFilePath(String profileFilePath) {
		PropertiesHolder.profileFilePath = profileFilePath;
	}

	public static String getProfileFilePath() {
		return profileFilePath;
	}

	public static void setTemplateFilePath(String templateFilePath) {
		PropertiesHolder.templateFilePath = templateFilePath;
	}

	public static String getTemplateFilePath() {
		return templateFilePath;
	}

	public static void setEnvelopeFilePath(String envelopeFilePath) {
		PropertiesHolder.envelopeFilePath = envelopeFilePath;
	}

	public static String getEnvelopeFilePath() {
		return envelopeFilePath;
	}

	public static String getNodeId() {
		return nodeId;
	}

	public static String getLoginUser() {
		return loginUser;
	}

	public static void setLoginUser(String loginUser) {
		PropertiesHolder.loginUser = loginUser;
	}

	public static void setNodeId(String nodeId) {
		PropertiesHolder.nodeId = nodeId;
	}

	public static String getNodePath() {
		return nodePath;
	}

	public static void setNodePath(String nodePath) {
		PropertiesHolder.nodePath = nodePath;
	}

	public static Job getBatchJob() {
		return batchJob;
	}

	public static void setBatchJob(Job batchJob) {
		PropertiesHolder.batchJob = batchJob;
	}

	public static long getCsvRecordsCount() {
		return csvRecordsCount;
	}

	public static void setCsvRecordsCount(long csvRecordsCount) {
		PropertiesHolder.csvRecordsCount = csvRecordsCount;
	}

	public static long getUserRecordsCount() {
		return userRecordsCount;
	}

	public static void setUserRecordsCount(long userRecordsCount) {
		PropertiesHolder.userRecordsCount = userRecordsCount;
	}

	public static String getBasicAuth() {
		return basicAuth;
	}

	public static void setBasicAuth(String basicAuth) {
		PropertiesHolder.basicAuth = basicAuth;
	}

	public static Type getTransXmlData() {
		return transXmlData;
	}

	public static void setTransXmlData(Type transXmlData) {
		PropertiesHolder.transXmlData = transXmlData;
	}

	public static long getRecordsExecutionCount() {
		return recordsExecutionCount;
	}

	public static void setRecordsExecutionCount(long recordsExecutionCount) {
		PropertiesHolder.recordsExecutionCount = recordsExecutionCount;
	}

	public static long getUserRecordsExecutionCount() {
		return userRecordsExecutionCount;
	}

	public static void setUserRecordsExecutionCount(long userRecordsExecutionCount) {
		PropertiesHolder.userRecordsExecutionCount = userRecordsExecutionCount;
	}

	public static TextArea getLogTextArea() {
		return logTextArea;
	}

	public static void setLogTextArea(TextArea logTextArea) {
		PropertiesHolder.logTextArea = logTextArea;
	}

	public static ProgressIndicator getLogProcessLoader() {
		return logProcessLoader;
	}

	public static void setLogProcessLoader(ProgressIndicator logProcessLoader) {
		PropertiesHolder.logProcessLoader = logProcessLoader;
	}

	public static boolean isLogProcessIndicatorStatus() {
		return logProcessIndicatorStatus;
	}

	public static void setLogProcessIndicatorStatus(boolean logProcessIndicatorStatus) {
		PropertiesHolder.logProcessIndicatorStatus = logProcessIndicatorStatus;
	}

	public static boolean isNewTansformationTabStatus() {
		return newTansformationTabStatus;
	}

	public static void setNewTansformationTabStatus(boolean newTansformationTabStatus) {
		PropertiesHolder.newTansformationTabStatus = newTansformationTabStatus;
	}

	public static String getReportFolderPath() {
		return reportFolderPath;
	}

	public static void setReportFolderPath(String reportFolderPath) {
		PropertiesHolder.reportFolderPath = reportFolderPath;
	}

	public static String getSelectedReportDownloadFormat() {
		return selectedReportDownloadFormat;
	}

	public static void setSelectedReportDownloadFormat(String selectedReportDownloadFormat) {
		PropertiesHolder.selectedReportDownloadFormat = selectedReportDownloadFormat;
	}

	public static String getSelectedEnvelopeReportDownloadFormat() {
		return selectedEnvelopeReportDownloadFormat;
	}

	public static void setSelectedEnvelopeReportDownloadFormat(String selectedEnvelopeReportDownloadFormat) {
		PropertiesHolder.selectedEnvelopeReportDownloadFormat = selectedEnvelopeReportDownloadFormat;
	}

	public static String getTemplateReportFolderPath() {
		return templateReportFolderPath;
	}

	public static void setTemplateReportFolderPath(String templateReportFolderPath) {
		PropertiesHolder.templateReportFolderPath = templateReportFolderPath;
	}

	public static String getEnvelopeReportFolderPath() {
		return envelopeReportFolderPath;
	}

	public static void setEnvelopeReportFolderPath(String envelopeReportFolderPath) {
		PropertiesHolder.envelopeReportFolderPath = envelopeReportFolderPath;
	}

}
