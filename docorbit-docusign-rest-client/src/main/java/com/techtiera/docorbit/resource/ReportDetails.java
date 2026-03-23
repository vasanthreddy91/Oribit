package com.techtiera.docorbit.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDetails {
	
	private boolean status;
	private String errorMessage;
	
}