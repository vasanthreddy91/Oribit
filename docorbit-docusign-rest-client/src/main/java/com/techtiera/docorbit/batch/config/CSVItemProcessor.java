package com.techtiera.docorbit.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class CSVItemProcessor implements ItemProcessor<CSVProperties, CSVProperties> {

	public static final Logger logger = LoggerFactory.getLogger(CSVItemProcessor.class);

	@Override
	public CSVProperties process(CSVProperties csvProperties) throws Exception {
		return csvProperties;
	}
}