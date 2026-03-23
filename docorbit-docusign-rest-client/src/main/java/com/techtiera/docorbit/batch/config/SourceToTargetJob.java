package com.techtiera.docorbit.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import com.techtiera.docorbit.alfresco.services.CreateFolderWrapper;
import com.techtiera.docorbit.alfresco.services.FileCustomTypeWrapper;
import com.techtiera.docorbit.alfresco.services.GetNodeMetadataWrapper;
import com.techtiera.docorbit.resource.Property;
import com.techtiera.docorbit.resource.Type;

@Configuration
public class SourceToTargetJob {

	public static final Logger logger = LoggerFactory.getLogger(SourceToTargetJob.class);

	private final JobRepository jobRepository;

	public FileCustomTypeWrapper fileCustomTypeWrapper;

	public GetNodeMetadataWrapper getNodeMetadataWrapper;

	public CreateFolderWrapper createFolderWrapper;

	public TargetWriter alfrescoWriter;

	@Value("${batch.chunk.size}")
	private int chunkSize;

	@Value("${batch.threadPool.coreSize}")
	private int threadPoolCoreSize;

	@Value("${batch.threadPool.maxSize}")
	private int threadPoolMaxSize;

	@Value("${batch.threadPool.queueSize}")
	private int threadPoolQueueSize;

	public StepExecution stepExecution;

	public SourceToTargetJob(JobRepository jobRepository, FileCustomTypeWrapper fileCustomTypeWrapper,
			GetNodeMetadataWrapper getNodeMetadataWrapper, CreateFolderWrapper createFolderWrapper,
			TargetWriter alfrescoWriter) {
		this.jobRepository = jobRepository;
		this.fileCustomTypeWrapper = fileCustomTypeWrapper;
		this.getNodeMetadataWrapper = getNodeMetadataWrapper;
		this.createFolderWrapper = createFolderWrapper;
		this.alfrescoWriter = alfrescoWriter;
	}

	@Bean
	public Job multithreadedJob(Step readTransXmlStep, Step readPropertiesXmlStep) throws Exception {
		var name = "Multithreaded JOB";
		var builder = new JobBuilder(name, jobRepository);
		return builder.start(readTransXmlStep).next(readPropertiesXmlStep).build();
	}

	// User Job
	@Bean
	public Job userMultithreadedJob(Step readUserXmlStep) throws Exception {
		var name = "User Multithreaded JOB";
		var builder = new JobBuilder(name, jobRepository);
		return builder.start(readUserXmlStep).build();
	}

	@Bean
	public Step readUserXmlStep(SynchronizedItemStreamReader<UserRecord> reader,
			SynchronizedItemStreamWriter<UserRecord> writer, PlatformTransactionManager txManager) throws Exception {
		logger.info("readUserXmlStep start..");
		var name = "Multithreaded : Read -> Process -> Write ";
		var builder = new StepBuilder(name, jobRepository);
		return builder.<UserRecord, UserRecord>chunk(chunkSize, txManager).reader(reader).faultTolerant().writer(writer)
				.listener(writer).taskExecutor(taskExecutor()).build();
	}

	@Bean
	public SynchronizedItemStreamReader<UserRecord> synchronizedItemStreamUserReader(
			StaxEventItemReader<UserRecord> userXmlFileReader) {
		SourceUserReader sourceReader = new SourceUserReader(userXmlFileReader);
		SynchronizedItemStreamReader<UserRecord> synchronizedReader = new SynchronizedItemStreamReader<>();
		synchronizedReader.setDelegate(sourceReader);
		return synchronizedReader;
	}

	@Bean
	public SynchronizedItemStreamWriter<UserRecord> synchronizedItemStreamUserWriter(UserTargetWriter delegate) {
		SynchronizedItemStreamWriter<UserRecord> writer = new SynchronizedItemStreamWriter<>();
		writer.setDelegate(delegate);
		return writer;
	}

