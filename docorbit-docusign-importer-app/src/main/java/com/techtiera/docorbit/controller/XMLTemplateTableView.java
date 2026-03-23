package com.techtiera.docorbit.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techtiera.docorbit.util.CommonUtility;
import com.techtiera.docorbit.util.RecordProperties;
import com.techtiera.docorbit.util.RecordTemplateProperties;
import com.techtiera.docorbit.util.XMLDOMParser;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class XMLTemplateTableView {

	public static final Logger logger = LoggerFactory.getLogger(XMLTemplateTableView.class);

	public TableView<RecordTemplateProperties> tableView = new TableView<>();

	public TableView<RecordTemplateProperties> readTemplateXMLData(File file) {
		List<RecordTemplateProperties> recordProperties = XMLDOMParser.readTemplateRecordProperties(file.getAbsolutePath());
		tableView.getColumns().clear();
		if (!recordProperties.isEmpty()) {
			RecordTemplateProperties firstRecord = recordProperties.get(0);
			for (Map.Entry<String, String> entry : firstRecord.getProperties().entrySet()) {
				String columnKey = entry.getKey();
				String headerText = CommonUtility.formatColumnName(columnKey);
				TableColumn<RecordTemplateProperties, String> column = new TableColumn<>(headerText);
				column.setCellValueFactory(cellDataFeatures -> {
					RecordTemplateProperties currentRecord = cellDataFeatures.getValue();
					return new SimpleStringProperty(currentRecord.getProperties().getOrDefault(columnKey, ""));
				});
				if (tableView.getColumns().stream().noneMatch(c -> c.getText().equals(columnKey))) {
					tableView.getColumns().add(column);
				}
			}
		}
		tableView.setItems(FXCollections.observableArrayList(recordProperties));
		return tableView;
	}

}