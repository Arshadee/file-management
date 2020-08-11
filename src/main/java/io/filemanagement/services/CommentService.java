package io.filemanagement.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.naming.ServiceUnavailableException;

import org.apache.http.ConnectionClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.filemanagement.exceptions.FileManagementRequestException;
import io.filemanagement.models.Comment;
import io.filemanagement.models.Post;
import io.filemanagement.repository.CommentRepository;
import io.filemanagement.repository.PostRepository;
import io.filemanagement.validations.IValidator;

@Service
public class CommentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	@Qualifier("validateNotNull")
	private IValidator<Object> notNullValidator;

	public Comment getCommentByPostIdAndId(Long commentId, Long postId) {
		return commentRepository.findByIdAndPostId(commentId, postId);
	}

	public List<Comment> getCommentsByPostId(Long postId) {
		return commentRepository.findByPostId(postId);
	}

	public List<Comment> getAllComments() {
		return (List<Comment>) commentRepository.findAll();
	}

	@Retryable(value = { SQLException.class, ConnectionClosedException.class, ServiceUnavailableException.class,
			DataIntegrityViolationException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1500))
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	 public Comment save(Comment comment) throws FileManagementRequestException,
		 Exception {
		Comment commenResult = null;
		try {
			Optional<Post> post = postRepository.findById(comment.getPostId());
			notNullValidator.validate(post.orElse(null), "Related entity / entity does not exist");
			commenResult = commentRepository.save(comment);
		} catch (DataIntegrityViolationException de) {
			LOGGER.info("Bad Request - Post data cannot be saved to DB " + de.getMessage());
			throw new FileManagementRequestException("Unique Key violation - Possibly adding a record that exists");
		}
		return commenResult;
	}

	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteAll() {
		commentRepository.deleteAll();
	}

	public List<Comment> getCommentByBody(String body) {
		return commentRepository.findByBody(body);
	}
}
