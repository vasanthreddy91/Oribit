package com.techtiera.docorbit.controller;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techtiera.docorbit.alfresco.constants.Constants;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.jfxsupport.FXMLController;
import com.techtiera.docorbit.jfxsupport.ModernAlertUtil;
import com.techtiera.docorbit.util.RecordEnvelopeProperties;
import com.techtiera.docorbit.util.RecordProperties;
import com.techtiera.docorbit.util.RecordTemplateProperties;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

@FXMLController
public class ReportController {

	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

	@FXML
	private TableView<RecordProperties> reportRecordTableView;

	@FXML
	private TableView<RecordTemplateProperties> reportTemplateRecordTableView;

	@FXML
	private TableView<RecordEnvelopeProperties> reportEnvelopeRecordTableView;

	private File xmlFile;

	private File templatexmlFile;

	private File envelopexmlFile;

	@FXML
	private ComboBox<String> formatChooserComboBox;

	@FXML
	private Button downloadReportsbtn;

	@FXML
	private PieChart reportsPieChart;

	@FXML
	private PieChart reportsTemplatePieChart;

	@FXML
	private PieChart reportsEnvelopePieChart;

	@FXML
	private VBox vboxStatusCount;

	@FXML
	private VBox vboxTemplateStatusCount;

	@FXML
	private VBox vboxEnvelopeStatusCount;

	@FXML
	private ImageView refreshTableImageView;

	@FXML
	private ImageView refreshTemplateTableImageView;

	@FXML
	private ImageView refreshEnvelopeTableImageView;

	@FXML
	private ComboBox<String> formatChooserTemplateComboBox;

	@FXML
	private ComboBox<String> formatChooserEnvelopeComboBox;

	@FXML
	private Button downloadTemplateReportsbtn;

	private boolean templateFlag = false;

	private boolean envelopeFlag = false;

	@FXML
	public void initialize() {

		refreshTemplateReports();
		refreshEnvelopeReports();

	}

	public void refreshTemplateReports() {
		System.out.println("report controller initialized");
		String reportsTemplateXMLfilePath = PropertiesHolder.getTemplateReportFolderPath()
				+ "\\DocOrbit-docusign-import-template-report-status.xml";
		System.out.println("reportsTemplateXMLfilePath---" + reportsTemplateXMLfilePath);
		if (reportsTemplateXMLfilePath != null && !reportsTemplateXMLfilePath.isEmpty()) {
			XMLTemplateTableView tableView = new XMLTemplateTableView();
			templatexmlFile = new File(reportsTemplateXMLfilePath);
			if (templatexmlFile.exists()) {
				TableView<RecordTemplateProperties> recordView = tableView.readTemplateXMLData(templatexmlFile);
				reportTemplateRecordTableView.getColumns().clear();
				reportTemplateRecordTableView.getColumns().addAll(recordView.getColumns());
				reportTemplateRecordTableView.setItems(recordView.getItems());
				reportTemplateRecordTableView.setVisible(true);
				Platform.runLater(() -> {
					createTemplateDownloadStatusPieChart(recordView.getItems());
				});

			} else {
				logger.info("XML file not found or invalid.");
			}
		} else {
			logger.info("Invalid XML file path.");
		}

		String downloadForamts[] = { Constants.REPORTS_DOWNLOAD_FORMAT_PDF, Constants.REPORTS_DOWNLOAD_FORMAT_EXCEL };

		formatChooserTemplateComboBox.getItems().addAll(downloadForamts);
		formatChooserTemplateComboBox.setOnAction(event -> {
			String selectedFormatType = formatChooserTemplateComboBox.getSelectionModel().getSelectedItem().toString();
			logger.info("Selected Report Download Format : - " + selectedFormatType);
			PropertiesHolder.setSelectedReportDownloadFormat(selectedFormatType);
		});

		refreshTemplateTableImageView.setOnMouseClicked(event -> {
			refreshTemplateTableView();
		});

		templateFlag = true;
	}

