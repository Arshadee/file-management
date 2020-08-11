package io.filemanagement.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import io.filemanagement.exceptions.FileManagementRequestException;
import io.filemanagement.models.Post;
import io.filemanagement.repository.PostRepository;

@Service
public class PostService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);

	private final RestTemplate restTemplate;

	@Autowired
	private PostRepository postRepository;

	public PostService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	public Post getPost(String documentName) {
		Post postResult = null;
		postResult = postRepository.findByTitle(documentName);
		return postResult;
	}

	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public Post save(Post post) throws FileManagementRequestException, Exception {
		Post postResult = null;
		try {
			postResult = postRepository.save(post);
		} catch (DataIntegrityViolationException de) {
			LOGGER.info("Bad Request - Post data cannot be saved to DB " + de.getMessage());
			//throw new FileManagementRequestException(de.getMessage(), de);
			throw new FileManagementRequestException("Unique Key violation - Possibly adding a record that exists");
		} catch (Exception e) {
			LOGGER.info("@@@ exception uk voiation");
			e.printStackTrace();
		}
		return postResult;
	}
	

	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteAll() throws FileManagementRequestException, Exception {
		try {
			postRepository.deleteAll();
		} catch (DataIntegrityViolationException de) {
			LOGGER.info("Bad Request - Post data cannot be saved to DB " + de.getMessage());
			throw new FileManagementRequestException(de.getMessage(), de);
		} catch (Exception e) {
			LOGGER.info("@@@ exception uk voiation");
			e.printStackTrace();
		}
	}

}
