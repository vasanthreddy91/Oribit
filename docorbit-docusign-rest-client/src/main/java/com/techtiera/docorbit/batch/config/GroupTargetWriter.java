package com.techtiera.docorbit.batch.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.techtiera.docorbit.resource.GroupReportDetails;
import com.techtiera.docorbit.util.RecordReport;

@Component
public class GroupTargetWriter implements ItemStreamWriter<GroupRecord> {

	public static final Logger logger = LoggerFactory.getLogger(GroupTargetWriter.class);

	public FileCustomTypeWrapper fileCustomTypeWrapper;

	public GetNodeMetadataWrapper getNodeMetadataWrapper;

	public CreateFolderWrapper createFolderWrapper;

	public UploadNewFileVersionWrapper uploadNewFileVersionWrapper;

	public StepExecution stepExecution;

	public String sourceType;

	public String targetType;

	public long recordsExecutionCount;

//	public long userRecordsExecutionCount;

	public long groupRecordsExecutionCount;

	private List<RecordReport> reportList;

	@Autowired
	private AlfescoRestServiceUtility alfescoRestServiceUtility;

	public GroupTargetWriter(FileCustomTypeWrapper fileCustomTypeWrapper, GetNodeMetadataWrapper getNodeMetadataWrapper,
			CreateFolderWrapper createFolderWrapper, UploadNewFileVersionWrapper uploadNewFileVersionWrapper) {
		this.fileCustomTypeWrapper = fileCustomTypeWrapper;
		this.getNodeMetadataWrapper = getNodeMetadataWrapper;
		this.createFolderWrapper = createFolderWrapper;
		this.uploadNewFileVersionWrapper = uploadNewFileVersionWrapper;
		this.reportList = new ArrayList<>();
	}

	@Override
	public void write(Chunk<? extends GroupRecord> items) throws Exception {

		if (items.isEmpty()) {
			logger.info("UserTargetWriter :: No users to process");
			return;
		}

		logger.info("{} - UserTargetWriter :: Processing {} users", Thread.currentThread().getName(), items.size());

		if (PropertiesHolder.getUserDetails() == null) {
			Map<String, String> userDetails = alfescoRestServiceUtility.getAllUserDetails();
			PropertiesHolder.setUserDetails(userDetails);
		}

		List<GroupReportDetails> reports = alfescoRestServiceUtility.uploadAllGroups(items.getItems());
		for (GroupReportDetails report : reports) {

			if (report.isStatus()) {
				groupRecordsExecutionCount++;
				logger.info("{} - User '{}' created successfully", Thread.currentThread().getName(),
						report.getGroupName());
			} else {
//				CommonUtility.logsDisplay(CommonUtility.formatDateTime() + " - User '" + report.getUserName()
//						+ "' failed: " + report.getErrorMessage(), PropertiesHolder.getLogTextArea());
			}
		}
		PropertiesHolder.setGroupRecordsExecutionCount(groupRecordsExecutionCount);
	}

}
