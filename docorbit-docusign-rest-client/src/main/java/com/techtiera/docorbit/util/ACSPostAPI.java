package com.techtiera.docorbit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class ACSPostAPI {

	private final CloseableHttpClient httpClient;
	String encoding = null;
	String url = "http://192.168.60.144/";
	// public static File folder = new File("C:\\Users\\Downloads\\Annotations");
	public static File folder = new File("D:\\Alfresco\\EXporter-Output2\\content\\Section");

	static String temp = "";
	String folderId = "84b9eecb-8bca-4540-8436-a06a3bc4d583";
	String associationId = "d9744942-284d-47cc-bfcd-ea99dbbde23f";

	public static void main(String[] args) throws IOException {

		ACSPostAPI restAction = new ACSPostAPI();
		restAction.listFilesForFolder(folder);
	}

	public ACSPostAPI() {

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setDefaultMaxPerRoute(200);
		cm.setMaxTotal(200);
		httpClient = HttpClients.custom().setConnectionManager(cm).build();
		String username = "admin";
		String password = "Alfresco01";
		// If using Java 8 use the default
		// String encoding = Base64.getEncoder().encodeToString((username + ":" +
		// password).getBytes("UTF-8"));
		encoding = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
	}

	private void uploadAnnotation(String filePath, String sourceId) throws IOException {

		CloseableHttpResponse response = null;
		String jsonString = null;
		HttpRequestBase httpPostRequest = new HttpPost(
				url + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + folderId + "/children");

		// String path = "C:\\Users\\Downloads\\content.xfdf";
		File file = new File(filePath);
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] streamByteArray = IOUtils.toByteArray(fileInputStream);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//		builder.addBinaryBody("filedata", streamByteArray, ContentType.APPLICATION_OCTET_STREAM, file.getName());
		builder.addBinaryBody("filedata", streamByteArray, ContentType.create("application/vnd.adobe.xfdf"),
				file.getName());
		String fileName = file.getName();
		builder.addTextBody("filename", fileName);
		builder.addTextBody("nodeType", "oa:annotation");
		builder.addTextBody("cm:autoVersion", "false");
		builder.addTextBody("cm:versionLabel", "1.0");
		builder.addTextBody("oa:annotatedVersion", "1.0");

		HttpEntity multipart = builder.build();
		((HttpPost) httpPostRequest).setEntity(multipart);

		httpPostRequest.addHeader("Authorization", "Basic " + encoding);

		Long httpStartTime = new Date().getTime();
		response = (CloseableHttpResponse) executeHttpRequest(httpPostRequest, "POST");
		System.out.println("Total HTTP Execution Time: " + ((new Date()).getTime() - httpStartTime));

		try {
			jsonString = EntityUtils.toString(response.getEntity());
			// System.out.println(jsonString);
			JSONObject entry = new JSONObject(jsonString).getJSONObject("entry");
			// System.out.println(entry);
			System.out.println("Afte document creation id : " + entry.getString("id"));
			// renameAnnotation(entry.getString("id"), sourceId);
			createAssociations(entry.getString("id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void renameAnnotation(String annoateNodeId, String sourceId) throws IOException {

		CloseableHttpResponse response = null;
		String jsonString = null;
		HttpPut httpPutRequest = new HttpPut(
				url + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + annoateNodeId);

		String jsonInput = "{\r\n" + "  \"name\": \"" + annoateNodeId + "\"\r\n" + "}";
		StringEntity stringEntity = new StringEntity(jsonInput);
		httpPutRequest.setEntity(stringEntity);
		httpPutRequest.addHeader("Authorization", "Basic " + encoding);

		Long httpStartTime = new Date().getTime();
		response = (CloseableHttpResponse) executeHttpRequest(httpPutRequest, "PUT");
		System.out.println("Total HTTP Execution Time: " + ((new Date()).getTime() - httpStartTime));
		jsonString = EntityUtils.toString(response.getEntity());
		System.out.println(jsonString);
		JSONObject entry = new JSONObject(jsonString).getJSONObject("entry");
		String newAnnotatedId = entry.getString("id");
		System.out.println("After rename document id : " + newAnnotatedId);
		createAssociations(newAnnotatedId);
		System.out.println("--------------------------------------------------------------------------------------");

	}

	private void createAssociations(String annoateNodeId) throws IOException {

		System.out.println("annoateNodeId : " + annoateNodeId);
		CloseableHttpResponse response = null;
		String jsonString = null;
		HttpPost httpPostRequest = new HttpPost(
				url + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + annoateNodeId + "/targets");

		String jsonInput = "{\r\n" + "  \"targetId\": \"" + associationId + "\",\r\n"
				+ "  \"assocType\": \"oa:annotates\"\r\n" + "}";
		StringEntity stringEntity = new StringEntity(jsonInput);
		httpPostRequest.setEntity(stringEntity);
		httpPostRequest.addHeader("Authorization", "Basic " + encoding);

		Long httpStartTime = new Date().getTime();
		response = (CloseableHttpResponse) executeHttpRequest(httpPostRequest, "PUT");
		System.out.println("Total HTTP Execution Time: " + ((new Date()).getTime() - httpStartTime));
		jsonString = EntityUtils.toString(response.getEntity());
		System.out.println(jsonString);

	}

	protected HttpResponse executeHttpRequest(HttpRequestBase httpRequest, String method) {

		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (response.getStatusLine().getStatusCode() >= 400) {
			System.out.println("error while executing http request " + method + " " + httpRequest.getURI()
					+ " with status code: " + response.getStatusLine().getStatusCode());
		}
		return response;
	}

	private void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.isFile()) {
					temp = fileEntry.getName();
					System.out.println("Name:" + temp);
					if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("xfdf"))
						System.out.println("File = " + fileEntry.getAbsolutePath());
					System.out.println("NodeId = " + temp.substring(0, temp.lastIndexOf('.')));
					try {
						uploadAnnotation(fileEntry.getAbsolutePath(), temp.substring(0, temp.lastIndexOf('.')));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}
