package io.filemanagement.controllers;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.filemanagement.dtos.CommentDto;
import io.filemanagement.dtos.PostDto;
import io.filemanagement.exceptions.FileManagementNotFoundException;
import io.filemanagement.exceptions.FileManagementRequestException;
import io.filemanagement.models.Comment;
import io.filemanagement.services.CommentService;
import io.filemanagement.services.PostService;
import io.filemanagement.thirdparty.abstraction.IRestFunctions;
import io.filemanagement.utils.FileManagmentEntityDtoMappingService;


@RestController
@Validated
@RequestMapping
public class CommentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

	@Autowired
	private CommentService commentService;

	@Autowired
	@Qualifier("CommentPlaceHolder")
	IRestFunctions<CommentDto> placeHolderService;

	@Autowired
	FileManagmentEntityDtoMappingService<Comment, CommentDto> mappingService;

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "document/posts/{postId}/comment/{commentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<CommentDto> getComment(@PathVariable("postId") Long postId,
			@PathVariable("commentId") Long commentId) throws FileManagementNotFoundException {
		try {
			Comment comment = commentService.getCommentByPostIdAndId(commentId, postId);
			if (comment == null) {
				return new ResponseEntity<CommentDto>(HttpStatus.NOT_FOUND);
			}
			CommentDto commentDto = new CommentDto();
			commentDto = mappingService.toDto(commentDto, comment);
			return new ResponseEntity<CommentDto>(commentDto, HttpStatus.OK);
		} catch (NullPointerException fne) {
			LOGGER.error("HTTP Status 404 returned");
			throw new FileManagementNotFoundException("Related entity / entity does not exist");
		} catch (FileManagementRequestException fre) {
			LOGGER.error("HTTP Status 400 returned");
			throw fre;
		} catch (Exception e) {
			LOGGER.error("HTTP Status 500 returned - unexpected error occured");
			e.printStackTrace();
			return new ResponseEntity<CommentDto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "document/posts/{postId}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<List<CommentDto>> getComments(@PathVariable("postId") Long postId) throws FileManagementNotFoundException {
		try {
			List<Comment> comments = commentService.getCommentsByPostId(postId);
			if ((comments == null) || (comments.isEmpty())) {
				return new ResponseEntity<List<CommentDto>>(HttpStatus.NOT_FOUND);
			}
			List<CommentDto> commentDtos = mappingService.toDtoList(comments, new CommentDto());
			return new ResponseEntity<List<CommentDto>>(commentDtos, HttpStatus.OK);
		} catch (NullPointerException fne) {
			LOGGER.error("HTTP Status 404 returned");
			throw new FileManagementNotFoundException("Related entity / entity does not exist");
		} catch (FileManagementRequestException fre) {
			LOGGER.error("HTTP Status 400 returned");
			throw fre;
		} catch (Exception e) {
			LOGGER.error("HTTP Status 500 returned - unexpected error occured");
			e.printStackTrace();
			return new ResponseEntity<List<CommentDto>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/document/posts/{postId}/comments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto,
			@PathVariable("postId") Long postId) throws FileManagementNotFoundException {
		try {
			LOGGER.info("CommentDto : " + commentDto);
			Comment comment = new Comment();
			//comment.setPostId(postId);
			commentDto.setPostId(postId);
			comment = mappingService.fromDto(commentDto, comment);
			Comment createdComment = commentService.save(comment);
			CommentDto createdCommentDto = mappingService.toDto(new CommentDto(), createdComment);
			return placeHolderService.post(createdCommentDto);
			//return new ResponseEntity<CommentDto>(createdCommentDto,HttpStatus.CREATED);
		} catch (NullPointerException fne) {
			LOGGER.error("HTTP Status 404 returned");
			throw new FileManagementNotFoundException("Related entity / entity does not exist");
		} catch (FileManagementRequestException fre) {
			LOGGER.error("HTTP Status 400 returned");
			throw fre;
		} catch (FileManagementNotFoundException fne) {
			LOGGER.error("HTTP Status 404 returned");
			throw fne;
		} catch (Exception e) {
			LOGGER.error("HTTP Status 500 returned - unexpected error occured");
			e.printStackTrace();
			return new ResponseEntity<CommentDto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
