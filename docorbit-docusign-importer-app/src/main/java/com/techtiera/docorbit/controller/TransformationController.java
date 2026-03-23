package com.techtiera.docorbit.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.techtiera.docorbit.alfresco.constants.Constants;
import com.techtiera.docorbit.alfresco.services.AlfescoRestServiceUtility;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.jfxsupport.FXMLController;
import com.techtiera.docorbit.jfxsupport.ModernAlertUtil;
import com.techtiera.docorbit.util.AppUtil;
import com.techtiera.docorbit.util.CommonUtility;
import com.techtiera.docorbit.util.Property;
import com.techtiera.docorbit.util.TransformationProperties;
import com.techtiera.docorbit.util.XMLDOMParser;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

@Component
@FXMLController
public class TransformationController {

	public static final Logger logger = LoggerFactory.getLogger(TransformationController.class);

	@FXML
	private SplitPane sourceAttrSplitPane;

	@FXML
	private SplitPane targetAttrSplitPane;

	@FXML
	private ComboBox documentTypeTarget;

	@FXML
	private ComboBox documentTypeSource;

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private VBox containerVBox;

	@FXML
	private HBox hBoxScrollPane;

	@FXML
	private ScrollPane scrollPaneSizing;

	@Autowired
	private AlfescoRestServiceUtility alfescoRestServiceUtility;

	List<String> attributeValues = new ArrayList<>();

	List<String> propertyValues = new ArrayList<>();

	private int valueCount;

	private BooleanProperty attrPropPairSelected = new SimpleBooleanProperty(false);

	@Autowired
	private ApplicationContext applicationContext;

	@SuppressWarnings("unchecked")
	@FXML
	private void initialize() {
		if (PropertiesHolder.getCsvFilePath() != null) {
			System.out.println("PropertiesHolder.getCsvFilePath() : " + PropertiesHolder.getCsvFilePath());
			List<String> sourceDocumentTypes = XMLDOMParser.getSourceDocumentTypes(PropertiesHolder.getCsvFilePath());
			documentTypeSource.setItems(FXCollections.observableArrayList(sourceDocumentTypes));
			documentTypeSource.valueProperty().addListener((observable, oldValue, newValue) -> {
				attributeValues = XMLDOMParser.getSourceAttributes(PropertiesHolder.getCsvFilePath(),
						newValue.toString());
				updateComboBoxes(0, attributeValues);
			});
			List<String> targetDocumentTypes = alfescoRestServiceUtility.getAlfrescoTypes();
			documentTypeTarget.setItems(FXCollections.observableArrayList(targetDocumentTypes));
			documentTypeTarget.valueProperty().addListener((observable, oldValue, newValue) -> {
				propertyValues = alfescoRestServiceUtility.getAlfrescoTypeAttributes(newValue.toString());
				updateComboBoxes(1, propertyValues);
			});
			PropertiesHolder.setNewTansformationTabStatus(true);
		}
		hBoxScrollPane.prefWidthProperty().bind(scrollPaneSizing.widthProperty());
		hBoxScrollPane.prefHeightProperty().bind(scrollPaneSizing.heightProperty());
	}