	public void refreshEnvelopeReports() {
		System.out.println("initialize method is called");
		String reportsEnvelopeXMLfilePath = PropertiesHolder.getEnvelopeReportFolderPath()
				+ "\\DocOrbit-docusign-import-envelope-report-status.xml";
		System.out.println("reportsEnvelopeXMLfilePath---" + reportsEnvelopeXMLfilePath);
		if (reportsEnvelopeXMLfilePath != null && !reportsEnvelopeXMLfilePath.isEmpty()) {
			XMLEnvelopeTableView tableView = new XMLEnvelopeTableView();
			envelopexmlFile = new File(reportsEnvelopeXMLfilePath);
			if (envelopexmlFile.exists()) {
				TableView<RecordEnvelopeProperties> recordView = tableView.readEnvelopeXMLData(envelopexmlFile);
				reportEnvelopeRecordTableView.getColumns().clear();
				reportEnvelopeRecordTableView.getColumns().addAll(recordView.getColumns());
				reportEnvelopeRecordTableView.setItems(recordView.getItems());
				reportEnvelopeRecordTableView.setVisible(true);
				Platform.runLater(() -> {
					createEnvelopeDownloadStatusPieChart(recordView.getItems());
				});

			} else {
				logger.info("XML file not found or invalid.");
			}
		} else {
			logger.info("Invalid XML file path.");
		}
		String downloadForamts[] = { Constants.REPORTS_DOWNLOAD_FORMAT_PDF, Constants.REPORTS_DOWNLOAD_FORMAT_EXCEL };

		formatChooserEnvelopeComboBox.getItems().addAll(downloadForamts);
		formatChooserEnvelopeComboBox.setOnAction(event -> {
			String selectedFormatType = formatChooserEnvelopeComboBox.getSelectionModel().getSelectedItem().toString();
			logger.info("Selected Report Download Format : - " + selectedFormatType);
			PropertiesHolder.setSelectedEnvelopeReportDownloadFormat(selectedFormatType);
		});

		refreshEnvelopeTableImageView.setOnMouseClicked(event -> {
			refreshEnvelopeTableView();
		});

		envelopeFlag = true;
	}

	@FXML
	public void downloadReportsAction(ActionEvent event) {
		logger.info("Clicked on Download Button");
		String selectedFormatType = formatChooserComboBox.getSelectionModel().getSelectedItem();
		if (selectedFormatType == null || selectedFormatType.isEmpty()) {
			logger.warn("No format selected for download.");
			ModernAlertUtil.showErrorAlert("No format selected", "Please select a format to download the report");
			return;
		}
		logger.info("Selected Report Download Format: " + selectedFormatType);

		ObservableList<RecordProperties> records = reportRecordTableView.getItems();
		if (records.isEmpty()) {
			logger.warn("No data available for download.");
			return;
		}
		String reportFolderPath = PropertiesHolder.getReportFolderPath();
		String fileNamePrefix = "DocOrbit-Report-";
		String timestamp = new java.text.SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new java.util.Date());
		String filePath = reportFolderPath + Constants.SEPARATOR_BACKWARD_SLASH + fileNamePrefix + selectedFormatType
				+ "-" + timestamp;

