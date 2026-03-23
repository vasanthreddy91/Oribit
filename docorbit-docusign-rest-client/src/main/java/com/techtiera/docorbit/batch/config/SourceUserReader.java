package com.techtiera.docorbit.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.stereotype.Component;

@Component
public class SourceUserReader implements ItemStreamReader<UserRecord> {

	public static final Logger logger = LoggerFactory.getLogger(SourceUserReader.class);

	private final StaxEventItemReader<UserRecord> delegate;

	public SourceUserReader(StaxEventItemReader<UserRecord> delegate) {
		this.delegate = delegate;
	}

	@Override
	public UserRecord read() throws Exception {
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
