package com.techtiera.docorbit.util;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformationProperties {
	
	private String sourceType;
	private String targetType;
	private List<Property> properties;
	
}