	@Bean
	@StepScope
	public StaxEventItemReader<UserRecord> userXmlFileReader(
			@Value("#{jobParameters['fullPathFileName']}") String pathToFile) {
		StaxEventItemReader<UserRecord> reader = new StaxEventItemReader<>();
		reader.setResource(new FileSystemResource(pathToFile));
		reader.setFragmentRootElementName("user");
		Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
		unmarshaller.setClassesToBeBound(UserRecord.class);
		reader.setUnmarshaller(unmarshaller);
		return reader;
	}

	@Bean
	public Step readTransXmlStep(TransformationItemReader transformationXmlReader, TransformationXmlItemWriter writer,
			PlatformTransactionManager txManager) throws Exception {
		logger.info("readTransXmlStep start..");
		var name = "Multithreaded : readTransXmlStep ";
		var builder = new StepBuilder(name, jobRepository);
		return builder.<Type, Type>chunk(chunkSize, txManager).reader(transformationXmlReader)
				.processor(readTransXmlProcessor()).writer(writer).taskExecutor(taskExecutor()).build();
	}

	@Bean
	public Step readPropertiesXmlStep(SynchronizedItemStreamReader<Record> reader,
			SynchronizedItemStreamWriter<Record> writer, PlatformTransactionManager txManager) throws Exception {
		logger.info("readPropertiesXmlStep start..");
		var name = "Multithreaded : Read -> Process -> Write ";
		var builder = new StepBuilder(name, jobRepository);
		return builder.<Record, Record>chunk(chunkSize, txManager).reader(reader).faultTolerant()
				.processor(multithreadedchProcessor()).writer(writer).listener(writer).taskExecutor(taskExecutor())
				.build();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(chunkSize); // Number of concurrent threads
		return taskExecutor;
	}

	@Bean
	public ItemProcessor<Record, Record> multithreadedchProcessor() {
		return (record) -> {
			logger.info("Thread Name : " + Thread.currentThread().getName());
			logger.info("multithreadedchProcessor :: start");
			logger.info(record.getName());
			record.getProperties().forEach(property -> {
				logger.info("Name : " + property.getName());
				logger.info("Value : " + property.getValue());
			});
			return record;
		};
	}

	@Bean
	@StepScope
	public StaxEventItemReader<Record> xmlFileReader(@Value("#{jobParameters['fullPathFileName']}") String pathToFile) {
		StaxEventItemReader<Record> reader = new StaxEventItemReader<>();
		reader.setResource(new FileSystemResource(pathToFile));
		reader.setFragmentRootElementName("record");
		Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
		unmarshaller.setClassesToBeBound(Record.class);
		reader.setUnmarshaller(unmarshaller);
		return reader;
	}

	@Bean
	public SynchronizedItemStreamReader<Record> synchronizedItemStreamReader(
			StaxEventItemReader<Record> xmlFileReader) {
		SourceReader sourceReader = new SourceReader(xmlFileReader);
		SynchronizedItemStreamReader<Record> synchronizedReader = new SynchronizedItemStreamReader<>();
		synchronizedReader.setDelegate(sourceReader);
		return synchronizedReader;
	}

	@Bean
	public SynchronizedItemStreamWriter<Record> synchronizedItemStreamWriter(TargetWriter delegate) {
		SynchronizedItemStreamWriter<Record> writer = new SynchronizedItemStreamWriter<>();
		writer.setDelegate(delegate);
		return writer;
	}

	@Bean
	@StepScope
	public TransformationItemReader transformationItemReader(
			@Value("#{jobParameters['transformationXml']}") String transXmlPath) {
		return new TransformationItemReader(transXmlPath);
	}

