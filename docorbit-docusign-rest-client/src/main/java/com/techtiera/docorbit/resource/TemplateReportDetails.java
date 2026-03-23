package com.techtiera.docorbit.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateReportDetails {

	private String templateName;
	private String extension;
	private String location;
	private boolean status;
	private String errorMessage;

}