		try {
			if (selectedFormatType.equals(Constants.REPORTS_DOWNLOAD_FORMAT_PDF)) {
				generatePDFReport(records, filePath + ".pdf");
			} else if (selectedFormatType.equals(Constants.REPORTS_DOWNLOAD_FORMAT_EXCEL)) {
				generateExcelReport(records, filePath + ".xlsx");
			} else {
				logger.warn("Unsupported format selected.");
				ModernAlertUtil.showErrorAlert("Unsupported format selected", "Please select a supported format");
			}
		} catch (Exception e) {
			logger.error("Error generating report: ", e);
			ModernAlertUtil.showErrorAlert("Error generating report", "Error generating report");
		}
	}

	private void generatePDFReport(ObservableList<RecordProperties> records, String outputFilePath) throws Exception {
		com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4.rotate());
		com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(outputFilePath));

		document.open();
		document.add(new com.itextpdf.text.Paragraph("Report, Date: " + new java.util.Date()));
		document.add(new com.itextpdf.text.Paragraph("\n"));
		int recordCount = records.size();
		document.add(new com.itextpdf.text.Paragraph("Records Count: " + recordCount));
		document.add(new com.itextpdf.text.Paragraph("\n"));

		com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10); // Font
		com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12,
				com.itextpdf.text.Font.BOLD);

		com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(
				reportRecordTableView.getColumns().size() + 1);
		float[] columnWidths = new float[reportRecordTableView.getColumns().size() + 1];
		columnWidths[0] = 0.6f; // s.no. column
		columnWidths[1] = 2.0f; // Name column
		columnWidths[2] = 1.2f; // Extension column
		columnWidths[3] = 3.7f; // Content URL column
		columnWidths[4] = 1.3f; // Document Type column
		columnWidths[5] = 1.2f; // Download Status column
		columnWidths[6] = 3.7f; // Error Message column

		table.setWidths(columnWidths);
		table.setWidthPercentage(90f);

		com.itextpdf.text.Phrase slNoHeader = new com.itextpdf.text.Phrase("S.No", headerFont);
		table.addCell(slNoHeader);
		for (int i = 0; i < reportRecordTableView.getColumns().size(); i++) {
			com.itextpdf.text.Phrase header = new com.itextpdf.text.Phrase(
					reportRecordTableView.getColumns().get(i).getText(), headerFont);
			table.addCell(header);
		}
		int slNo = 1;
		for (RecordProperties record : records) {
			table.addCell(new com.itextpdf.text.Phrase(String.valueOf(slNo++), dataFont));
			for (int i = 0; i < reportRecordTableView.getColumns().size(); i++) {
				Object value = reportRecordTableView.getColumns().get(i).getCellData(record);
				table.addCell(new com.itextpdf.text.Phrase(value != null ? value.toString() : "", dataFont));
			}
		}
		document.add(table);
		document.close();
		logger.info("PDF Report generated at: " + outputFilePath);
		ModernAlertUtil.infoBox("PDF Report generated at: " + outputFilePath, "PDF Report downloaded successfully",
				"Download Status");
	}

	private void generateExcelReport(ObservableList<RecordProperties> records, String outputFilePath) throws Exception {
		org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
		org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Report");

		org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
		org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
		boldFont.setBold(true);
		headerStyle.setFont(boldFont);
		headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

		org.apache.poi.ss.usermodel.CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		cellStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		cellStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		cellStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

		org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);

		org.apache.poi.ss.usermodel.Cell serialNoHeaderCell = headerRow.createCell(0);
		serialNoHeaderCell.setCellValue("s.no");
		serialNoHeaderCell.setCellStyle(headerStyle);

		for (int i = 0; i < reportRecordTableView.getColumns().size(); i++) {
			org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i + 1);
			cell.setCellValue(reportRecordTableView.getColumns().get(i).getText());
			cell.setCellStyle(headerStyle);
		}
		int rowNum = 1;
		for (RecordProperties record : records) {
			org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
			org.apache.poi.ss.usermodel.Cell serialNoCell = row.createCell(0);
			serialNoCell.setCellValue(rowNum - 1);
			serialNoCell.setCellStyle(cellStyle);
			for (int i = 0; i < reportRecordTableView.getColumns().size(); i++) {
				Object value = reportRecordTableView.getColumns().get(i).getCellData(record);
				org.apache.poi.ss.usermodel.Cell cell = row.createCell(i + 1);
				cell.setCellValue(value != null ? value.toString() : "");
				cell.setCellStyle(cellStyle);
			}
		}
		for (int i = 0; i < reportRecordTableView.getColumns().size() + 1; i++) {
			sheet.autoSizeColumn(i);
		}
		try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(outputFilePath)) {
			workbook.write(fileOut);
		}
		workbook.close();
		logger.info("Excel Report generated at: " + outputFilePath);
		ModernAlertUtil.infoBox("Excel Report generated at: " + outputFilePath, "Excel Report downloaded successfully",
				"Download Status");
	}

	private void createDownloadStatusPieChart(ObservableList<RecordProperties> recordPropertiesList) {
		int trueCount = 0;
		int falseCount = 0;
		int totalCount = 0;
		for (RecordProperties record : recordPropertiesList) {
			if (record.getDownloadStatus()) {
				trueCount++;
			} else {
				falseCount++;
			}
			totalCount++;
		}
		PieChart.Data trueStatusSlice = new PieChart.Data("Pass ", trueCount);
		PieChart.Data falseStatusSlice = new PieChart.Data("Fail ", falseCount);
		reportsPieChart.getData().clear();
		reportsPieChart.getData().addAll(trueStatusSlice, falseStatusSlice);
		reportsPieChart.setLegendVisible(false);
		trueStatusSlice.getNode().setStyle("-fx-pie-color: green;");
		falseStatusSlice.getNode().setStyle("-fx-pie-color: red;");
		Label totalCountLabel = new Label("Total : " + totalCount);
		Label trueCountLabel = new Label("Pass : " + trueCount);
		Label falseCountLabel = new Label("Fail : " + falseCount);
		trueCountLabel.setTextFill(javafx.scene.paint.Color.GREEN);
		falseCountLabel.setTextFill(javafx.scene.paint.Color.RED);
		totalCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		trueCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		falseCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		double truePercentage = (double) trueCount / totalCount * 100;
		double falsePercentage = (double) falseCount / totalCount * 100;
		Tooltip trueTooltip = new Tooltip(String.format("Pass: %.2f%%", truePercentage));
		Tooltip falseTooltip = new Tooltip(String.format("Fail: %.2f%%", falsePercentage));
		Tooltip.install(trueStatusSlice.getNode(), trueTooltip);
		Tooltip.install(falseStatusSlice.getNode(), falseTooltip);
		vboxStatusCount.getChildren().addAll(totalCountLabel, trueCountLabel, falseCountLabel);
		trueStatusSlice.getNode().setOnMouseClicked(event -> {
			filterTableView(recordPropertiesList, true);
		});

		falseStatusSlice.getNode().setOnMouseClicked(event -> {
			filterTableView(recordPropertiesList, false);
		});
	}

	private void filterTableView(ObservableList<RecordProperties> recordPropertiesList, boolean filterValue) {

		ObservableList<RecordProperties> filteredItems = FXCollections.observableArrayList();

		for (RecordProperties record : recordPropertiesList) {
			if (record.getDownloadStatus() == filterValue) {
				filteredItems.add(record);
			}
		}
		reportRecordTableView.setItems(filteredItems);
	}

//	public void refreshTableView() {
//		if (xmlFile.exists()) {
//			XMLTableView tableView = new XMLTableView();
//			TableView<RecordProperties> recordView = tableView.readXMLData(xmlFile, "");
//			reportRecordTableView.getColumns().clear();
//			reportRecordTableView.getColumns().addAll(recordView.getColumns());
//			reportRecordTableView.setItems(recordView.getItems());
//		} else {
//			logger.error("XML file not found or invalid.");
//		}
//	}

