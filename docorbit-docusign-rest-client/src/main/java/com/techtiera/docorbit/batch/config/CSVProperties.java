package com.techtiera.docorbit.batch.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CSVProperties {

	private String name;
	private String extension;
	private String dateAccessed;
	private String dateModified;
	private String dateCreated;
	private String documentType;
	private String disciplineCode;
	private String typeCode;
	private String folderPath;
	private String docPath;
	private String column1;

}