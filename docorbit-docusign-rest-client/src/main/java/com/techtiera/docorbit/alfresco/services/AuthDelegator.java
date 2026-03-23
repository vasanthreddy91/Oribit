package com.techtiera.docorbit.alfresco.services;

import org.alfresco.rest.sdk.feign.DelegatedAuthenticationProvider;
import org.alfresco.rest.sdk.feign.config.EnableAuthConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.techtiera.docorbit.config.UserSession;

import feign.RequestTemplate;

@Component
@Configuration
@EnableAuthConfiguration
public class AuthDelegator implements DelegatedAuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(AuthDelegator.class);

	@Override
	public void setAuthentication(RequestTemplate template) {
		logger.info("############  inside setAuthentication()");
		String access_token = "TICKET_fc41d3fa5edff70ba3314a5fb2438db9e22d74fb";

		HttpHeaders headers = new HttpHeaders();
		// headers.add("Authorization", "Basic YWRtaW46QWxmcmVzY28wMQ==");

		logger.info("Ticket : " + UserSession.getInstace().getTicket());

		String token = UserSession.getInstace().getTicket();
		logger.info("token" + ":" + token + ":");
		template.header(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46QWxmcmVzY28wMQ==");
		logger.info("template : " + template.resolved());

	}

}