	@SuppressWarnings("unchecked")
	@FXML
	public void addButtonAction(ActionEvent event) {
		if (documentTypeSource.getValue() != null && documentTypeTarget.getValue() != null) {
			int numAttributeValues = attributeValues.size();
			int numPropertyValues = propertyValues.size();
			int currentChildrenCount = containerVBox.getChildren().size();
			if (currentChildrenCount < numAttributeValues && currentChildrenCount < numPropertyValues) {
				HBox newPane = new HBox();
				String colorCode = "#f6f6f8";
				BackgroundFill backgroundFill = new BackgroundFill(Color.web(colorCode), new CornerRadii(10.0),
						Insets.EMPTY);
				Background background = new Background(backgroundFill);
				newPane.setBackground(background);
				newPane.setPrefWidth(600);
				newPane.setPadding(new Insets(10));
				newPane.setAlignment(Pos.CENTER_LEFT);

				InnerShadow innerShadow = new InnerShadow();
				innerShadow.setRadius(3);
				innerShadow.setColor(Color.BLACK);
				innerShadow.setBlurType(BlurType.THREE_PASS_BOX);
				newPane.setEffect(innerShadow);

				ComboBox<String> newAttrComboBox = new ComboBox<>();
				ComboBox<String> newPropComboBox = new ComboBox<>();
				newAttrComboBox.setItems(FXCollections.observableArrayList(attributeValues));
				newPropComboBox.setItems(FXCollections.observableArrayList(propertyValues));

				newAttrComboBox.setPrefHeight(25);
				newPropComboBox.setPrefHeight(25);
				newAttrComboBox.setMinWidth(220);
				newPropComboBox.setMinWidth(220);

				Region spacer = new Region();
				HBox.setHgrow(spacer, Priority.ALWAYS);
				Region spacer1 = new Region();
				HBox.setHgrow(spacer1, Priority.ALWAYS);

				Button dltButton = new Button("Delete");
				dltButton.setMinWidth(60);
				dltButton.setPrefHeight(20.0);
				dltButton.setStyle("-fx-text-fill: #ffffff");
				String dltBtnCC = "#0D78BE";
				BackgroundFill dltBackgroundFill = new BackgroundFill(Color.web(dltBtnCC), new CornerRadii(5.0),
						Insets.EMPTY);
				Background dltBackground = new Background(dltBackgroundFill);
				dltButton.setBackground(dltBackground);

				dltButton.setOnAction(dltEvent -> {
					HBox parentHBox = (HBox) dltButton.getParent();
					containerVBox.getChildren().remove(parentHBox);
					System.out.println("containerVBox.getChildren() count : " + containerVBox.getChildren().size());
					if (containerVBox.getChildren().size() == 0) {
						attrPropPairSelected.set(false);
						notifyMainController();
					}
				});

				newPane.getChildren().addAll(newAttrComboBox, spacer1, newPropComboBox, spacer, dltButton);
				containerVBox.getChildren().add(newPane);

				double vBoxSpacing = 15.0;
				containerVBox.setSpacing(vBoxSpacing);
			} else {
				logger.info("Cannot add more elements. The limit has been reached.");
			}

			if (containerVBox.getChildren() != null) {
				System.out.println("Attribute-Property Pair is selected");
				attrPropPairSelected.set(true);
				notifyMainController();
			}

		} else {
			ModernAlertUtil.infoBox(Constants.TRANS_ADD_ATTR_MESSAGE, Constants.TRANS_ADD_ATTR_HEADER,
					Constants.TRANS_ADD_ATTR_TITLE);
		}
	}

