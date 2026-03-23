package com.techtiera.docorbit.batch.config;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.techtiera.docorbit.resource.Type;

@Component
public class TransformationXmlItemWriter implements ItemWriter<Type> {

	@Override
	public void write(Chunk<? extends Type> items) throws Exception {

	}
}
