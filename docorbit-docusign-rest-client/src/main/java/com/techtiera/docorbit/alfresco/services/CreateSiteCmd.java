package com.techtiera.docorbit.alfresco.services;

import java.io.IOException;
import java.util.Objects;

import org.alfresco.core.handler.SitesApi;
import org.alfresco.core.model.Site;
import org.alfresco.core.model.SiteBodyCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateSiteCmd {
	static final Logger LOGGER = LoggerFactory.getLogger(CreateSiteCmd.class);

	@Autowired
	SitesApi sitesApi;

	public void execute(String siteId, String title, String description) throws IOException {
		Site site = Objects.requireNonNull(sitesApi.createSite(new SiteBodyCreate().id(siteId).title("title-" + title)
				.description("description-" + description).visibility(SiteBodyCreate.VisibilityEnum.PUBLIC), null, null,
				null).getBody()).getEntry();
		LOGGER.info("Created site: {}", site);
	}
}