//	public void refreshTemplateTableView() {
//		if (templatexmlFile.exists()) {
//			XMLTemplateTableView tableView = new XMLTemplateTableView();
//			TableView<RecordTemplateProperties> recordView = tableView.readTemplateXMLData(templatexmlFile);
//			reportTemplateRecordTableView.getColumns().clear();
//			reportTemplateRecordTableView.getColumns().addAll(recordView.getColumns());
//			reportTemplateRecordTableView.setItems(recordView.getItems());
//		} else {
//			logger.error("XML file not found or invalid.");
//		}
//	}

//	public void refreshEnvelopeTableView() {
//		if (envelopexmlFile.exists()) {
//			XMLEnvelopeTableView tableView = new XMLEnvelopeTableView();
//			TableView<RecordEnvelopeProperties> recordView = tableView.readEnvelopeXMLData(envelopexmlFile);
//			reportEnvelopeRecordTableView.getColumns().clear();
//			reportEnvelopeRecordTableView.getColumns().addAll(recordView.getColumns());
//			reportEnvelopeRecordTableView.setItems(recordView.getItems());
//		} else {
//			logger.error("XML file not found or invalid.");
//		}
//	}

	private void createTemplateDownloadStatusPieChart(ObservableList<RecordTemplateProperties> recordPropertiesList) {
		int trueCount = 0;
		int falseCount = 0;
		int totalCount = 0;
		for (RecordTemplateProperties record : recordPropertiesList) {
			if (record.getDownloadStatus()) {
				trueCount++;
			} else {
				falseCount++;
			}
			totalCount++;
		}
		PieChart.Data trueStatusSlice = new PieChart.Data("Pass ", trueCount);
		PieChart.Data falseStatusSlice = new PieChart.Data("Fail ", falseCount);
		reportsTemplatePieChart.getData().clear();
		reportsTemplatePieChart.getData().addAll(trueStatusSlice, falseStatusSlice);
		reportsTemplatePieChart.setLegendVisible(false);
		trueStatusSlice.getNode().setStyle("-fx-pie-color: green;");
		falseStatusSlice.getNode().setStyle("-fx-pie-color: red;");
		Label totalCountLabel = new Label("Total : " + totalCount);
		Label trueCountLabel = new Label("Pass : " + trueCount);
		Label falseCountLabel = new Label("Fail : " + falseCount);
		trueCountLabel.setTextFill(javafx.scene.paint.Color.GREEN);
		falseCountLabel.setTextFill(javafx.scene.paint.Color.RED);
		totalCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		trueCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		falseCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		double truePercentage = (double) trueCount / totalCount * 100;
		double falsePercentage = (double) falseCount / totalCount * 100;
		Tooltip trueTooltip = new Tooltip(String.format("Pass: %.2f%%", truePercentage));
		Tooltip falseTooltip = new Tooltip(String.format("Fail: %.2f%%", falsePercentage));
		Tooltip.install(trueStatusSlice.getNode(), trueTooltip);
		Tooltip.install(falseStatusSlice.getNode(), falseTooltip);
		vboxTemplateStatusCount.getChildren().addAll(totalCountLabel, trueCountLabel, falseCountLabel);
		trueStatusSlice.getNode().setOnMouseClicked(event -> {
			filterTemplateTableView(recordPropertiesList, true);
		});

		falseStatusSlice.getNode().setOnMouseClicked(event -> {
			filterTemplateTableView(recordPropertiesList, false);
		});
	}

	private void filterTemplateTableView(ObservableList<RecordTemplateProperties> recordPropertiesList,
			boolean filterValue) {

		ObservableList<RecordTemplateProperties> filteredItems = FXCollections.observableArrayList();

		for (RecordTemplateProperties record : recordPropertiesList) {
			if (record.getDownloadStatus() == filterValue) {
				filteredItems.add(record);
			}
		}
		reportTemplateRecordTableView.setItems(filteredItems);
	}

	// ------------------ Template -------------------------

	@FXML
	public void downloadTemplateReportsAction(ActionEvent event) {
		logger.info("Clicked on Template Download Button");
		String selectedFormatType = formatChooserTemplateComboBox.getSelectionModel().getSelectedItem();
		if (selectedFormatType == null || selectedFormatType.isEmpty()) {
			logger.warn("No format selected for download.");
			ModernAlertUtil.showErrorAlert("No format selected", "Please select a format to download the report");
			return;
		}
		logger.info("Selected Report Download Format: " + selectedFormatType);

		ObservableList<RecordTemplateProperties> records = reportTemplateRecordTableView.getItems();
		if (records.isEmpty()) {
			logger.warn("No data available for download.");
			return;
		}
		String reportFolderPath = PropertiesHolder.getTemplateReportFolderPath();
		String fileNamePrefix = "DocOrbit-Template-Report-";
		String timestamp = new java.text.SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new java.util.Date());
		String filePath = reportFolderPath + Constants.SEPARATOR_BACKWARD_SLASH + fileNamePrefix + "-"
				+ selectedFormatType + "-" + timestamp;

		try {
			if (selectedFormatType.equals(Constants.REPORTS_DOWNLOAD_FORMAT_PDF)) {
				generateTemplatePDFReport(records, filePath + ".pdf");
			} else if (selectedFormatType.equals(Constants.REPORTS_DOWNLOAD_FORMAT_EXCEL)) {
				generateTemplateExcelReport(records, filePath + ".xlsx");
			} else {
				logger.warn("Unsupported format selected.");
				ModernAlertUtil.showErrorAlert("Unsupported format selected", "Please select a supported format");
			}
		} catch (Exception e) {
			logger.error("Error generating report: ", e);
			ModernAlertUtil.showErrorAlert("Error generating report", "Error generating report");
		}
	}

	private void generateTemplatePDFReport(ObservableList<RecordTemplateProperties> records, String outputFilePath)
			throws Exception {
		com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4.rotate());
		com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(outputFilePath));

		document.open();
		document.add(new com.itextpdf.text.Paragraph("Report, Date: " + new java.util.Date()));
		document.add(new com.itextpdf.text.Paragraph("\n"));
		int recordCount = records.size();
		document.add(new com.itextpdf.text.Paragraph("Records Count: " + recordCount));
		document.add(new com.itextpdf.text.Paragraph("\n"));

		com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10); // Font
		com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12,
				com.itextpdf.text.Font.BOLD);

		com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(
				reportTemplateRecordTableView.getColumns().size() + 1);
		float[] columnWidths = new float[reportTemplateRecordTableView.getColumns().size() + 1];
		columnWidths[0] = 0.6f; // s.no. column
		columnWidths[1] = 2.0f; // Name column
		columnWidths[2] = 1.2f; // Extension column
		columnWidths[3] = 3.7f; // Content URL column
		columnWidths[4] = 1.3f; // Document Type column
		columnWidths[5] = 1.2f; // Download Status column
		columnWidths[6] = 3.7f; // Error Message column

		table.setWidths(columnWidths);
		table.setWidthPercentage(90f);

		com.itextpdf.text.Phrase slNoHeader = new com.itextpdf.text.Phrase("s.no", headerFont);
		table.addCell(slNoHeader);
		for (int i = 0; i < reportTemplateRecordTableView.getColumns().size(); i++) {
			com.itextpdf.text.Phrase header = new com.itextpdf.text.Phrase(
					reportTemplateRecordTableView.getColumns().get(i).getText(), headerFont);
			table.addCell(header);
		}
		int slNo = 1;
		for (RecordTemplateProperties record : records) {
			table.addCell(new com.itextpdf.text.Phrase(String.valueOf(slNo++), dataFont));
			for (int i = 0; i < reportTemplateRecordTableView.getColumns().size(); i++) {
				Object value = reportTemplateRecordTableView.getColumns().get(i).getCellData(record);
				table.addCell(new com.itextpdf.text.Phrase(value != null ? value.toString() : "", dataFont));
			}
		}
		document.add(table);
		document.close();
		logger.info("PDF Report generated at: " + outputFilePath);
		ModernAlertUtil.infoBox("PDF Report generated at: " + outputFilePath, "PDF Report downloaded successfully",
				"Download Status");
	}

	private void generateTemplateExcelReport(ObservableList<RecordTemplateProperties> records, String outputFilePath)
			throws Exception {
		org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
		org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Report");

		org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
		org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
		boldFont.setBold(true);
		headerStyle.setFont(boldFont);
		headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

		org.apache.poi.ss.usermodel.CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		cellStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		cellStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		cellStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

		org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);

		org.apache.poi.ss.usermodel.Cell serialNoHeaderCell = headerRow.createCell(0);
		serialNoHeaderCell.setCellValue("s.no");
		serialNoHeaderCell.setCellStyle(headerStyle);

		for (int i = 0; i < reportTemplateRecordTableView.getColumns().size(); i++) {
			org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i + 1);
			cell.setCellValue(reportTemplateRecordTableView.getColumns().get(i).getText());
			cell.setCellStyle(headerStyle);
		}
		int rowNum = 1;
		for (RecordTemplateProperties record : records) {
			org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
			org.apache.poi.ss.usermodel.Cell serialNoCell = row.createCell(0);
			serialNoCell.setCellValue(rowNum - 1);
			serialNoCell.setCellStyle(cellStyle);
			for (int i = 0; i < reportTemplateRecordTableView.getColumns().size(); i++) {
				Object value = reportTemplateRecordTableView.getColumns().get(i).getCellData(record);
				org.apache.poi.ss.usermodel.Cell cell = row.createCell(i + 1);
				cell.setCellValue(value != null ? value.toString() : "");
				cell.setCellStyle(cellStyle);
			}
		}
		for (int i = 0; i < reportTemplateRecordTableView.getColumns().size() + 1; i++) {
			sheet.autoSizeColumn(i);
		}
		try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(outputFilePath)) {
			workbook.write(fileOut);
		}
		workbook.close();
		logger.info("Excel Report generated at: " + outputFilePath);
		ModernAlertUtil.infoBox("Excel Report generated at: " + outputFilePath, "Excel Report downloaded successfully",
				"Download Status");
	}

	// ------------- Envelope Download PieChart

	private void createEnvelopeDownloadStatusPieChart(ObservableList<RecordEnvelopeProperties> recordPropertiesList) {
		int trueCount = 0;
		int falseCount = 0;
		int totalCount = 0;
		for (RecordEnvelopeProperties record : recordPropertiesList) {
			if (record.getDownloadStatus()) {
				trueCount++;
			} else {
				falseCount++;
			}
			totalCount++;
		}
		PieChart.Data trueStatusSlice = new PieChart.Data("Pass ", trueCount);
		PieChart.Data falseStatusSlice = new PieChart.Data("Fail ", falseCount);
		reportsEnvelopePieChart.getData().clear();
		reportsEnvelopePieChart.getData().addAll(trueStatusSlice, falseStatusSlice);
		reportsEnvelopePieChart.setLegendVisible(false);
		trueStatusSlice.getNode().setStyle("-fx-pie-color: green;");
		falseStatusSlice.getNode().setStyle("-fx-pie-color: red;");
		Label totalCountLabel = new Label("Total : " + totalCount);
		Label trueCountLabel = new Label("Pass : " + trueCount);
		Label falseCountLabel = new Label("Fail : " + falseCount);
		trueCountLabel.setTextFill(javafx.scene.paint.Color.GREEN);
		falseCountLabel.setTextFill(javafx.scene.paint.Color.RED);
		totalCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		trueCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		falseCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		double truePercentage = (double) trueCount / totalCount * 100;
		double falsePercentage = (double) falseCount / totalCount * 100;
		Tooltip trueTooltip = new Tooltip(String.format("Pass: %.2f%%", truePercentage));
		Tooltip falseTooltip = new Tooltip(String.format("Fail: %.2f%%", falsePercentage));
		Tooltip.install(trueStatusSlice.getNode(), trueTooltip);
		Tooltip.install(falseStatusSlice.getNode(), falseTooltip);
		vboxEnvelopeStatusCount.getChildren().addAll(totalCountLabel, trueCountLabel, falseCountLabel);
		trueStatusSlice.getNode().setOnMouseClicked(event -> {
			filterEnvelopeTableView(recordPropertiesList, true);
		});

		falseStatusSlice.getNode().setOnMouseClicked(event -> {
			filterEnvelopeTableView(recordPropertiesList, false);
		});
	}

	private void filterEnvelopeTableView(ObservableList<RecordEnvelopeProperties> recordPropertiesList,
			boolean filterValue) {

		ObservableList<RecordEnvelopeProperties> filteredItems = FXCollections.observableArrayList();

		for (RecordEnvelopeProperties record : recordPropertiesList) {
			if (record.getDownloadStatus() == filterValue) {
				filteredItems.add(record);
			}
		}
		reportEnvelopeRecordTableView.setItems(filteredItems);
	}

	@FXML
	public void downloadEnvelopeReportsAction(ActionEvent event) {
		logger.info("Clicked on Envelope Download Button");
		String selectedFormatType = formatChooserEnvelopeComboBox.getSelectionModel().getSelectedItem();
		if (selectedFormatType == null || selectedFormatType.isEmpty()) {
			logger.warn("No format selected for download.");
			ModernAlertUtil.showErrorAlert("No format selected", "Please select a format to download the report");
			return;
		}
		logger.info("Selected Report Download Format: " + selectedFormatType);

		ObservableList<RecordEnvelopeProperties> records = reportEnvelopeRecordTableView.getItems();
		if (records.isEmpty()) {
			logger.warn("No data available for download.");
			return;
		}
		String reportFolderPath = PropertiesHolder.getEnvelopeReportFolderPath();
//		String reportFolderPath = "C:\\Users\\Sudharsan\\Downloads\\Docusign test\\import reports";
		String fileNamePrefix = "DocOrbit-Envelope-Report-";
		String timestamp = new java.text.SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new java.util.Date());
		String filePath = reportFolderPath + Constants.SEPARATOR_BACKWARD_SLASH + fileNamePrefix + "-"
				+ selectedFormatType + "-" + timestamp;

		try {
			if (selectedFormatType.equals(Constants.REPORTS_DOWNLOAD_FORMAT_PDF)) {
				generateEnvelopePDFReport(records, filePath + ".pdf");
			} else if (selectedFormatType.equals(Constants.REPORTS_DOWNLOAD_FORMAT_EXCEL)) {
				generateEnvelopeExcelReport(records, filePath + ".xlsx");
			} else {
				logger.warn("Unsupported format selected.");
				ModernAlertUtil.showErrorAlert("Unsupported format selected", "Please select a supported format");
			}
		} catch (Exception e) {
			logger.error("Error generating report: ", e);
			ModernAlertUtil.showErrorAlert("Error generating report", "Error generating report");
		}
	}

	private void generateEnvelopePDFReport(ObservableList<RecordEnvelopeProperties> records, String outputFilePath)
			throws Exception {
		com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4.rotate());
		com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(outputFilePath));

		document.open();
		document.add(new com.itextpdf.text.Paragraph("Report, Date: " + new java.util.Date()));
		document.add(new com.itextpdf.text.Paragraph("\n"));
		int recordCount = records.size();
		document.add(new com.itextpdf.text.Paragraph("Records Count: " + recordCount));
		document.add(new com.itextpdf.text.Paragraph("\n"));

		com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10); // Font
		com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12,
				com.itextpdf.text.Font.BOLD);

		com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(
				reportEnvelopeRecordTableView.getColumns().size() + 1);
		float[] columnWidths = new float[reportEnvelopeRecordTableView.getColumns().size() + 1];
		columnWidths[0] = 0.6f; // s.no. column
		columnWidths[1] = 2.0f; // Name column
		columnWidths[2] = 1.2f; // Extension column
		columnWidths[3] = 3.7f; // Content URL column
		columnWidths[4] = 1.3f; // Document Type column
		columnWidths[5] = 1.2f; // Download Status column
		columnWidths[6] = 3.7f; // Error Message column

		table.setWidths(columnWidths);
		table.setWidthPercentage(90f);

		com.itextpdf.text.Phrase slNoHeader = new com.itextpdf.text.Phrase("s.no", headerFont);
		table.addCell(slNoHeader);
		for (int i = 0; i < reportEnvelopeRecordTableView.getColumns().size(); i++) {
			com.itextpdf.text.Phrase header = new com.itextpdf.text.Phrase(
					reportEnvelopeRecordTableView.getColumns().get(i).getText(), headerFont);
			table.addCell(header);
		}
		int slNo = 1;
		for (RecordEnvelopeProperties record : records) {
			table.addCell(new com.itextpdf.text.Phrase(String.valueOf(slNo++), dataFont));
			for (int i = 0; i < reportEnvelopeRecordTableView.getColumns().size(); i++) {
				Object value = reportEnvelopeRecordTableView.getColumns().get(i).getCellData(record);
				table.addCell(new com.itextpdf.text.Phrase(value != null ? value.toString() : "", dataFont));
			}
		}
		document.add(table);
		document.close();
		logger.info("PDF Report generated at: " + outputFilePath);
		ModernAlertUtil.infoBox("PDF Report generated at: " + outputFilePath, "PDF Report downloaded successfully",
				"Download Status");
	}

	private void generateEnvelopeExcelReport(ObservableList<RecordEnvelopeProperties> records, String outputFilePath)
			throws Exception {
		org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
		org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Report");

		org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
		org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
		boldFont.setBold(true);
		headerStyle.setFont(boldFont);
		headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

		org.apache.poi.ss.usermodel.CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		cellStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		cellStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
		cellStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

		org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);

		org.apache.poi.ss.usermodel.Cell serialNoHeaderCell = headerRow.createCell(0);
		serialNoHeaderCell.setCellValue("s.no");
		serialNoHeaderCell.setCellStyle(headerStyle);

		for (int i = 0; i < reportEnvelopeRecordTableView.getColumns().size(); i++) {
			org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i + 1);
			cell.setCellValue(reportEnvelopeRecordTableView.getColumns().get(i).getText());
			cell.setCellStyle(headerStyle);
		}
		int rowNum = 1;
		for (RecordEnvelopeProperties record : records) {
			org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
			org.apache.poi.ss.usermodel.Cell serialNoCell = row.createCell(0);
			serialNoCell.setCellValue(rowNum - 1);
			serialNoCell.setCellStyle(cellStyle);
			for (int i = 0; i < reportEnvelopeRecordTableView.getColumns().size(); i++) {
				Object value = reportEnvelopeRecordTableView.getColumns().get(i).getCellData(record);
				org.apache.poi.ss.usermodel.Cell cell = row.createCell(i + 1);
				cell.setCellValue(value != null ? value.toString() : "");
				cell.setCellStyle(cellStyle);
			}
		}
		for (int i = 0; i < reportEnvelopeRecordTableView.getColumns().size() + 1; i++) {
			sheet.autoSizeColumn(i);
		}
		try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(outputFilePath)) {
			workbook.write(fileOut);
		}
		workbook.close();
		logger.info("Excel Report generated at: " + outputFilePath);
		ModernAlertUtil.infoBox("Excel Report generated at: " + outputFilePath, "Excel Report downloaded successfully",
				"Download Status");
	}

	public void refreshEnvelopeTableView() {
		reportEnvelopeRecordTableView.getItems().clear();
		reportEnvelopeRecordTableView.getColumns().clear();
		reportEnvelopeRecordTableView.getSelectionModel().clearSelection();
		reportEnvelopeRecordTableView.getSortOrder().clear();
		reportsEnvelopePieChart.getData().clear();
		vboxEnvelopeStatusCount.getChildren().clear();
		formatChooserEnvelopeComboBox.getItems().clear();
		formatChooserEnvelopeComboBox.getSelectionModel().clearSelection();
		String reportsEnvelopeXMLfilePath = PropertiesHolder.getEnvelopeReportFolderPath()
				+ "\\DocOrbit-docusign-import-envelope-report-status.xml";

		if (reportsEnvelopeXMLfilePath != null && !reportsEnvelopeXMLfilePath.isEmpty()) {
			XMLEnvelopeTableView tableView = new XMLEnvelopeTableView();
			envelopexmlFile = new File(reportsEnvelopeXMLfilePath);
			if (envelopexmlFile.exists()) {
				TableView<RecordEnvelopeProperties> recordView = tableView.readEnvelopeXMLData(envelopexmlFile);
				reportEnvelopeRecordTableView.getColumns().clear();
				reportEnvelopeRecordTableView.getColumns().addAll(recordView.getColumns());
				reportEnvelopeRecordTableView.setItems(recordView.getItems());
				reportEnvelopeRecordTableView.setVisible(true);
				Platform.runLater(() -> {
					createEnvelopeDownloadStatusPieChart(recordView.getItems());
				});

			} else {
				logger.info("refresh envelope XML file not found or invalid.");
			}
		} else {
			logger.info("Invalid XML file path.");
		}

		String downloadForamts[] = { Constants.REPORTS_DOWNLOAD_FORMAT_PDF, Constants.REPORTS_DOWNLOAD_FORMAT_EXCEL };

		formatChooserEnvelopeComboBox.getItems().addAll(downloadForamts);
		formatChooserEnvelopeComboBox.setOnAction(event -> {
			Object selectedItem = formatChooserEnvelopeComboBox.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				String selectedFormatType = selectedItem.toString();
				logger.info("Selected Report Download Format: " + selectedFormatType);
				PropertiesHolder.setSelectedReportDownloadFormat(selectedFormatType);
			} else {
				logger.warn(":warning: No format selected in formatChooserComboBox");
			}
		});

		refreshEnvelopeTableImageView.setOnMouseClicked(event -> {
			refreshEnvelopeTableView();
		});
	}

	public void refreshTemplateTableView() {
		reportTemplateRecordTableView.getItems().clear();
		reportTemplateRecordTableView.getColumns().clear();
		reportTemplateRecordTableView.getSelectionModel().clearSelection();
		reportTemplateRecordTableView.getSortOrder().clear();
		reportsTemplatePieChart.getData().clear();
		vboxTemplateStatusCount.getChildren().clear();
		formatChooserTemplateComboBox.getItems().clear();
		formatChooserTemplateComboBox.getSelectionModel().clearSelection();
		String reportsTemplateXMLfilePath = PropertiesHolder.getTemplateReportFolderPath()
				+ "\\DocOrbit-docusign-import-template-report-status.xml";
		System.out.println("reportsTemplateXMLfilePath---" + reportsTemplateXMLfilePath);
		if (reportsTemplateXMLfilePath != null && !reportsTemplateXMLfilePath.isEmpty()) {
			XMLTemplateTableView tableView = new XMLTemplateTableView();
			xmlFile = new File(reportsTemplateXMLfilePath);
			if (xmlFile.exists()) {
				TableView<RecordTemplateProperties> recordView = tableView.readTemplateXMLData(xmlFile);
				reportTemplateRecordTableView.getColumns().clear();
				reportTemplateRecordTableView.getColumns().addAll(recordView.getColumns());
				reportTemplateRecordTableView.setItems(recordView.getItems());
				reportTemplateRecordTableView.setVisible(true);
				Platform.runLater(() -> {
					createTemplateDownloadStatusPieChart(recordView.getItems());
				});

			} else {
				logger.info("refresh teplate XML file not found or invalid.");
			}
		} else {
			logger.info("Invalid XML file path.");
		}

		String downloadForamts[] = { Constants.REPORTS_DOWNLOAD_FORMAT_PDF, Constants.REPORTS_DOWNLOAD_FORMAT_EXCEL };

		formatChooserTemplateComboBox.getItems().addAll(downloadForamts);
		formatChooserTemplateComboBox.setOnAction(event -> {
			Object selectedItem = formatChooserTemplateComboBox.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				String selectedFormatType = selectedItem.toString();
				logger.info("Selected Report Download Format: " + selectedFormatType);
				PropertiesHolder.setSelectedReportDownloadFormat(selectedFormatType);
			} else {
				logger.warn(":warning: No format selected in formatChooserComboBox");
			}
		});

		refreshTemplateTableImageView.setOnMouseClicked(event -> {
			refreshTemplateTableView();
		});
	}

	public boolean isEnvelopeInitialized() {
		return envelopeFlag;
	}

	public boolean isTemplateInitialized() {
		return templateFlag;
	}
}
