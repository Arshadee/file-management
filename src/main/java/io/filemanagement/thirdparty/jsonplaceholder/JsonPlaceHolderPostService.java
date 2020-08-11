package io.filemanagement.thirdparty.jsonplaceholder;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.ServiceUnavailableException;

import org.apache.http.ConnectionClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.filemanagement.dtos.CommentDto;
import io.filemanagement.dtos.PostDto;
import io.filemanagement.exceptions.FileManagementExtServerDownException;
import io.filemanagement.models.Post;
import io.filemanagement.thirdparty.abstraction.IRestFunctions;
import io.filemanagement.utils.FileManagmentEntityDtoMappingService;

@Component("PostPlaceHolder")
public class JsonPlaceHolderPostService implements IRestFunctions<PostDto> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonPlaceHolderPostService.class);

	@Autowired
	FileManagmentEntityDtoMappingService<Post, PostDto> mappingService;

	private final RestTemplate restTemplate;

	public JsonPlaceHolderPostService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@SuppressWarnings("hiding")
	@Override
	public <Long> ResponseEntity<PostDto> get(Long key) {
		String url = "https://jsonplaceholder.typicode.com/posts/{id}";
		ResponseEntity<PostDto> response = this.restTemplate.getForEntity(url, PostDto.class, key);
		if (response.getStatusCode() == HttpStatus.OK) {
			LOGGER.info("post successfully retrieved");
			return response;
		} else {
			LOGGER.info("post not found");
			return null;
		}
	}

	@Override
	@Retryable(value = { SQLException.class, NullPointerException.class, ConnectionClosedException.class,
			ServiceUnavailableException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1500))
	public ResponseEntity<PostDto> post(PostDto post) {
		String url = "https://jsonplaceholder.typicode.com/posts";

		// create headers
		HttpHeaders headers = new HttpHeaders();

		// set`content-type` header
		headers.setContentType(MediaType.APPLICATION_JSON);

		// set `accept` header
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		// create a map for post parameters
		Map<String, Object> map = new HashMap<>();
		map.put("userId", post.getUsername());// getUserId());
		map.put("title", post.getTitle());
		map.put("body", post.getBody());

		// build the request
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		// send POST request
		ResponseEntity<PostDto> response = this.restTemplate.postForEntity(url, entity, PostDto.class);

		// check response status code
		if (response.getStatusCode() == HttpStatus.CREATED) {
			LOGGER.info("post successfully created");
			return new ResponseEntity<PostDto>(post, response.getStatusCode());
		} else {
			// to throw not found exception
			//return null;
			LOGGER.info("post not found / or created");
			return new ResponseEntity<PostDto>(response.getStatusCode());
		}

	}

	@Override
	public ResponseEntity<PostDto> put(PostDto post) {
		return null;
	}

	@Override
	public ResponseEntity<PostDto> delete(PostDto post) {
		return null;
	}

	@SuppressWarnings("hiding")
	@Override
	public <Long> ResponseEntity<List<PostDto>> getList(Long key) {
		return null;
	}
	
	@Override
	@Recover
	public ResponseEntity<CommentDto> recover(ConnectionClosedException exception) throws FileManagementExtServerDownException{
		throw new FileManagementExtServerDownException(exception.getMessage());
	}
	
	@Override
	@Recover
	public ResponseEntity<CommentDto> recover(ServiceUnavailableException exception) throws FileManagementExtServerDownException{
		throw new FileManagementExtServerDownException(exception.getMessage());
	}

}
