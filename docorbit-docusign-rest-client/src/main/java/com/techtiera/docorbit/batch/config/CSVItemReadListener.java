package com.techtiera.docorbit.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;

public class CSVItemReadListener implements ItemReadListener<CSVProperties> {
	public static final Logger logger = LoggerFactory.getLogger(CSVItemReadListener.class);

	@Override
	public void beforeRead() {
		logger.info("CSVItemReadListener :: beforeRead()");
	}

	@Override
	public void afterRead(CSVProperties input) {
		logger.info("CSVItemReadListener :: afterRead()");
	}

	@Override
	public void onReadError(Exception e) {
		logger.error("CSVItemReadListener :: onReadError() :: Error in reading the CSVProperties record : " + e);
	}

}