	@FXML
	public void mapDataButtonAction(ActionEvent event) {
		Set<String> uniquePairs = new HashSet<>();
		Set<String> usedAttributes = new HashSet<>();
		Set<String> usedProperties = new HashSet<>();
		List<Property> propertyList = new ArrayList<>();
		boolean hasDuplicate = false;
		boolean hasUnused = false;

		for (Node node : containerVBox.getChildren()) {
			if (node instanceof HBox) {
				List<String> list = new ArrayList<>();
				HBox hBox = (HBox) node;
				for (Node childNode : hBox.getChildren()) {
					if (childNode instanceof ComboBox) {
						ComboBox<?> comboBox = (ComboBox<?>) childNode;
						String selectedValue = comboBox.getValue() != null ? comboBox.getValue().toString() : null;
						list.add(selectedValue);
					}
				}
				if (list.size() >= 2) {
					String source = list.get(0);
					String target = list.get(1);
					String pair = source + "=>" + target;
					if (uniquePairs.contains(pair)) {
						hasDuplicate = true;
						break;
					}
					uniquePairs.add(pair);
					if (usedAttributes.contains(source)) {
						hasUnused = true;
						break;
					}
					usedAttributes.add(source);
					if (usedProperties.contains(target)) {
						hasUnused = true;
						break;
					}
					usedProperties.add(target);
					Property property = new Property();
					property.setSource(source);
					property.setTarget(target);
					propertyList.add(property);
				}
			}
		}
		if (hasDuplicate) {
			ModernAlertUtil.infoBox(Constants.TRANS_DUPLICATE_VALUES_MESSAGE, Constants.TRANS_DUPLICATE_VALUES_HEADER,
					Constants.TRANS_DUPLICATE_VALUES_TITTLE);
			return;
		}
		if (hasUnused) {
			ModernAlertUtil.infoBox(Constants.TRANS_USED_VALUES_MESSAGE, Constants.TRANS_USED_VALUES_HEADER,
					Constants.TRANS_USED_VALUES_TITTLE);
			return;
		}
		List<TransformationProperties> typesList = new ArrayList<TransformationProperties>();
		TransformationProperties transformationProperties = new TransformationProperties();
		transformationProperties.setSourceType(documentTypeSource.getValue().toString());
		transformationProperties.setTargetType(documentTypeTarget.getValue().toString());
		transformationProperties.setProperties(propertyList);
		typesList.add(transformationProperties);

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.newDocument();

			Element rootTransformation = document.createElement(Constants.XML_TRANS_TAG);
			document.appendChild(rootTransformation);

			Element types = document.createElement(Constants.XML_TRANS_TYPES_TAG);
			rootTransformation.appendChild(types);

			Element type = document.createElement(Constants.XML_TRANS_TYPE_TAG);
			types.appendChild(type);

			typesList.forEach(temp -> {
				CommonUtility.createElementTag(document, type, Constants.XML_TRANS_SOURCE_TYPE_TAG,
						temp.getSourceType());
				CommonUtility.createElementTag(document, type, Constants.XML_TRANS_TARGET_TYPE_TAG,
						temp.getTargetType());

				Element properties = document.createElement(Constants.XML_PROPERTIES_TAG);
				type.appendChild(properties);

				temp.getProperties().forEach(property -> {
					Element propertyTag = document.createElement(Constants.XML_PROPERTY_TAG);
					properties.appendChild(propertyTag);
					CommonUtility.createElementTag(document, propertyTag, Constants.XML_TRANS_SOURCE_TAG,
							property.getSource());
					CommonUtility.createElementTag(document, propertyTag, Constants.XML_TRANS_TARGET_TAG,
							property.getTarget());
				});
			});
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			DOMSource source = new DOMSource(document);

			File xml = new File(PropertiesHolder.getContentFolderPath() + Constants.SEPARATOR_FORWARD_SLASH
					+ Constants.XML_TRANS_FILE_NAME);
			if (xml.exists()) {
				xml.delete();
			}
			boolean status = xml.createNewFile();
			logger.info("Transformation File Created : " + status);
			StreamResult result = new StreamResult(xml);
			transformer.transform(source, result);
			if (status) {
				AppUtil.infoBox(Constants.TRANSFORMATION_SUCCESS_MESSAGE, Constants.TRANSFORMATION_SUCCESS_HEADER,
						Constants.TRANSFORMATION_SUCCESS_TITLE);
			} else {
				AppUtil.showErrorAlert(Constants.TRANSFORMATION_FAILURE_TITLE,
						Constants.TRANSFORMATION_FAILURE_MESSAGE);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private void updateComboBoxes(int index, List<String> values) {
		containerVBox.getChildren().forEach(node -> {
			if (node instanceof HBox) {
				HBox hBox = (HBox) node;
				if (hBox.getChildren().size() > 1 && hBox.getChildren().get(1) instanceof ComboBox) {
					ComboBox<String> comboBox = (ComboBox<String>) hBox.getChildren().get(index);
					comboBox.getItems().clear();
					comboBox.setItems(FXCollections.observableArrayList(values));
					valueCount = comboBox.getItems().size();
				}
			}
		});
		if (containerVBox.getChildren().size() > valueCount) {
			int extraCount = containerVBox.getChildren().size() - valueCount;
			for (int i = 0; i < extraCount; i++) {
				containerVBox.getChildren().remove(containerVBox.getChildren().size() - 1);
			}
		}
	}

	private void notifyMainController() {
		if (attrPropPairSelected.get()) {
			MainController mainControllerEnable = applicationContext.getBean(MainController.class);
//			mainControllerEnable.enableExportButton(true);
		} else {
			MainController mainControllerDisable = applicationContext.getBean(MainController.class);
//			mainControllerDisable.enableExportButton(false);
		}
	}
}
