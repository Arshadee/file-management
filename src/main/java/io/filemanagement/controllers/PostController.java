package io.filemanagement.controllers;

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

import io.filemanagement.dtos.PostDto;
import io.filemanagement.exceptions.FileManagementNotFoundException;
import io.filemanagement.exceptions.FileManagementRequestException;
import io.filemanagement.models.Document;
import io.filemanagement.models.Post;
import io.filemanagement.services.DocumentService;
import io.filemanagement.services.PostService;
import io.filemanagement.thirdparty.abstraction.IRestFunctions;
import io.filemanagement.utils.FileManagmentEntityDtoMappingService;

@RestController
@Validated
@RequestMapping
public class PostController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

	@Autowired
	private PostService postService;

	@Autowired
	private DocumentService documentService;

	@Autowired
	@Qualifier("PostPlaceHolder")
	IRestFunctions<PostDto> placeHolderService;

	@Autowired
	FileManagmentEntityDtoMappingService<Post, PostDto> mappingService;

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/username/{username}/document/{documentName}/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<PostDto> getPost(@PathVariable("username") String username,
			@PathVariable("documentName") String documentName) throws FileManagementNotFoundException {
		try {
			LOGGER.info("ctrl getPost documentName " + documentName);
			Post post = postService.getPost(documentName);
			if (post == null) {
				return new ResponseEntity<PostDto>(HttpStatus.NOT_FOUND);
			}
			PostDto postDto = new PostDto();
			postDto = mappingService.toDto(postDto, post);
			postDto.setUsername(username);
			return new ResponseEntity<PostDto>(postDto, HttpStatus.OK);
		} catch (NullPointerException fne) {
			LOGGER.error("HTTP Status 404 returned");
			throw new FileManagementNotFoundException("Related entity / entity does not exist");
		} catch (FileManagementRequestException fre) {
			LOGGER.error("HTTP Status 400 returned");
			throw fre;
		} catch (Exception e) {
			LOGGER.error("HTTP Status 500 returned - unexpected error occured");
			e.printStackTrace();
			return new ResponseEntity<PostDto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/username/{username}/document/{documentName}/posts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto, @PathVariable("username") String username, @PathVariable("documentName") String documentName)
			throws FileManagementNotFoundException {
		try {
			LOGGER.info("PostDto : " + postDto);
			Document document = documentService.getDocumentByUsernameAndFileName(username, documentName);
			Post post = new Post(document.getId(), username, documentName, postDto.getBody());
			Post createdPost = postService.save(post);
			document.setPost(createdPost);
			documentService.update(document);
			PostDto createdPostDto = mappingService.toDto(new PostDto(), createdPost);
			return placeHolderService.post(createdPostDto);
		} catch (NullPointerException fne) {
			LOGGER.error("HTTP Status 404 returned");
			throw new FileManagementNotFoundException("Related entity / entity does not exist");
		} catch (FileManagementRequestException fre) {
			LOGGER.error("HTTP Status 400 returned");
			throw fre;
		} catch (Exception e) {
			LOGGER.error("HTTP Status 500 returned - unexpected error occured");
			e.printStackTrace();
			return new ResponseEntity<PostDto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
