package com.techtiera.docorbit.alfresco.services;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.boot.model.internal.PropertyHolder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Prop;
import com.techtiera.docorbit.alfresco.constants.Constants;
import com.techtiera.docorbit.batch.config.EnvelopeListWrapper;
import com.techtiera.docorbit.batch.config.GroupRecord;
import com.techtiera.docorbit.batch.config.TemplateWrapper;
import com.techtiera.docorbit.batch.config.UserRecord;
import com.techtiera.docorbit.config.PropertiesHolder;
import com.techtiera.docorbit.resource.EnvelopReportDetails;
import com.techtiera.docorbit.resource.GroupReportDetails;
import com.techtiera.docorbit.resource.Source;
import com.techtiera.docorbit.resource.TemplateReportDetails;
import com.techtiera.docorbit.resource.UserReportDetails;
import com.techtiera.docorbit.resource.VersionDetails;
import com.techtiera.docorbit.util.Base64FileUtil;
import com.techtiera.docorbit.util.CommonUtility;
import com.techtiera.docorbit.util.FileExtensionUtil;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AlfescoRestServiceUtility {

	private static final Logger logger = LoggerFactory.getLogger(AlfescoRestServiceUtility.class);

	@Value("${alfresco.baseurl}")
	public String baseUrl;

	@Value("${alfresco.auth.url}")
	public String authUrl;

	@Value("${alfresco.models.url}")
	public String modelsUrl;

	@Value("${alfresco.discovery.url}")
	public String discoveryUrl;

	@Value("${docusign.rsa.key}")
	public String rsaKey;

	@Value("${docusign.integration.key}")
	public String docusignIntegrationKey;

	@Value("${docusign.auth.url}")
	public String docusignAuthUrl;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	public boolean getLoginAuth(String userId) {
		if (userId.isEmpty() || userId.isBlank()) {
			return false;
		}
		try {
			PrivateKey privateKey = loadPrivateKeyFromProperties(rsaKey);
			String jwtToken = generateJwt(privateKey, userId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
			body.add("grant_type", Constants.GRANT_TYPE);
			body.add("assertion", jwtToken);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

			ResponseEntity<String> response = restTemplate.exchange(docusignAuthUrl, HttpMethod.POST, request,
					String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				logger.info("Login success !!!!!");
				JSONObject json = new JSONObject(response.getBody());
				String accessToken = json.optString("access_token");
				if (accessToken == null) {
					return false;
				}
				PropertiesHolder.setAccessToken(accessToken);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public PrivateKey loadPrivateKeyFromProperties(String privateKeyPem) throws Exception {

		String sanitizedKey = privateKeyPem.replaceAll("\\s", "") // removes spaces, newlines, tabs
				.trim();

		byte[] decoded = java.util.Base64.getDecoder().decode(sanitizedKey);

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		return keyFactory.generatePrivate(spec);
	}

	public String generateJwt(PrivateKey privateKey, String userId) {
		long now = Instant.now().getEpochSecond();

		return Jwts.builder().setHeaderParam("typ", "JWT").setIssuer(docusignIntegrationKey).setSubject(userId)
				.setAudience(Constants.AUDIENCE).claim("scope", Constants.SCOPE).claim("iat", now)
				.claim("exp", now + 3600).signWith(privateKey, io.jsonwebtoken.SignatureAlgorithm.RS256).compact();
	}

	public boolean getAlfrescoTicket(String username, String password) {
		try {
			String url = baseUrl + authUrl;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("userId", username);
			jsonObject.put("password", password);

			HttpEntity<?> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
			if (result.getStatusCode().is2xxSuccessful()) {
				String JSON_DATA = result.getBody();
				logger.info("response : " + JSON_DATA);
				String auth = username + ":" + password;
				String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
				String authHeader = "Basic " + encodedAuth;
				PropertiesHolder.setBasicAuth(authHeader);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<String> getAlfrescoTypes() {
		List<String> types = new ArrayList<>();
		try {
			String url = baseUrl + modelsUrl;
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", PropertiesHolder.getBasicAuth());
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			types = CommonUtility.getAlfrescoTypes(response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return types;
	}

	public List<UserReportDetails> uploadAllUser(List<? extends UserRecord> users) {

		List<UserReportDetails> reports = new ArrayList<>();

		if (users == null || users.isEmpty()) {
			return reports;
		}

		try {
			String url = "https://demo.docusign.net/restapi/v2.1/accounts/" + PropertiesHolder.getAccountId()
					+ "/users";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(PropertiesHolder.getAccessToken());

			List<Map<String, Object>> newUsers = users.stream().map(user -> {
				Map<String, Object> map = new HashMap<>();
				map.put("userName", user.getUserName());
				map.put("email", user.getEmail());
				return map;
			}).toList();

			Map<String, Object> requestBody = Map.of("newUsers", newUsers);

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {

				JSONObject responseJson = new JSONObject(response.getBody());
				JSONArray responseUsers = responseJson.getJSONArray("newUsers");

				for (int i = 0; i < responseUsers.length(); i++) {

					JSONObject userJson = responseUsers.getJSONObject(i);

					UserReportDetails report = new UserReportDetails();
					report.setUserName(userJson.optString("userName"));

					JSONObject error = userJson.optJSONObject("errorDetails");

					if (error == null) {
						report.setStatus(true);
					} else {
						report.setStatus(false);
						report.setErrorMessage(error.optString("message"));
					}

					reports.add(report);
				}
			}
		} catch (Exception ex) {
			logger.error("Error while uploading users to DocuSign", ex);
			users.forEach(user -> {
				UserReportDetails report = new UserReportDetails();
				report.setUserName(user.getUserName());
				report.setStatus(false);
				report.setErrorMessage("DocuSign API call failed");
				reports.add(report);
			});
		}
		return reports;
	}

	public List<String> getAlfrescoTypeAttributes(String type) {
		List<String> typeAttributes = new ArrayList<>();
		try {
			type = type.replace(":", "_");
			String url = baseUrl + modelsUrl + Constants.SEPARATOR_FORWARD_SLASH + type;
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", PropertiesHolder.getBasicAuth());
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			typeAttributes = CommonUtility.getAlfrescoTypesAttributes(response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return typeAttributes;
	}

	public VersionDetails getVersionDetails() {
		VersionDetails versionDetails = null;
		try {
			String url = baseUrl + discoveryUrl;
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", PropertiesHolder.getBasicAuth());
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				versionDetails = CommonUtility.parsingDiscoveryDetails(response.getBody());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionDetails;
	}

	public List<TemplateReportDetails> uploadAllTemplates(List<? extends TemplateWrapper> templates) {

		List<TemplateReportDetails> reports = new ArrayList<>();

		if (templates == null || templates.isEmpty()) {
			return reports;
		}

		try {
			String url = "https://demo.docusign.net/restapi/v2.1/accounts/" + PropertiesHolder.getAccountId()
					+ "/templates";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(PropertiesHolder.getAccessToken());

			for (TemplateWrapper template : templates) {
				TemplateReportDetails report = new TemplateReportDetails();

				Map<String, Object> body = new HashMap<>();
				body.put("name", template.getName());
				body.put("emailSubject", template.getEmailSubject());

				/* ---------------- Documents ---------------- */
				List<Map<String, Object>> documentList = new ArrayList<>();

				if (template.getDocuments() != null) {
					for (TemplateWrapper.DocumentRecord doc : template.getDocuments()) {
						System.out.println("document Name--------------------> " + doc.getName());
						System.out.println("document Id--------------------> " + doc.getDocumentId());
						Map<String, Object> documentMap = new HashMap<>();
						documentMap.put("documentId", doc.getDocumentId());
						String docName = doc.getName().endsWith(".pdf") ? doc.getName() : doc.getName() + ".pdf";
						String base64 = Base64FileUtil.encodeToBase64(PropertiesHolder.getTemplateContentFolderPath()
								+ "/" + template.getTemplateId() + "_" + docName);
						documentMap.put("documentBase64", base64);
						String fileName = doc.getName();
						String extension = FileExtensionUtil.fileExtension(fileName);
						documentMap.put("name", doc.getName());
						documentMap.put("fileExtension", extension);

						documentList.add(documentMap);

						report.setTemplateName(doc.getName());
						report.setExtension(getFileExtension(doc.getName()));
					}
				}

				body.put("documents", documentList);

				/* ---------------- Recipients (Signers) ---------------- */
				Map<String, Object> recipientsMap = new HashMap<>();

				if (template.getRecipients() != null && template.getRecipients().getSigners() != null) {

					List<Map<String, Object>> signersList = new ArrayList<>();

					for (TemplateWrapper.SignerRecord signer : template.getRecipients().getSigners()) {
						Map<String, Object> signerMap = new HashMap<>();
						signerMap.put("email", signer.getEmail());
						signerMap.put("name", signer.getName());
						signerMap.put("recipientId", signer.getRecipientId());
						signerMap.put("routingOrder", signer.getRoutingOrder());

						signersList.add(signerMap);
					}

					recipientsMap.put("signers", signersList);
				}

				body.put("recipients", recipientsMap);

				HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

				if (response.getStatusCode().is2xxSuccessful()) {
					report.setLocation(PropertiesHolder.getTemplateContentFolderPath() + "/" + template.getTemplateId()
							+ "_" + template.getName());
					report.setStatus(true);
					CommonUtility.logsDisplay(
							CommonUtility.formatDateTime() + " - " + template.getName() + " is uploaded Successfully.",
							PropertiesHolder.getLogTextArea());
					reports.add(report);
				} else {
					report.setLocation(PropertiesHolder.getTemplateContentFolderPath() + "/" + template.getTemplateId()
							+ "_" + template.getName());
					report.setStatus(false);
					report.setErrorMessage("Failed with status: " + response.getStatusCode());
					CommonUtility.logsDisplay(
							CommonUtility.formatDateTime() + " - " + template.getName()
									+ " is failed to Upload. Please check the report",
							PropertiesHolder.getLogTextArea());
					reports.add(report);
					System.out.println("fail to download ");
				}
				if (PropertiesHolder.isLogProcessIndicatorStatus()) {
					CommonUtility.logProcessLoader(false, PropertiesHolder.getLogProcessLoader());
					PropertiesHolder.setLogProcessIndicatorStatus(false);
				}

			}

		} catch (Exception ex) {
			logger.error("Error while uploading templates to DocuSign", ex);
			templates.forEach(t -> {
				TemplateReportDetails report = new TemplateReportDetails();
				report.setTemplateName(t.getName());
				report.setStatus(false);
				report.setErrorMessage("DocuSign API call failed");
				reports.add(report);
			});
		}

		return reports;
	}

	public String getUserInfo(String accessToken) {
		String accountId = null;
		try {
			String url = "https://account-d.docusign.com/oauth/userinfo";
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + accessToken);
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				accountId = CommonUtility.parsingUserInfo(response.getBody());
				return accountId;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accountId;
	}

	public String getBuildVersion() {
		String buildVersion = null;

		try {
			String url = "https://demo.docusign.net" + "/restapi/service_information";

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

			if (!response.getStatusCode().is2xxSuccessful()) {
				System.err.println("Failed to fetch service information");
				return null;
			}

			JSONObject json = new JSONObject(response.getBody());
			buildVersion = json.optString("buildVersion", null);

			if (buildVersion != null && buildVersion.contains("(")) {
				// Extract only numeric version part
				buildVersion = buildVersion.substring(0, buildVersion.indexOf("(")).trim();
			}

			System.out.println("Build Version: " + buildVersion);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return buildVersion;
	}

	public List<EnvelopReportDetails> uploadAllEnvelops(List<? extends EnvelopeListWrapper> envelops) {

		List<EnvelopReportDetails> reports = new ArrayList<>();

		if (envelops == null || envelops.isEmpty()) {
			return reports;
		}

		try {
			String url = "https://demo.docusign.net/restapi/v2.1/accounts/" + PropertiesHolder.getAccountId()
					+ "/envelopes";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(PropertiesHolder.getAccessToken());

			for (EnvelopeListWrapper envelop : envelops) {

				if (envelop.getStatus().equals("voided")) {
					CommonUtility.logsDisplay(
							CommonUtility.formatDateTime() + " - " + envelop.getName() + " - "
									+ "Upload skipped: the envelope is marked as voided in DocuSign.",
							PropertiesHolder.getLogTextArea());
					EnvelopReportDetails report = new EnvelopReportDetails();
					report.setEnvelopName(envelop.getName());
					report.setStatus(false);
					report.setErrorMessage("Upload skipped: the envelope is marked as voided in DocuSign.");
					reports.add(report);
				}

				else {

					try {
						boolean skipEnvelope = false;
						int docNumber = 1;

						Map<String, Object> body = new HashMap<>();
						body.put("emailSubject", envelop.getName());

						/* ---------------- Documents ---------------- */
						List<Map<String, Object>> documentList = new ArrayList<>();

						if (envelop.getDocuments() != null) {
							for (EnvelopeListWrapper.DocumentRecord doc : envelop.getDocuments()) {

								Map<String, Object> documentMap = new HashMap<>();

								documentMap.put("documentId", docNumber++);
								String docName = doc.getDocumentName();
								String documentName;

								if ("Summary".equalsIgnoreCase(docName) || "testing-docx".equalsIgnoreCase(docName)) {
									documentName = docName + ".pdf";
								} else {
									documentName = docName;
								}

								String filePath = PropertiesHolder.getEnvelopContentFolderPath() + "/"
										+ envelop.getEnvelopeId() + "_" + documentName;

								Path path = Paths.get(filePath);

								// 🚫 Skip envelope if ANY file missing
								if (!Files.exists(path)) {
									logger.warn("File not found [{}], skipping envelope [{}]", filePath,
											envelop.getName());
									skipEnvelope = true;
									break;
								}

								System.out.println("File Path-->" + filePath);
								String base64 = Base64FileUtil.encodeToBase64(filePath);
								documentMap.put("documentBase64", base64);
								documentMap.put("name", documentName);

								String extension = FileExtensionUtil.fileExtension(documentName);
								documentMap.put("fileExtension", extension);

								documentList.add(documentMap);
							}
						}

						// 🚫 Do NOT create envelope if files are missing
						if (skipEnvelope) {
							EnvelopReportDetails report = new EnvelopReportDetails();
							report.setEnvelopName(envelop.getName());
							report.setStatus(false);
							reports.add(report);
							continue;
						}

						body.put("documents", documentList);

						/* ---------------- Recipients ---------------- */
						Map<String, Object> recipientsMap = new HashMap<>();
						List<Map<String, Object>> signersList = new ArrayList<>();

						if (envelop.getSender() != null) {
							Map<String, Object> signerMap = new HashMap<>();
							signerMap.put("email", envelop.getSender().getSenderEmail());
							signerMap.put("name", envelop.getSender().getSenderName());
							signerMap.put("recipientId", "1");
							signersList.add(signerMap);
						}

						recipientsMap.put("signers", signersList);
						body.put("recipients", recipientsMap);

						/* ---------------- Status ---------------- */
						body.put("status", "sent");

						HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

						ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request,
								String.class);

						EnvelopReportDetails report = new EnvelopReportDetails();
						report.setEnvelopName(envelop.getName());
						report.setExtension("pdf");
						report.setLocation(PropertiesHolder.getEnvelopeFilePath() + "/" + envelop.getName());
						report.setStatus(response.getStatusCode().is2xxSuccessful());

						if (response.getStatusCode().is2xxSuccessful()) {
							CommonUtility.logsDisplay(CommonUtility.formatDateTime() + " - " + envelop.getName()
									+ " is uploaded Successfully.", PropertiesHolder.getLogTextArea());
						} else {
							CommonUtility.logsDisplay(
									CommonUtility.formatDateTime() + " - " + envelop.getName()
											+ " is failed to Upload. Please check the report",
									PropertiesHolder.getLogTextArea());
							report.setErrorMessage("Failed with status: " + response.getStatusCode());
						}
						reports.add(report);
					}

					catch (Exception ex) {
						logger.error("Failed to upload envelope {}", envelop.getName(), ex.getMessage());
						CommonUtility.logsDisplay(CommonUtility.formatDateTime() + " - " + envelop.getName()
								+ " is inaccessible in DocuSign.", PropertiesHolder.getLogTextArea());
						EnvelopReportDetails report = new EnvelopReportDetails();
						report.setEnvelopName(envelop.getName());
						report.setStatus(false);
						report.setErrorMessage("Upload skipped: the envelope is inaccessible in DocuSign.");
						reports.add(report);
					}

				}
				if (PropertiesHolder.isLogProcessIndicatorStatus()) {
					CommonUtility.logProcessLoader(false, PropertiesHolder.getLogProcessLoader());
					PropertiesHolder.setLogProcessIndicatorStatus(false);
				}
			}

		} catch (Exception ex) {
			logger.error("Error while uploading envelopes to DocuSign", ex);

			envelops.forEach(e -> {
				EnvelopReportDetails report = new EnvelopReportDetails();
				report.setEnvelopName(e.getName());
				report.setStatus(false);
				reports.add(report);
			});
		}

		return reports;
	}

	public List<GroupReportDetails> uploadAllGroups(List<? extends GroupRecord> groups) {

		List<GroupReportDetails> reports = new ArrayList<>();

		if (groups == null || groups.isEmpty()) {
			return reports;
		}

		for (GroupRecord groupRecord : groups) {
//				if (groupRecord.getGroupName().equals("Administrators") || groupRecord.getGroupName().equals("Everyone")) {
//					return reports;
//				}
			try {
				String url = "https://demo.docusign.net/restapi/v2.1/accounts/"
						+ "f6db2deb-ac95-455b-a7e6-a6c914dd8807/groups";

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.setBearerAuth(PropertiesHolder.getAccessToken());

				List<Map<String, Object>> newGroups = List.of(Map.of("groupName", groupRecord.getGroupName()));

				Map<String, Object> requestBody = Map.of("groups", newGroups);

				HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

				if (response.getStatusCode().is2xxSuccessful()) {

					JSONObject responseJson = new JSONObject(response.getBody());
					JSONArray responseGroups = responseJson.getJSONArray("groups");

					for (int i = 0; i < responseGroups.length(); i++) {

						JSONObject groupJson = responseGroups.getJSONObject(i);

						GroupReportDetails report = new GroupReportDetails();
						report.setGroupName(groupJson.optString("groupName"));

						JSONObject error = groupJson.optJSONObject("errorDetails");

						if (error == null || groupRecord.getGroupName().equals("Administrators")
								|| groupRecord.getGroupName().equals("Everyone")) {
							if (groupRecord.getUsers() != null)
								uploadUsersInGroups(groupJson.optString("groupId"), groupRecord);

							report.setStatus(true);
						} else {
							report.setStatus(false);
							report.setErrorMessage(error.optString("message"));
						}

						reports.add(report);
					}
				}

			} catch (Exception ex) {
				logger.error("Error while uploading group to DocuSign", ex);

				GroupReportDetails report = new GroupReportDetails();
				report.setGroupName(groupRecord.getGroupName());
				report.setStatus(false);
				report.setErrorMessage("DocuSign API call failed");
				reports.add(report);
			}
		}
		return reports;
	}

	public boolean uploadUsersInGroups(String groupId, GroupRecord groupRecord) {

		String url = "https://demo.docusign.net/restapi/v2.1/accounts/" + "f6db2deb-ac95-455b-a7e6-a6c914dd8807/groups/"
				+ groupId + "/users";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(PropertiesHolder.getAccessToken());

		List<Map<String, String>> users = groupRecord.getUsers().stream()
				.map(user -> Map.of("userId", getUserIdByName(user.getUserName()))).toList();

		Map<String, Object> requestBody = Map.of("users", users);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

		return response.getStatusCode().is2xxSuccessful();
	}

	public Map<String, String> getAllUserDetails() {

		Map<String, String> userDetails = new HashMap<>();

		try {
			String url = "https://demo.docusign.net/restapi/v2.1/accounts/"
					+ "f6db2deb-ac95-455b-a7e6-a6c914dd8807/users";

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(PropertiesHolder.getAccessToken());

			HttpEntity<Void> entity = new HttpEntity<>(headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

				JSONObject json = new JSONObject(response.getBody());
				JSONArray users = json.optJSONArray("users");

				if (users != null) {
					for (int i = 0; i < users.length(); i++) {
						JSONObject u = users.getJSONObject(i);
						userDetails.put(u.optString("userId"), u.optString("userName"));
					}
				}
			}

		} catch (Exception e) {
			logger.error("Failed to fetch DocuSign users", e);
		}

		return userDetails;
	}

	public String getUserIdByName(String userName) {

		if (userName == null) {
			return null;
		}

		Map<String, String> userDetailsMap = PropertiesHolder.getUserDetails();

		for (Map.Entry<String, String> entry : userDetailsMap.entrySet()) {
			if (userName.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static String getFileExtension(String fileName) {
		if (fileName == null || !fileName.contains(".")) {
			return "";
		}
		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}

}
