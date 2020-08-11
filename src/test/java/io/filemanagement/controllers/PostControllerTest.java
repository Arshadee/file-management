package io.filemanagement.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import io.filemanagement.controllers.PostController;
import io.filemanagement.dtos.PostDto;
import io.filemanagement.services.PostService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@JsonSerialize
public class PostControllerTest {
	@Autowired
	private ApplicationContext context;

	@LocalServerPort
	private int port;

	@Autowired
	PostController postController;
	
	@Autowired
	DocumentController documentController;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	private PostService service;

	@Value("${root}")
	private String root;

	private final String username = "MockUser";

	private final String docName1 = "Scan0001.pdf";
	
	private final String body = "This is a test post";

	
	@AfterEach
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteFolder() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		testDeleteAll();
	}
	
	@Test
	public void testcreatePostSuccess() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postController);
		testUploadFilePdfSuccess();
		
		PostDto postDto = new PostDto();
		postDto.setUsername(username);
		postDto.setTitle(docName1);
		postDto.setBody(body);
		
		
		ResponseEntity<PostDto> responseEntity = this.restTemplate.postForEntity(
                "http://localhost:"+port+"/username/"+username+"/document/"+docName1+"/posts",
                postDto,
                PostDto.class);
		
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201);

	}
	
	@Test
	public void testcreatePostNoDocument() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postController);
		
		PostDto postDto = new PostDto();
		postDto.setUsername(username);
		postDto.setTitle(docName1);
		postDto.setBody(body);
		
		
		ResponseEntity<PostDto> responseEntity = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/username/document/posts", postDto,
                PostDto.class);
		
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
	}
	
	@Test
	public void testcreatePostBeanValidationError() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postController);
	
		testUploadFilePdfSuccess();
		
		PostDto postDto = new PostDto();
		postDto.setUsername(username);
		postDto.setTitle(docName1);
		postDto.setBody("x");
		
		
		ResponseEntity<PostDto> responseEntity = this.restTemplate.postForEntity(
				"http://localhost:"+ port +"/username/"+username+"/document/"+docName1+"/posts",
                postDto,
                PostDto.class);
		
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(400);
	}
	
	@Test
	public void testcreatePostBeanValidationErrorNullParam() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postController);
	
		testUploadFilePdfSuccess();
		
		PostDto postDto = new PostDto();
		postDto.setUsername(username);
		postDto.setTitle(docName1);
		//postDto.setBody(body);
		
		
		ResponseEntity<PostDto> responseEntity = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/username/"+username+"/document/"+docName1+"/posts",
                postDto,
                PostDto.class);
		
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(400);
	}
	
	@Test
	public void testgetPostSuccess() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postController);
		testUploadFilePdfSuccess();
		testcreatePostSuccess();
		ResponseEntity<PostDto> responseEntity = this.restTemplate
                .getForEntity("http://localhost:" + port + "/username/" + username +"/document/"+docName1+"/posts", PostDto.class);
	    
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
	}
	
	@Test
	public void testgetPostNoPost() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postController);
		testUploadFilePdfSuccess();
		ResponseEntity<PostDto> responseEntity = this.restTemplate
                .getForEntity("http://localhost:" + port + "/username/" + username +"/document/"+docName1+"/posts", PostDto.class);
	    
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
		
	}
	
	@Test
	public void testgetPostNoDocument() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postController);
		ResponseEntity<PostDto> responseEntity = this.restTemplate
                .getForEntity("http://localhost:" + port + "/username/" + username +"/document/"+docName1+"/posts", PostDto.class);
	    
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
		
	}
	
	@Test
	public void testDeleteAll() throws UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.documentController);
		Unirest.setTimeouts(0, 0);
		HttpResponse<String> response = Unirest.delete("http://localhost:"+port+"/upload/"+username)
		  .asString();
		
		assertThat(response.getStatus()).isEqualTo(200);
	}
	
	@Test
	public void testUploadFilePdfSuccess() throws IOException, UnirestException {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.documentController);
		Resource resource = resourceLoader.getResource("classpath:" + docName1);
		File file = resource.getFile();

		Unirest.setTimeouts(0, 0);
		HttpResponse<String> response = Unirest.post("http://localhost:" + port + "/upload/" + username)
				.field("file", file).asString();

		assertThat(response.getStatus()).isEqualTo(201);

	}
	
	

}
