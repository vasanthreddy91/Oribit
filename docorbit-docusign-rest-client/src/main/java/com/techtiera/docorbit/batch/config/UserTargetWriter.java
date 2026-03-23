package com.techtiera.docorbit.batch.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techtiera.docorbit.alfresco.services.AlfescoRestServiceUtility;
import com.techtiera.docorbit.alfresco.services.CreateFolderWrapper;
import com.techtiera.docorbit.alfresco.services.FileCustomTypeWrapper;
import com.techtiera.docorbit.alfresco.services.GetNodeMetadataWrapper;
import com.techtiera.docorbit.alfresco.services.UploadNewFileVersionWrapper;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.resource.UserReportDetails;
import com.techtiera.docorbit.util.CommonUtility;
import com.techtiera.docorbit.util.RecordReport;

@Component
public class UserTargetWriter implements ItemStreamWriter<UserRecord> {

	public static final Logger logger = LoggerFactory.getLogger(UserTargetWriter.class);

	public FileCustomTypeWrapper fileCustomTypeWrapper;

	public GetNodeMetadataWrapper getNodeMetadataWrapper;

	public CreateFolderWrapper createFolderWrapper;

	public UploadNewFileVersionWrapper uploadNewFileVersionWrapper;

	public StepExecution stepExecution;

	public String sourceType;

	public String targetType;

	public long recordsExecutionCount;

	public long userRecordsExecutionCount;

	private List<RecordReport> reportList;

	@Autowired
	private AlfescoRestServiceUtility alfescoRestServiceUtility;

	public UserTargetWriter(FileCustomTypeWrapper fileCustomTypeWrapper, GetNodeMetadataWrapper getNodeMetadataWrapper,
			CreateFolderWrapper createFolderWrapper, UploadNewFileVersionWrapper uploadNewFileVersionWrapper) {
		this.fileCustomTypeWrapper = fileCustomTypeWrapper;
		this.getNodeMetadataWrapper = getNodeMetadataWrapper;
		this.createFolderWrapper = createFolderWrapper;
		this.uploadNewFileVersionWrapper = uploadNewFileVersionWrapper;
		this.reportList = new ArrayList<>();
	}

	@Override
	public void write(Chunk<? extends UserRecord> items) throws Exception {

		if (items.isEmpty()) {
			logger.info("UserTargetWriter :: No users to process");
			return;
		}

		logger.info("{} - UserTargetWriter :: Processing {} users", Thread.currentThread().getName(), items.size());

		List<UserReportDetails> reports = alfescoRestServiceUtility.uploadAllUser(items.getItems());
		for (UserReportDetails report : reports) {

			if (report.isStatus()) {
				userRecordsExecutionCount++;
				logger.info("{} - User '{}' created successfully", Thread.currentThread().getName(),
						report.getUserName());
			} else {
//				CommonUtility.logsDisplay(CommonUtility.formatDateTime() + " - User '" + report.getUserName()
//						+ "' failed: " + report.getErrorMessage(), PropertiesHolder.getLogTextArea());
			}
		}
		PropertiesHolder.setUserRecordsExecutionCount(userRecordsExecutionCount);
	}

}