	@Bean
	@StepScope
	public StaxEventItemReader<Type> transformationXmlReader(
			@Value("#{jobParameters['transformationXml']}") String transXmlPath) {
		StaxEventItemReader<Type> transXmlReader = new StaxEventItemReader<>();

		transXmlReader.setResource(new FileSystemResource(transXmlPath));

		// The fragment root element should be "type" to directly read <type> elements
		transXmlReader.setFragmentRootElementName("type");

		Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
		unmarshaller.setClassesToBeBound(Type.class, Property.class);

		transXmlReader.setUnmarshaller(unmarshaller);

		return transXmlReader;
	}

	@Bean
	public ItemProcessor<Type, Type> readTransXmlProcessor() {
		return new ItemProcessor<Type, Type>() {
			@Override
			public Type process(Type item) throws Exception {
				return item;
			}
		};
	}
	
//	======Template Job =========
	
	@Bean
	public Job templateMultithreadedJob(Step readTemplateXmlStep) throws Exception {
		var name = "Template Multithreaded JOB";
		var builder = new JobBuilder(name, jobRepository);
		return builder.start(readTemplateXmlStep).build();
	}
	
	@Bean
	public Step readTemplateXmlStep(SynchronizedItemStreamReader<TemplateWrapper> reader,
			SynchronizedItemStreamWriter<TemplateWrapper> writer, PlatformTransactionManager txManager) throws Exception {
		logger.info("readTemplateXmlStep start..");
		var name = "Multithreaded : Read -> Process -> Write ";
		var builder = new StepBuilder(name, jobRepository);
		return builder.<TemplateWrapper, TemplateWrapper>chunk(chunkSize, txManager).reader(reader).faultTolerant().writer(writer)
				.listener(writer).taskExecutor(taskExecutor()).build();
	}
	
	@Bean
	public SynchronizedItemStreamReader<TemplateWrapper> synchronizedItemStreamTemplateReader(
			StaxEventItemReader<TemplateWrapper> templateXmlFileReader) {
		SourceTemplateReader sourceReader = new SourceTemplateReader(templateXmlFileReader);
		SynchronizedItemStreamReader<TemplateWrapper> synchronizedReader = new SynchronizedItemStreamReader<>();
		synchronizedReader.setDelegate(sourceReader);
		return synchronizedReader;
	}

	@Bean
	public SynchronizedItemStreamWriter<TemplateWrapper> synchronizedItemStreamTemplateWriter(TemplateTargetWriter delegate) {
		SynchronizedItemStreamWriter<TemplateWrapper> writer = new SynchronizedItemStreamWriter<>();
		writer.setDelegate(delegate);
		return writer;
	}
	
	@Bean
	@StepScope
	public StaxEventItemReader<TemplateWrapper> templateXmlFileReader(
			@Value("#{jobParameters['fullPathFileName']}") String pathToFile) {
		StaxEventItemReader<TemplateWrapper> reader = new StaxEventItemReader<>();
		reader.setResource(new FileSystemResource(pathToFile));
		reader.setFragmentRootElementName("template");
		Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
		unmarshaller.setClassesToBeBound(TemplateWrapper.class);
		reader.setUnmarshaller(unmarshaller);
		return reader;
	}
	
	
//	========== Envelop Job =================
	
	@Bean
	public Job envelopMultithreadedJob(Step readEnvelopXmlStep) throws Exception {
		var name = "Envelop Multithreaded JOB";
		var builder = new JobBuilder(name, jobRepository);
		return builder.start(readEnvelopXmlStep).build();
	}
	
	@Bean
	public Step readEnvelopXmlStep(SynchronizedItemStreamReader<EnvelopeListWrapper> reader,
			SynchronizedItemStreamWriter<EnvelopeListWrapper> writer, PlatformTransactionManager txManager) throws Exception {
		logger.info("readEnvelopXmlStep start..");
		var name = "Multithreaded : Read -> Process -> Write ";
		var builder = new StepBuilder(name, jobRepository);
		return builder.<EnvelopeListWrapper, EnvelopeListWrapper>chunk(chunkSize, txManager).reader(reader).faultTolerant().writer(writer)
				.listener(writer).taskExecutor(taskExecutor()).build();
	}
	
