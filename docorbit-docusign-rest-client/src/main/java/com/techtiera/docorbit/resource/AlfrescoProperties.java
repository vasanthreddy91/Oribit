package com.techtiera.docorbit.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AlfrescoProperties {

	private String nodeId;

	private String name;

	private String parentId;
	
	private String title;

}
