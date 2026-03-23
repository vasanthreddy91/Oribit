package com.techtiera.docorbit.batch.config;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "users")
public class UserRecords {

	private List<UserRecord> users;

	@XmlElement(name = "user")
	public List<UserRecord> getUsers() {
		return users;
	}

	public void setRecords(List<UserRecord> users) {
		this.users = users;
	}
}
