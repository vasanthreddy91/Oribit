package com.techtiera.docorbit.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordReport {

	private String name;
	private String extension;
	private String contentUrl;
	private String documentType;
	private boolean status;
	private String errorMessage;
}