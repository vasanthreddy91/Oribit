package com.techtiera.docorbit.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public final class CSVTableView extends TableView<String> {

	public static final Logger logger = LoggerFactory.getLogger(CSVTableView.class);

	public CSVTableView(String delimiter, File file) throws IOException {

		// Get CSV file lines as List
		List<String> lines = Files.readAllLines(Paths.get(file.toURI()));

		lines.remove(0);

		// Get the header row
		String[] firstRow = lines.get(0).split(delimiter);

		// For each header/column, create TableColumn
		for (String columnName : firstRow) {
			TableColumn<String, String> column = new TableColumn<>(columnName);
			this.getColumns().add(column);

			column.setCellValueFactory(cellDataFeatures -> {
				String values = cellDataFeatures.getValue();
				String[] cells = values.split(delimiter);
				int columnIndex = cellDataFeatures.getTableView().getColumns()
						.indexOf(cellDataFeatures.getTableColumn());
				if (columnIndex >= cells.length) {
					return new SimpleStringProperty("");
				} else {
					return new SimpleStringProperty(cells[columnIndex]);
				}
			});

			this.setItems(FXCollections.observableArrayList(lines));
			this.getItems().remove(0);
		}
		// printTableViewToConsole();
	}

	private void printTableViewToConsole() {
		logger.info("Printing TableView to console:");
		for (String item : getItems()) {
			String[] cells = item.split(",");
			for (String cell : cells) {
				logger.info(cell + "\t");
			}
		}
	}
}