package com.techtiera.docorbit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UserSession {

	public static final Logger logger = LoggerFactory.getLogger(UserSession.class);

	private static UserSession instance;
	private static String userName;
	private static String ticket;
	private static Boolean isUserLogout = false;

	private UserSession(String userName, String ticket) {
		UserSession.userName = userName;
		UserSession.ticket = ticket;
	}

	public static UserSession getInstace() {
		return instance;
	}

	public static UserSession setInstace(String userName, String ticket) {
		if (instance == null) {
			instance = new UserSession(userName, ticket);
		}
		return instance;
	}

	public String getUserName() {
		return userName;
	}

	public String getTicket() {
		return ticket;
	}

	public String getSelectedNodeId() {
		return ticket;
	}

	public static void cleanUserSession() {
		userName = null;
		ticket = null;
		logger.info("removed the session objects");
	}

	@Override
	public String toString() {
		return "UserSession{" + "userName='" + userName + '\'' + ", ticket=" + ticket + '}';
	}

	public static Boolean getIsUserLogout() {
		return isUserLogout;
	}

	public static void setIsUserLogout(Boolean isUserLogout) {
		UserSession.isUserLogout = isUserLogout;
	}
	
	
}