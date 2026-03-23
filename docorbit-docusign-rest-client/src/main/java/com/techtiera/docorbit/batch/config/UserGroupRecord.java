package com.techtiera.docorbit.batch.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupRecord {
	private String groupId;
	private String groupName;
	private String userType;
	private String lastModifiedOn;
	private String isManagedByScim;
}
