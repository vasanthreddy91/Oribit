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
import org.w3c.dom.Document;

import com.techtiera.docorbit.alfresco.services.AlfescoRestServiceUtility;
import com.techtiera.docorbit.alfresco.services.CreateFolderWrapper;
import com.techtiera.docorbit.alfresco.services.FileCustomTypeWrapper;
import com.techtiera.docorbit.alfresco.services.GetNodeMetadataWrapper;
import com.techtiera.docorbit.alfresco.services.UploadNewFileVersionWrapper;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.resource.Source;
import com.techtiera.docorbit.resource.TemplateReportDetails;
import com.techtiera.docorbit.util.CommonUtility;

@Component
public class TemplateTargetWriter implements ItemStreamWriter<TemplateWrapper> {

	public static final Logger logger = LoggerFactory.getLogger(TemplateTargetWriter.class);

	public FileCustomTypeWrapper fileCustomTypeWrapper;

	public GetNodeMetadataWrapper getNodeMetadataWrapper;

	public CreateFolderWrapper createFolderWrapper;

	public UploadNewFileVersionWrapper uploadNewFileVersionWrapper;

	public StepExecution stepExecution;

	public String sourceType;

	public String targetType;

	public long recordsExecutionCount;

	public long templateRecordsExecutionCount;

	List<TemplateReportDetails> reportList;

	private Document reportDocument;

	private Source source;

	@Autowired
	private AlfescoRestServiceUtility alfescoRestServiceUtility;

	public TemplateTargetWriter(FileCustomTypeWrapper fileCustomTypeWrapper,
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
	public void write(Chunk<? extends TemplateWrapper> items) throws Exception {

		if (items.isEmpty()) {
			return;
		}

		logger.info("{} - Processing {} templates", Thread.currentThread().getName(), items.size());

		List<TemplateReportDetails> reports = alfescoRestServiceUtility.uploadAllTemplates(items.getItems());

		for (TemplateReportDetails report : reports) {
			source.getTemplates().add(report);

			reportList.add(report);
			if (report.isStatus()) {
				templateRecordsExecutionCount++;
				logger.info("Template '{}' uploaded successfully", report.getTemplateName());
			} else {
				logger.error("Template '{}' failed: {}", report.getTemplateName(), report.getErrorMessage());
			}
		}
	}

	// --------------------------------------------------
	// CLOSE – called once after all chunks
	// --------------------------------------------------
	@Override
	public void close() {
		logger.info("TemplateTargetWriter :: close");

		try {
			Path xmlPath = Paths.get(PropertiesHolder.getTemplateReportFolderPath(),
					"DocOrbit-docusign-import-template-report-status.xml");

			CommonUtility.templatexmlwrite(source, xmlPath);
			PropertiesHolder.setTemplateRecordsExecutionCount(templateRecordsExecutionCount);

			logger.info("Template XML written successfully with {} records", source.getTemplates().size());

		} catch (Exception e) {
			logger.error("Failed to write template XML", e);
		}
	}

	@Override
	public void update(ExecutionContext executionContext) {
		// Optional – only needed for restartability
	}

}
