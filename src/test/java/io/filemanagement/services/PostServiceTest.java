package io.filemanagement.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
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

import io.filemanagement.exceptions.FileManagementRequestException;
import io.filemanagement.models.Document;
import io.filemanagement.models.Post;
import io.filemanagement.models.User;
import io.filemanagement.services.DocumentService;
import io.filemanagement.services.PostService;

@SpringBootTest
public class PostServiceTest {
	
	@Autowired
	private ApplicationContext context;

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


	@AfterEach
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void cleanUp() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.documentService);
		deleteAllPosts();
		documentService.deleteAll(username);
	}
	
	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testSavePost() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		setupTestData();
		Post post = new Post();
		post.setUsername(username);
		post.setUserId(1L);
		post.setTitle(docName1);
		post.setBody("This is a test");
		Post newPost = postService.save(post);
	    
		Boolean actualAfter1 = newPost.getId()!=null;
		Boolean expectedAfter1 = true;
		Assertions.assertEquals(expectedAfter1, actualAfter1);
		
		Boolean actualAfter2 = newPost.getTitle().equalsIgnoreCase(docName1);
		Boolean expectedAfter2 = true;
		Assertions.assertEquals(expectedAfter2, actualAfter2);
	}
	
	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testSavePostErrorNoDocument() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		String expectedErrorMessage = "Related entity / entity does not exist";
		Post post = new Post();
		post.setUserId(1L);
		post.setUsername(username);
		post.setTitle(docName1);
		post.setBody("This is a test");
		try {
			postService.save(post);
		} catch (FileManagementRequestException e) {
			assertThat(e.getMessage()).isEqualTo(expectedErrorMessage);
		}

	}
	
	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testGetPost() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		deleteUserDocData();
		//setupTestData();
		testSavePost();
		Post post = postService.getPost(docName1);
		
		Boolean actualAfter1 = post.getId()!=null;
		Boolean expectedAfter1 = true;
		Assertions.assertEquals(expectedAfter1, actualAfter1);
		
		Boolean actualAfter2 = post.getTitle().equals(docName1);
		Boolean expectedAfter2 = true;
		Assertions.assertEquals(expectedAfter2, actualAfter2);
		
	}
	
	@Test
	public void testGetPostNoPosts() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		//setupTestData();
		Post post = postService.getPost(docName1);
		
		Boolean actualAfter1 = (post==null||post.getId()==null);
		Boolean expectedAfter1 = true;
		Assertions.assertEquals(expectedAfter1, actualAfter1);
		
	}
	
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void setupTestData() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.documentService);
		//deleteUserDocData();
		User user = new User();
	    user.setUsername(username);
	    Set<Document> docs  = new HashSet<>();
		Document document = new Document();
	    document.setFilePath(docName1);
	    document.setUser(user);
	    docs.add(document);
	    user.setDocuments(docs);
	    
	    documentService.update(document);
		
	}
	
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteUserDocData() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.documentService);
		documentService.deleteAll(username);
	}
	
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteAllPosts() throws FileManagementRequestException, Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.postService);
		postService.deleteAll();	
	}
}
