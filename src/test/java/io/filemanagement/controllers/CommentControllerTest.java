package io.filemanagement.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

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
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.filemanagement.dtos.CommentDto;
import io.filemanagement.exceptions.FileManagementRequestException;
import io.filemanagement.models.Comment;
import io.filemanagement.models.Document;
import io.filemanagement.models.Post;
import io.filemanagement.models.User;
import io.filemanagement.services.CommentService;
import io.filemanagement.services.DocumentService;
import io.filemanagement.services.PostService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@JsonSerialize
public class CommentControllerTest {
	@Autowired
	private ApplicationContext context;

	@LocalServerPort
	private int port;

	@Autowired
	DocumentController documentController;

	@Autowired
	PostController postController;

	@Autowired
	CommentController commentController;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private PostService postService;

	@Autowired
	private CommentService commentService;

	@Value("${root}")
	private String root;

	private final String username = "MockUser";

	private final String docName1 = "Scan0001.pdf";

	private final String email = "test@test.com";

	private final String body = "This is a test comment";

	@AfterEach
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void cleanUp() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.documentService);
		deleteAllComments();
		deleteAllPosts();
		documentService.deleteAll(username);
		documentService.deleteAllUsers();
	}

	@Test
	public void testcreateCommentSuccess() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentController);
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		setupTestUserDocData();
		Post post = postService.getPost(docName1);
		CommentDto commentDto = new CommentDto();
		commentDto.setPostId(post.getId());
		commentDto.setName(username);
		commentDto.setEmail(email);
		commentDto.setBody(body);
		ResponseEntity<CommentDto> responseEntity = this.restTemplate.postForEntity(
				"http://localhost:" + port + "/document/posts/" + post.getId() + "/comments", commentDto,
				CommentDto.class);
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201);
	}

	@Test
	public void testcreateCommentBeanValidationError() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentController);
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		setupTestUserDocData();
		Post post = postService.getPost(docName1);
		CommentDto commentDto = new CommentDto();
		commentDto.setPostId(post.getId());
		commentDto.setEmail(email);
		commentDto.setBody(body);
		ResponseEntity<CommentDto> responseEntity = this.restTemplate.postForEntity(
				"http://localhost:" + port + "/document/posts/" + post.getId() + "/comments", commentDto,
				CommentDto.class);
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(400);
	}

	@Test
	public void testcreateCommentNoPostError() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentController);
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		CommentDto commentDto = new CommentDto();
		commentDto.setPostId(1L);
		commentDto.setName(username);
		commentDto.setEmail(email);
		commentDto.setBody(body);
		ResponseEntity<CommentDto> responseEntity = this.restTemplate.postForEntity(
				"http://localhost:" + port + "/document/posts/" + commentDto.getPostId() + "/comments", commentDto,
				CommentDto.class);
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
	}

	@Test
	public void getComments() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentController);
		testcreateCommentSuccess();
		Post post = postService.getPost(docName1);
		ResponseEntity<CommentDto[]> responseEntity = this.restTemplate.getForEntity(
				"http://localhost:" + port + "/document/posts/" + post.getId() + "/comments", CommentDto[].class);
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
	}

	@Test
	public void getCommentsNoCommentsFound() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentController);
		setupTestUserDocData();
		Post post = postService.getPost(docName1);
		ResponseEntity<CommentDto[]> responseEntity = this.restTemplate.getForEntity(
				"http://localhost:" + port + "/document/posts/" + post.getId() + "/comments", CommentDto[].class);
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
	}

	@Test
	public void getCommentsNoPostFound() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentController);
		ResponseEntity<CommentDto[]> responseEntity = this.restTemplate
				.getForEntity("http://localhost:" + port + "/document/posts/" + 1 + "/comments", CommentDto[].class);
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
	}

	@Test
	public void getComment() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentController);
		testcreateCommentSuccess();
		Post post = postService.getPost(docName1);
		Comment comment = commentService.getCommentsByPostId(post.getId()).get(0);
		ResponseEntity<CommentDto> responseEntity = this.restTemplate.getForEntity(
				"http://localhost:" + port + "/document/posts/" + post.getId() + "/comment/" + comment.getId(),
				CommentDto.class);
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
	}

	@Test
	public void getCommentNoFound() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentController);
		setupTestUserDocData();
		Post post = postService.getPost(docName1);
		ResponseEntity<CommentDto> responseEntity = this.restTemplate.getForEntity(
				"http://localhost:" + port + "/document/posts/" + post.getId() + "/comment/" + 1, CommentDto.class);
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
	}


	// setup & cleanup methods
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void setupTestUserDocData() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.documentService);
		User user = new User();
		user.setUsername(username);
		Set<Document> docs = new HashSet<>();
		Document document = new Document();
		document.setFilePath(docName1);
		document.setUser(user);
		docs.add(document);
		user.setDocuments(docs);
		documentService.update(document);
		setupTestPostData(document, user);
	}

	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void setupTestPostData(Document document, User user) throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		Post post = new Post();
		post.setTitle(document.getFilePath());
		post.setUserId(1L);
		post.setUsername(user.getUsername());
		post.setBody("This is a test post");
		postService.save(post);
	}

	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteAllComments() {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentService);
		commentService.deleteAll();
	}

	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteAllPosts() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		postService.deleteAll();
	}

	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	private void saveComment() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentService);
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		setupTestUserDocData();
		Long postid = postService.getPost(docName1).getId();
		Comment comment = new Comment();
		comment.setPostId(postid);
		comment.setName(username);
		comment.setEmail("test@test.com");
		comment.setBody("This is a test comment");
		commentService.save(comment);
	}

}
