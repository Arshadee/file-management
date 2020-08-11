package io.filemanagement.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import io.filemanagement.dtos.FileInfoDto;
import io.filemanagement.dtos.ResponseMessageDto;
import io.filemanagement.exceptions.FileManagementException;
import io.filemanagement.exceptions.FileManagementIOException;
import io.filemanagement.exceptions.FileManagementLockedException;
import io.filemanagement.exceptions.FileManagementNotFoundException;
import io.filemanagement.exceptions.FileManagementRequestException;
import io.filemanagement.services.DocumentService;

@RestController
@RequestMapping
public class DocumentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

	@Autowired
	DocumentService storageService;

	@PostMapping("/createdir/{username}")
	public ResponseEntity<String> createFolder(@PathVariable String username) {
		String message = "";
		try {
			
			storageService.createDirPath(username);
			return new ResponseEntity<String>("created", HttpStatus.CREATED);
			
		} catch(FileManagementIOException ioe) {
			
			ioe.printStackTrace();
			LOGGER.info("HTTP Status 417 returned");
			throw ioe;
			
		} catch (Exception e) {
			
			e.printStackTrace();
			message = "Could not create folder for : " + username + "!";
			LOGGER.info(message);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		
		}

	}

	@PostMapping("/upload/{username}")
	public ResponseEntity<ResponseMessageDto> uploadFile(@RequestParam("file") MultipartFile file,
			@PathVariable String username) {
		String message = "";
		try {
			
			storageService.save(file, username);
			message = "Uploaded the file successfully: " + file.getOriginalFilename();
			return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessageDto(message));
		
		} catch (FileManagementRequestException are) {
			
			LOGGER.error("HTTP Status 400 returned");
			throw are;
			
		} catch (FileManagementIOException	ioe) {
			
			LOGGER.error("HTTP Status 417 returned");
			throw ioe;
			
		} catch (Exception e) {
			
			LOGGER.error("HTTP Status 500 returned - unexpected error occured");
			e.printStackTrace();
			return new ResponseEntity<ResponseMessageDto>(HttpStatus.INTERNAL_SERVER_ERROR);
		
		}
	}

	@GetMapping("/files/{username}")
	public ResponseEntity<List<FileInfoDto>> getListFiles(@PathVariable String username) throws Exception {
		try {
		List<FileInfoDto> fileInfos = storageService.loadAllUserDocsFileSystem(username).map(path -> {
			String filename = path.getFileName().toString();

			Object[] params = { path.getFileName().toString(), username };
			String url = MvcUriComponentsBuilder.fromMethodName(DocumentController.class, "getFile", params).build()
					.toString();

			return new FileInfoDto(filename, url);
		}).filter(f -> f.getName().endsWith("pdf")).collect(Collectors.toList());

		return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
		}catch (FileManagementNotFoundException ane) {
			LOGGER.error("HTTP Status 404 returned");
			throw ane;
			//return new ResponseEntity<List<FileInfoDto>>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/file/{username}/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename, @PathVariable String username) throws FileManagementNotFoundException , Exception{
		
		Resource file;
		try {
			
			file = storageService.loadUserDocFileSystem(filename, username);
			
		}catch (FileManagementNotFoundException ane) {
					LOGGER.error("HTTP Status 404 returned");
					//throw ane;
					return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
				} 
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@DeleteMapping("/upload/{username}/{fileName}")
	public ResponseEntity<String> deleteFile(@PathVariable String username, @PathVariable String fileName)
	throws FileManagementException {
		String deleteMessage = "deleted";
		try {
			
			storageService.deleteFile(fileName, username);
			
		}catch (FileManagementRequestException fre) {
			
			LOGGER.error("HTTP Status 400 returned");
			throw fre; 
			
		}catch (FileManagementLockedException fle) {
			
			LOGGER.error("HTTP Status 423 returned");
			throw fle;
			
		}catch (Exception e) {
			
			LOGGER.error("HTTP Status 500 returned - unexpected error occured");
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		return new ResponseEntity<String>(deleteMessage, HttpStatus.OK);

	}

	@DeleteMapping("/upload/{username}")
	public ResponseEntity<String> deleteAllFiles(@PathVariable String username) throws FileManagementException {
		String deleteMessage = "deleted";
		try {
			
			storageService.deleteAll(username);
			
		}catch (FileManagementRequestException are) {
			
			LOGGER.error("HTTP Status 400 returned");
			throw are;
			
		} catch (FileManagementIOException	ioe) {
			
			LOGGER.error("HTTP Status 417 returned");
			throw ioe;
			
		}catch (FileManagementLockedException fle) {
			
			LOGGER.error("HTTP Status 423 returned");
			throw fle;
			
		} catch (Exception e) {
			
			LOGGER.error("HTTP Status 500 returned - unexpected error occured");
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
       
		}
		return new ResponseEntity<String>(deleteMessage, HttpStatus.OK);

	}

}
