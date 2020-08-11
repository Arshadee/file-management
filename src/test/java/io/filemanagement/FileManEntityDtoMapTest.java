package io.filemanagement;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import io.filemanagement.dtos.CommentDto;
import io.filemanagement.models.Comment;
import io.filemanagement.utils.FileManagmentEntityDtoMappingService;

@SpringBootTest
public class FileManEntityDtoMapTest {
	
	@Autowired
	private ApplicationContext context;

	@Autowired
	private FileManagmentEntityDtoMappingService<Comment,CommentDto> service;
	
	/*
	



	public List<E> fromDtoList(List<D> dtos, E newTargetEntity) 

	public List<E> fromDtoList(List<D> dtos, E newTargetEntity, String... exclList) 

	

	

	public List<D> toDtoList(List<E> entities, D newTargetDto) 

	public List<D> toDtoList(List<E> entities, D newTargetDto, String... exclList) 


	

	 
	 */
	@Test
	public void fromDtoTest() {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		Comment comment = new Comment();
		Comment expectedComment = createExpectedComment();
		CommentDto expectedCommentDto = createExpectedCommentDto();
		comment = service.fromDto(expectedCommentDto, comment);
		Boolean actualAfter2 = comment.equals(expectedComment); 
		Boolean expectedAfter2 = true;
		Assertions.assertEquals(expectedAfter2, actualAfter2);
	}
	
	@Test
	public void toDtoTest() {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		CommentDto commentDto = new CommentDto();
		CommentDto expectedCommentDto = createExpectedCommentDto();
		Comment expectedComment = createExpectedComment();
		commentDto = service.toDto(commentDto, expectedComment);
		Boolean actualAfter2 = commentDto.equals(expectedCommentDto); 
		Boolean expectedAfter2 = true;
		Assertions.assertEquals(expectedAfter2, actualAfter2);
	}
	
	@Test
	public void fromDtoExclTest() {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		Comment comment = new Comment();
		Comment expectedComment = createExpectedExclComment();
		CommentDto expectedCommentDto = createExpectedCommentDto();
		comment = service.fromDto(expectedCommentDto, comment,"name");
		Boolean actualAfter2 = comment.equals(expectedComment); 
		Boolean expectedAfter2 = true;
		Assertions.assertEquals(expectedAfter2, actualAfter2);
	}
	
	@Test
	public void toDtoExclTest() {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		CommentDto commentDto = new CommentDto();
		CommentDto expectedCommentDto = createExpectedExclCommentDto();
		Comment expectedComment = createExpectedComment();
		commentDto = service.toDto(commentDto, expectedComment,"name");
		Boolean actualAfter2 = commentDto.equals(expectedCommentDto); 
		Boolean expectedAfter2 = true;
		Assertions.assertEquals(expectedAfter2, actualAfter2);
	}
	
	@Test
	public void fromDtoListTest() {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		List<Comment> commentList = new ArrayList<>();
		List<Comment> expectedCommentList = getExpectedCommentList();
		List<CommentDto> expectedCommentDtoList = getExpectedCommentDtoList();
		commentList = service.fromDtoList(expectedCommentDtoList, new Comment());
		Boolean actualAfter2 = commentList.equals(expectedCommentList); 
		Boolean expectedAfter2 = true;
		Assertions.assertEquals(expectedAfter2, actualAfter2);
	}
	
	@Test
	public void toDtoListTest() {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		List<CommentDto> commentDtoList = new ArrayList<>();
		List<CommentDto> expectedCommentDtoList = getExpectedCommentDtoList();
		List<Comment> expectedCommentList = getExpectedCommentList();
		commentDtoList = service.toDtoList(expectedCommentList, new CommentDto());
		Boolean actualAfter2 = commentDtoList.equals(expectedCommentDtoList); 
		Boolean expectedAfter2 = true;
		Assertions.assertEquals(expectedAfter2, actualAfter2);
	}
	
	//-----
	
	@Test
	public void fromDtoExclListTest() {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		List<Comment> commentList = new ArrayList<>();
		List<Comment> expectedCommentList = getExpectedExclCommentList();
		List<CommentDto> expectedCommentDtoList = getExpectedCommentDtoList();
		commentList = service.fromDtoList(expectedCommentDtoList, new Comment(),"name");
		Boolean actualAfter2 = commentList.equals(expectedCommentList); 
		Boolean expectedAfter2 = true;
		Assertions.assertEquals(expectedAfter2, actualAfter2);
	}
	
