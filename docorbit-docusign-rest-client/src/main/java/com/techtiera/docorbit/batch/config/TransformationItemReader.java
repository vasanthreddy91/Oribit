package com.techtiera.docorbit.batch.config;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.resource.Transformation;
import com.techtiera.docorbit.resource.Type;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

@Component
public class TransformationItemReader implements ItemReader<Type> {
	
	public static final Logger logger = LoggerFactory.getLogger(TransformationItemReader.class);

	private List<com.techtiera.docorbit.resource.Property> properties;
	private Type currentType;
	private final String filePath;
	private boolean isInitialized = false;

	public TransformationItemReader(String filePath) {
		this.filePath = filePath;
		initializeReader();
	}

	private void initializeReader() {
		if (!isInitialized) {
			try {
				File file = new File(filePath);
				JAXBContext jaxbContext = JAXBContext.newInstance(Transformation.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Transformation transformation = (Transformation) jaxbUnmarshaller.unmarshal(file);

				currentType = transformation.getTypes().getType();
				List<com.techtiera.docorbit.resource.Property> properties = currentType.getProperties();
				this.properties = properties;
				isInitialized = true;
				PropertiesHolder.setTransXmlData(currentType);
			} catch (Exception e) {
				throw new RuntimeException("Error initializing the ItemReader", e);
			}
		}
	}

	@Override
	public Type read() throws Exception {
		initializeReader();
		if (properties.size() > 0 && !isInitialized) {
			for (com.techtiera.docorbit.resource.Property property : properties) {
				logger.info("Source: " + property.getSource() + ", Target: " + property.getTarget());
			}
			return currentType;
		} else {
			return null;
		}
	}
}
