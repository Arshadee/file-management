package io.filemanagement.thirdparty.jsonplaceholder;

import java.sql.SQLException;
import java.util.List;

import javax.naming.ServiceUnavailableException;

import org.apache.http.ConnectionClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.filemanagement.dtos.CommentDto;
import io.filemanagement.exceptions.FileManagementExtServerDownException;
import io.filemanagement.thirdparty.abstraction.IRestFunctions;

@Component("CommentPlaceHolder")
public class JsonPlaceHolderCommentService implements IRestFunctions<CommentDto> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonPlaceHolderCommentService.class);

	private final RestTemplate restTemplate;

	public JsonPlaceHolderCommentService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@SuppressWarnings("hiding")
	@Override
	public <Long> ResponseEntity<CommentDto> get(Long key) {
		LOGGER.info("comment returned successfully");
		String url = "https://jsonplaceholder.typicode.com/posts/" + key + "/comments";
		ResponseEntity<CommentDto> response = this.restTemplate.getForEntity(url, CommentDto.class, key);
		if (response.getStatusCode() == HttpStatus.OK) {
			return response;
		} else {
			return null;
		}
	}

	@Override
	@Retryable(value = { SQLException.class, NullPointerException.class, ConnectionClosedException.class,
			ServiceUnavailableException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1500))
	public ResponseEntity<CommentDto> post(CommentDto placeHolderEntity)
			throws SQLException, ConnectionClosedException, ServiceUnavailableException {
		LOGGER.info("comment added successfully");
		return new ResponseEntity<CommentDto>(placeHolderEntity, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<CommentDto> put(CommentDto placeHolderEntity) {
		return null;
	}

	@Override
	public ResponseEntity<CommentDto> delete(CommentDto placeHolderEntity) {
		return null;
	}

	@SuppressWarnings("hiding")
	@Override
	public <Long> ResponseEntity<List<CommentDto>> getList(Long key) {
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