	@Test
	public void toDtoExclListTest() {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		List<CommentDto> commentDtoList = new ArrayList<>();
		List<CommentDto> expectedCommentDtoList = getExpectedExclCommentDtoList();
		List<Comment> expectedCommentList = getExpectedCommentList();
		commentDtoList = service.toDtoList(expectedCommentList, new CommentDto(),"name");
		
		Boolean actualAfter2 = commentDtoList.equals(expectedCommentDtoList); 
		Boolean expectedAfter2 = true;
		Assertions.assertEquals(expectedAfter2, actualAfter2);
	}
	
	/*
	 * Setting up data for testing
	 */
	public Comment createExpectedComment() {
		Comment comment = new Comment();
		comment.setId(1L);
		comment.setPostId(1L);
		comment.setName("joe");
		comment.setEmail("mail@mail.com");
		comment.setBody("testing");
		return comment;
	}
	
	public CommentDto createExpectedCommentDto() {
		//public CommentDto(Long id, Long postId, String name, String email, String body) {
		CommentDto commentDto = new CommentDto(1L, 1L, "joe", "mail@mail.com","testing");
		return commentDto;
	}
	
	public Comment createExpectedExclComment() {
		Comment comment = new Comment();
		comment.setId(1L);
		comment.setPostId(1L);
		//comment.setName("joe");
		comment.setEmail("mail@mail.com");
		comment.setBody("testing");
		return comment;
	}
	
	public CommentDto createExpectedExclCommentDto() {
		CommentDto commentDto = new CommentDto();
		commentDto.setId(1L);
		commentDto.setPostId(1L);
		//comment.setName("joe");
		commentDto.setEmail("mail@mail.com");
		commentDto.setBody("testing");
		return commentDto;
	}
	
	public Comment createExpectedComment2() {
		Comment comment = new Comment();
		comment.setId(2L);
		comment.setPostId(2L);
		comment.setName("max");
		comment.setEmail("max@mail.com");
		comment.setBody("testing2");
		return comment;
	}
	
	public CommentDto createExpectedCommentDto2() {
		//public CommentDto(Long id, Long postId, String name, String email, String body) {
		CommentDto commentDto = new CommentDto(2L, 2L, "max", "max@mail.com","testing2");
		return commentDto;
	}
	
	public List<Comment> getExpectedCommentList(){
		List<Comment> expectedCommentList = new ArrayList<>();
		expectedCommentList.add(createExpectedComment());
		expectedCommentList.add(createExpectedComment2());
		return expectedCommentList;
	}
	
	public List<CommentDto> getExpectedCommentDtoList(){
		List<CommentDto> expectedCommentDtoList = new ArrayList<>();
		expectedCommentDtoList.add(createExpectedCommentDto());
		expectedCommentDtoList.add(createExpectedCommentDto2());
		return expectedCommentDtoList;
	}
	
	//----------
	
	
	public Comment createExpectedExclComment2() {
		Comment comment = new Comment();
		comment.setId(2L);
		comment.setPostId(2L);
		//comment.setName("joe");
		comment.setEmail("max@mail.com");
		comment.setBody("testing2");
		return comment;
	}
	
	public CommentDto createExpectedExclCommentDto2() {
		CommentDto commentDto = new CommentDto();
		commentDto.setId(2L);
		commentDto.setPostId(2L);
		//comment.setName("joe");
		commentDto.setEmail("max@mail.com");
		commentDto.setBody("testing2");
		return commentDto;
	}
	
	public List<Comment> getExpectedExclCommentList(){
		List<Comment> expectedCommentExclList = new ArrayList<>();
		expectedCommentExclList.add(createExpectedExclComment());
		expectedCommentExclList.add(createExpectedExclComment2());
		return expectedCommentExclList;
	}
	
	public List<CommentDto> getExpectedExclCommentDtoList(){
		List<CommentDto> expectedExclCommentDtoList = new ArrayList<>();
		expectedExclCommentDtoList.add(createExpectedExclCommentDto());
		expectedExclCommentDtoList.add(createExpectedExclCommentDto2());
		return expectedExclCommentDtoList;
	}

}
