package com.techtiera.docorbit.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.stereotype.Component;

@Component
public class SourceGroupReader implements ItemStreamReader<GroupRecord> {

	public static final Logger logger = LoggerFactory.getLogger(SourceGroupReader.class);

	private final StaxEventItemReader<GroupRecord> delegate;

	public SourceGroupReader(StaxEventItemReader<GroupRecord> delegate) {
		this.delegate = delegate;
	}

	@Override
	public GroupRecord read() throws Exception {
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
