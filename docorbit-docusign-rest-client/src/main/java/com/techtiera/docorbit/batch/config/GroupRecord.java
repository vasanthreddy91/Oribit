package com.techtiera.docorbit.batch.config;

import java.util.List;

import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupRecord {
	private String groupId;
	private String groupName;
	private String permissionProfileId;
	private String groupType;
	private String usersCount;
	private String lastModifiedOn;
	private String isManagedByScim;

	@XmlElementWrapper(name = "users")
	@XmlElement(name = "user")
	private List<UserRecord> users;
}