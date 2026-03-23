package com.techtiera.docorbit.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.stereotype.Component;

@Component
public class SourceReader implements ItemStreamReader<Record> {

	public static final Logger logger = LoggerFactory.getLogger(SourceReader.class);

	private final StaxEventItemReader<Record> delegate;

	public SourceReader(StaxEventItemReader<Record> delegate) {
		this.delegate = delegate;
	}

	@Override
	public Record read() throws Exception {
		return delegate.read();
	}

	@Override
	public void open(ExecutionContext executionContext) {
		((ItemStream) delegate).open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) {
		((ItemStream) delegate).update(executionContext);
	}

	@Override
	public void close() {
		((ItemStream) delegate).close();
	}

}
