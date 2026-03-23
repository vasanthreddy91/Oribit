package com.techtiera.docorbit.batch.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserRecord {
	private String userName;
	private String userId;
	private String userType;
	private String isAdmin;
	private String userStatus;
	private String uri;
	private String email;
	private String title;
	private String createdDateTime;
	private String userAddedToAccountDateTime;
	private String firstName;
	private String middleName;
	private String lastName;
	private String suffixName;
	private String jobTitle;
	private String company;
	private String permissionProfileId;
	private String permissionProfileName;
	private String isManagedByScim;
	private String isMembershipManagedByScim;

	@JsonProperty("groupList")
	private List<UserGroupRecord> groups;

	private UserSettings userSettings;

}