package com.techtiera.docorbit.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

@Component
public class AlfrescoItemWriteListener implements ItemWriteListener<CSVProperties> {

	public static final Logger logger = LoggerFactory.getLogger(AlfrescoItemWriteListener.class);

	@Override
	public void beforeWrite(Chunk<? extends CSVProperties> items) {
		logger.info("Writing started CSVProperties list : " + items);
	}

	@Override
	public void afterWrite(Chunk<? extends CSVProperties> items) {
		logger.info("Writing completed CSVProperties list : " + items.size());
	}

	@Override
	public void onWriteError(Exception e, Chunk<? extends CSVProperties> items) {
		logger.error("Error in reading the CSVProperties records " + items);
		logger.error("Error in reading the CSVProperties records " + e);
	}
}