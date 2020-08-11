package io.filemanagement.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.filemanagement.controllers.DocumentController;
import io.filemanagement.dtos.FileInfoDto;
import io.filemanagement.services.DocumentService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@JsonSerialize
public class DocumentControllerTest {

	@Autowired
	private ApplicationContext context;

	@LocalServerPort
	private int port;

	@InjectMocks
	DocumentController controller;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	ResourceLoader resourceLoader;

	@InjectMocks
	private DocumentService service;

	@Value("${root}")
	private String root;

	private final String username = "MockUser";

	private final String docName1 = "Scan0001.pdf";

	private final String incorrectDocFormat = "test.rtf";

	@AfterEach
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteFolder() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		testDeleteAll();
	}

	@Test
	public void testCreateFolder() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.controller);
		ResponseEntity<String> responseEntity = this.restTemplate
				.postForEntity("http://localhost:" + port + "/createdir/" + username, null, String.class);

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201);
	}

	@Test
	public void testUploadFilePdfSuccess() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.controller);
		Resource resource = resourceLoader.getResource("classpath:" + docName1);
		File file = resource.getFile();

		Unirest.setTimeouts(0, 0);
		HttpResponse<String> response = Unirest.post("http://localhost:" + port + "/upload/" + username)
				.field("file", file).asString();

		assertThat(response.getStatus()).isEqualTo(201);
	}
	

	@Test
	public void testUploadFilePdfSuccessUserFolderExists() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.controller);
		testCreateFolder();
		Resource resource = resourceLoader.getResource("classpath:" + docName1);
		File file = resource.getFile();

		Unirest.setTimeouts(0, 0);
		HttpResponse<String> response = Unirest.post("http://localhost:" + port + "/upload/" + username)
				.field("file", file).asString();

		assertThat(response.getStatus()).isEqualTo(201);
	}

	@Test
	public void testUploadFileNonPdfError() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.controller);
		Resource resource = resourceLoader.getResource("classpath:" + incorrectDocFormat);
		File file = resource.getFile();

		Unirest.setTimeouts(0, 0);
		HttpResponse<String> response = Unirest.post("http://localhost:" + port + "/upload/" + username)
				.field("file", file)
				.asString();

		assertThat(response.getStatus()).isEqualTo(400);
	}

	@Test
	public void testGetListFiles() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.controller);
		testUploadFilePdfSuccess();
		ResponseEntity<FileInfoDto[]> responseEntity = this.restTemplate
				.getForEntity("http://localhost:" + port + "/files/" + username, FileInfoDto[].class);

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
	}

	@Test
	public void testGetFileSuccess() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.controller);
		testUploadFilePdfSuccess();
		ResponseEntity<Resource> responseEntity = this.restTemplate
				.getForEntity("http://localhost:" + port + "/file/"+username+"/"+docName1, Resource.class);
		
		 assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
	}
	
	@Test
	public void testGetFileNonExists() {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.controller);
		ResponseEntity<Resource> responseEntity = this.restTemplate
				.getForEntity("http://localhost:" + port + "/file/"+username+"/"+incorrectDocFormat, Resource.class);
	
		 assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
	}

	@Test
	public void testDeleteFile() throws UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.controller);
	
		Unirest.setTimeouts(0, 0);
		HttpResponse<String> response = Unirest.delete("http://localhost:"+port+"/upload/"+username+"/"+docName1)
		  .asString();
		
		assertThat(response.getStatus()).isEqualTo(200);
	}
	
	@Test
	public void testDeleteAll() throws UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.controller);
		Unirest.setTimeouts(0, 0);
		HttpResponse<String> response = Unirest.delete("http://localhost:"+port+"/upload/"+username)
		  .asString();
		
		assertThat(response.getStatus()).isEqualTo(200);
	}
	
	public void uploadFilePdfSuccess() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.controller);
		Resource resource = resourceLoader.getResource("classpath:" + docName1);
		File file = resource.getFile();

		Unirest.setTimeouts(0, 0);
		HttpResponse<String> response = Unirest.post("http://localhost:" + port + "/upload/" + username)
				.field("file", file).asString();
	}
}