	@Bean
	public SynchronizedItemStreamReader<EnvelopeListWrapper> synchronizedItemStreamEnvelopReader(
			StaxEventItemReader<EnvelopeListWrapper> envelopXmlFileReader) {
		SourceEnvelopReader sourceReader = new SourceEnvelopReader(envelopXmlFileReader);
		SynchronizedItemStreamReader<EnvelopeListWrapper> synchronizedReader = new SynchronizedItemStreamReader<>();
		synchronizedReader.setDelegate(sourceReader);
		return synchronizedReader;
	}

	@Bean
	public SynchronizedItemStreamWriter<EnvelopeListWrapper> synchronizedItemStreamEnvelopWriter(EnvelopTargetWriter delegate) {
		SynchronizedItemStreamWriter<EnvelopeListWrapper> writer = new SynchronizedItemStreamWriter<>();
		writer.setDelegate(delegate);
		return writer;
	}
	
	@Bean
	@StepScope
	public StaxEventItemReader<EnvelopeListWrapper> envelopXmlFileReader(
			@Value("#{jobParameters['fullPathFileName']}") String pathToFile) {
		StaxEventItemReader<EnvelopeListWrapper> reader = new StaxEventItemReader<>();
		reader.setResource(new FileSystemResource(pathToFile));
		reader.setFragmentRootElementName("envelop");
		Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
		unmarshaller.setClassesToBeBound(EnvelopeListWrapper.class);
		reader.setUnmarshaller(unmarshaller);
		return reader;
	}
	
	// Group Job
		@Bean
		public Job groupMultithreadedJob(Step readGroupXmlStep) throws Exception {
			var name = "Group Multithreaded JOB";
			var builder = new JobBuilder(name, jobRepository);
			return builder.start(readGroupXmlStep).build();
		}

		@Bean
		public Step readGroupXmlStep(SynchronizedItemStreamReader<GroupRecord> reader,
				SynchronizedItemStreamWriter<GroupRecord> writer, PlatformTransactionManager txManager) throws Exception {
			logger.info("readGroupXmlStep start..");
			var name = "Multithreaded : Read -> Process -> Write ";
			var builder = new StepBuilder(name, jobRepository);
			return builder.<GroupRecord, GroupRecord>chunk(chunkSize, txManager).reader(reader).faultTolerant()
					.writer(writer).listener(writer).taskExecutor(taskExecutor()).build();
		}

		@Bean
		public SynchronizedItemStreamReader<GroupRecord> synchronizedItemStreamGroupReader(
				StaxEventItemReader<GroupRecord> groupXmlFileReader) {
			SourceGroupReader sourceReader = new SourceGroupReader(groupXmlFileReader);
			SynchronizedItemStreamReader<GroupRecord> synchronizedReader = new SynchronizedItemStreamReader<>();
			synchronizedReader.setDelegate(sourceReader);
			return synchronizedReader;
		}

		@Bean
		public SynchronizedItemStreamWriter<GroupRecord> synchronizedItemStreamGroupWriter(GroupTargetWriter delegate) {
			SynchronizedItemStreamWriter<GroupRecord> writer = new SynchronizedItemStreamWriter<>();
			writer.setDelegate(delegate);
			return writer;
		}

		@Bean
		@StepScope
		public StaxEventItemReader<GroupRecord> groupXmlFileReader(
				@Value("#{jobParameters['fullPathFileName']}") String pathToFile) {
			StaxEventItemReader<GroupRecord> reader = new StaxEventItemReader<>();
			reader.setResource(new FileSystemResource(pathToFile));
			reader.setFragmentRootElementName("group");
			Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
			unmarshaller.setClassesToBeBound(GroupRecord.class);
			reader.setUnmarshaller(unmarshaller);
			return reader;
		}
		
		
	

}