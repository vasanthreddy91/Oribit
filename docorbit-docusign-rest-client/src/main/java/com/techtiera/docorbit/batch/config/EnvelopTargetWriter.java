package com.techtiera.docorbit.batch.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techtiera.docorbit.alfresco.services.AlfescoRestServiceUtility;
import com.techtiera.docorbit.alfresco.services.CreateFolderWrapper;
import com.techtiera.docorbit.alfresco.services.FileCustomTypeWrapper;
import com.techtiera.docorbit.alfresco.services.GetNodeMetadataWrapper;
import com.techtiera.docorbit.alfresco.services.UploadNewFileVersionWrapper;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.resource.EnvelopReportDetails;
import com.techtiera.docorbit.resource.Source;
import com.techtiera.docorbit.util.CommonUtility;
import com.techtiera.docorbit.util.RecordReport;

@Component
public class EnvelopTargetWriter implements ItemStreamWriter<EnvelopeListWrapper> {

	public static final Logger logger = LoggerFactory.getLogger(EnvelopTargetWriter.class);

	public FileCustomTypeWrapper fileCustomTypeWrapper;

	public GetNodeMetadataWrapper getNodeMetadataWrapper;

	public CreateFolderWrapper createFolderWrapper;

	public UploadNewFileVersionWrapper uploadNewFileVersionWrapper;

	public StepExecution stepExecution;

	public String sourceType;

	public String targetType;

	public long recordsExecutionCount;

	public long envelopRecordsExecutionCount;

	private List<RecordReport> reportList;

	private Source source;

	@Autowired
	private AlfescoRestServiceUtility alfescoRestServiceUtility;

	public EnvelopTargetWriter(FileCustomTypeWrapper fileCustomTypeWrapper,
			GetNodeMetadataWrapper getNodeMetadataWrapper, CreateFolderWrapper createFolderWrapper,
			UploadNewFileVersionWrapper uploadNewFileVersionWrapper) {
		this.fileCustomTypeWrapper = fileCustomTypeWrapper;
		this.getNodeMetadataWrapper = getNodeMetadataWrapper;
		this.createFolderWrapper = createFolderWrapper;
		this.uploadNewFileVersionWrapper = uploadNewFileVersionWrapper;
		this.reportList = new ArrayList<>();
	}

	@Override
	public void open(ExecutionContext executionContext) {
		logger.info("TemplateTargetWriter :: open");

		source = new Source();
		source.setName("DocuSign");
		source.setVersion(PropertiesHolder.getDocusignversion());
	}

	@Override
	public void write(Chunk<? extends EnvelopeListWrapper> items) throws Exception {

		if (items.isEmpty()) {
			logger.info("EnvelopTargetWriter :: No users to process");
			return;
		}

		logger.info("{} - EnvelopTargetWriter :: Processing {} users", Thread.currentThread().getName(), items.size());

		List<EnvelopReportDetails> reports = alfescoRestServiceUtility.uploadAllEnvelops(items.getItems());
		for (EnvelopReportDetails report : reports) {
			source.getEnvelops().add(report);

			if (report.isStatus()) {
				envelopRecordsExecutionCount++;
				logger.info("{} - envelop '{}' created successfully", Thread.currentThread().getName(),
						report.getEnvelopName());
			} else {
				System.out.println("envelop failed to upload");
			}
		}
		PropertiesHolder.setEnvelopRecordsExecutionCount(envelopRecordsExecutionCount);
	}

	// --------------------------------------------------
	// CLOSE – called once after all chunks
	// --------------------------------------------------
	@Override
	public void close() {
		logger.info("EnvelopeTargetWriter :: close");

		try {
			Path xmlPath = Paths.get(PropertiesHolder.getEnvelopeReportFolderPath(),
					"DocOrbit-docusign-import-envelope-report-status.xml");

			CommonUtility.envelopexmlwrite(source, xmlPath);
//			PropertiesHolder.setTemplateRecordsExecutionCount(envelopRecordsExecutionCount);

			logger.info("Envelope XML written successfully with {} records", source.getEnvelops().size());

		} catch (Exception e) {
			logger.error("Failed to write template XML", e);
		}
	}

}
