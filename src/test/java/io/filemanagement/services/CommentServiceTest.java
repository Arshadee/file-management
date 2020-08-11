package io.filemanagement.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.filemanagement.exceptions.FileManagementNotFoundException;
import io.filemanagement.exceptions.FileManagementRequestException;
import io.filemanagement.models.Comment;
import io.filemanagement.models.Document;
import io.filemanagement.models.Post;
import io.filemanagement.models.User;

@SpringBootTest
public class CommentServiceTest {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private CommentService commentService;

	@Autowired
	private PostService postService;

	@InjectMocks
	private DocumentService documentService;

	@Autowired
	ResourceLoader resourceLoader;

	@Value("${root}")
	private String root;

	private final String username = "MockUser";

	private final String docName1 = "Scan0001.pdf";
	
	private final String body = "This is a test comment";
	
	private final String email = "test@test.com";

	@AfterEach
	public void cleanUp() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.documentService);
		deleteAllComments();
		deleteAllPosts();
		documentService.deleteAll(username);
	}

	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void save() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentService);
		setupTestUserDocData();
		Comment comment = new Comment();
		comment.setPostId(1L);
		comment.setName(username);
		comment.setEmail(email);
		comment.setBody(body);
		Comment newComment = commentService.save(comment);

		Boolean actualAfter1 = newComment.getId() != null;
		Boolean expectedAfter1 = true;
		Assertions.assertEquals(expectedAfter1, actualAfter1);

		Boolean actualAfter2 = newComment.getName().equalsIgnoreCase(username);
		Boolean expectedAfter2 = true;
		Assertions.assertEquals(expectedAfter2, actualAfter2);
	}

	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void saveNoPost() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentService);
		setupTestUserDocData();
		deleteAllPosts();
		String expectedErrorMessage = "Related entity / entity does not exist";
		try {
			Comment comment = new Comment();
			comment.setPostId(1L);
			comment.setName(username);
			comment.setEmail(email);
			comment.setBody(body);
			commentService.save(comment);
		} catch (FileManagementNotFoundException e) {
			assertThat(e.getMessage()).isEqualTo(expectedErrorMessage);
		}

	}
	
	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testGetCommentByPostIdAndId() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentService);
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		saveComment();
		Long postId = postService.getPost(docName1).getId();
		Long  commentId = commentService.getCommentByBody(body).get(0).getId();
		Comment comment = commentService.getCommentByPostIdAndId(commentId, postId);
		Boolean actualAfter1 = comment.getName().equals(username);
		Boolean expectedAfter1 = true;
		Assertions.assertEquals(expectedAfter1, actualAfter1);
	}
	
	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testGetCommentByPostId() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentService);
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		saveComment();
		Long postId = postService.getPost(docName1).getId();
		List<Comment> comments = commentService.getCommentsByPostId(postId);
		Boolean actualAfter1 = comments.size()>0;
		Boolean expectedAfter1 = true;
		Assertions.assertEquals(expectedAfter1, actualAfter1);
	}
	
	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testGetAllComments()throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.commentService);
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		saveComment();
		List<Comment> comments = commentService.getAllComments();
		Boolean actualAfter1 = comments.size()>0;
		Boolean expectedAfter1 = true;
		Assertions.assertEquals(expectedAfter1, actualAfter1);
	}

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
		post.setUserId(1L);// user.getId());
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
		//Comment newComment = commentService.save(comment);
		commentService.save(comment);
	}

}
