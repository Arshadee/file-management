package io.filemanagement.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.filemanagement.exceptions.FileManagementNotFoundException;
import io.filemanagement.exceptions.FileManagementRequestException;

@SpringBootTest
public class DocumentServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceTest.class);

	@Autowired
	private ApplicationContext context;

	@InjectMocks
	private DocumentService service;

	@Autowired
	ResourceLoader resourceLoader;

	@Value("${root}")
	private String root;

	private final String username = "MockUser";

	private final String docName1 = "Scan0001.pdf";

	private final String docName2 = "Scan0002.pdf";

	@AfterEach
	public void deleteFolder() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		service.deleteAll(username);
	}

	@Test
	public void testServiceCreateDirectory() {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		try {
			service.createDirPath(username);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String userDirPath = service.getRoot() + "/" + username;
		//Path path = Paths.get(userDirPath);
		File directory = new File(userDirPath);

		Boolean expected = true;
		Boolean actual = directory.exists();
		Assertions.assertEquals(expected, actual);

	}

	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testSaveSuccess() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		// Create File from testPdf in /Resource
		// Resource resource = resourceLoader.getResource("classpath:Scan0001.pdf");
		Resource resource = resourceLoader.getResource("classpath:" + docName1);
		File file = resource.getFile();
		addDocumentForUser(username, docName1, file);
		String userDirPath = service.getRoot() + "/" + username + "/" + file.getName();
		//Path path = Paths.get(userDirPath);
		File fileUploaded = new File(userDirPath);

		// assert(check file sys)
		Boolean expected = true;
		Boolean actual = fileUploaded.exists();
		Assertions.assertEquals(expected, actual);

		// assert(check db)
		List<String> documentPaths = service.loadAllUserDocsDatabase(username);
//		documentPaths.forEach(d -> {
//			System.out.println(d);
//
//		});

		Boolean expectedDb = true;
		Boolean actualDb = documentPaths.contains(root + "/" + username + "/" + file.getName());

		Assertions.assertEquals(expectedDb, actualDb);
	}

	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testSaveIErrorIncorrectFileFormat() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		String expectedErrorMessage = "Incorrect format or null must be a pdf file!";

		String fileIncorrectFormat = "test.rtf";
		Resource resource = resourceLoader.getResource("classpath:" + fileIncorrectFormat);
		File file = resource.getFile();
		try {
			addDocumentForUser(username, fileIncorrectFormat, file);
		} catch (FileManagementRequestException e) {
			assertThat(e.getMessage()).isEqualTo(expectedErrorMessage);
		}

	}
	
	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testSaveIErrorBlankFile() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		String expectedErrorMessage = "Incorrect format or null must be a pdf file!";

		String fileIncorrectFormat = "";
		Resource resource = resourceLoader.getResource("classpath:" + fileIncorrectFormat);
		File file = resource.getFile();
		try {
			addDocumentForUser(username, fileIncorrectFormat, file);
		} catch (FileManagementRequestException e) {
			assertThat(e.getMessage()).isEqualTo(expectedErrorMessage);
		}

	}

	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testLoadAllUserDocsFileSystemSuccess() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		addDocumentForUser(username, docName1);
		addDocumentForUser(username, docName2);
		String[] docArray = { docName1, docName2 };
		List<String> toSendDocs = Arrays.asList(docArray);
		List<Path> returnedDocsPaths = service.loadAllUserDocsFileSystem(username).collect(Collectors.toList());
		List<String> returnedDocs = returnedDocsPaths.stream().map(d -> d.getFileName().toString())
				.collect(Collectors.toList());
		Collections.sort(toSendDocs);
		Collections.sort(returnedDocs);
		Boolean actual = toSendDocs.equals(returnedDocs);
		Boolean expected = true;
		Assertions.assertEquals(expected, actual);

	}
	
	@SuppressWarnings("unused")
	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testLoadAllUserDocsFileSystemUserExistNoFiles() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		String expectedErrorMessage = "File not found";
	    try {
		List<Path> returnedDocsPaths = service.loadAllUserDocsFileSystem(username).collect(Collectors.toList());
		List<String> returnedDocs = returnedDocsPaths.stream().map(d -> d.getFileName().toString())
				.collect(Collectors.toList());
	    } catch (FileManagementNotFoundException e) {
			assertThat(e.getMessage()).isEqualTo(expectedErrorMessage);
		}
	}
	
	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void loadUserDocFileSystemSuccess() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		Resource resource = resourceLoader.getResource("classpath:" + docName1);
		File file = resource.getFile();
		addDocumentForUser(username, docName1, file);
		Resource resourceResult = service.loadUserDocFileSystem(docName1, username);
		File fileResult = resourceResult.getFile();
		Boolean actual = FileUtils.contentEquals(file, fileResult);
		Boolean expected = true;
		Assertions.assertEquals(expected, actual);
	}

	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testLoadUserDocFileSystemError() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		Resource resource = resourceLoader.getResource("classpath:" + docName1);
		File file = resource.getFile();
		Resource resource2 = resourceLoader.getResource("classpath:" + docName2);
		File file2 = resource2.getFile();
		addDocumentForUser(username, docName1, file);
		Resource resourceResult = service.loadUserDocFileSystem(docName1, username);
		File fileResult = resourceResult.getFile();
		Boolean actual = FileUtils.contentEquals(file2, fileResult);
		Boolean expected = false;
		Assertions.assertEquals(expected, actual);
	}

	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void loadUserDocFileSystemErrorFileNotExist() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		String fileNotExist = "noFile.pdf";
		String expectedErrorMessage = "File not found";

		try {
			service.loadUserDocFileSystem(fileNotExist, username);
		} catch (FileManagementNotFoundException e) {
			assertThat(e.getMessage()).isEqualTo(expectedErrorMessage);
		}
	}
	
	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void loadUserDocFileSystemErrorUserNotExist() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		String noUser = "noUser";
		String expectedErrorMessage = "File not found";

		try {
			//addDocumentForUser(username, fileNotExist);
			service.loadUserDocFileSystem(docName1, noUser);
		} catch (FileManagementNotFoundException e) {
			assertThat(e.getMessage()).isEqualTo(expectedErrorMessage);
		}
	}

	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testDeleteAllSuccess() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		addDocumentForUser(username, docName1);
		addDocumentForUser(username, docName2);
		String userDirPath = service.getRoot() + "/" + username;
		File directory = new File(userDirPath);

		Boolean actualBefore = directory.exists();
		Boolean expectedBefore = true;
		Assertions.assertEquals(expectedBefore, actualBefore);

		service.deleteAll(username);

		Boolean actualAfter = directory.exists();
		Boolean expectedAfter = false;
		Assertions.assertEquals(expectedAfter, actualAfter);
	}

	@Test
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void testdeleteFileSuccess() throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		addDocumentForUser(username, docName1);

		String userFilePath = service.getRoot() + "/" + username + "/" + docName1;
		File file = new File(userFilePath);

		Boolean actualBefore = file.exists();
		Boolean expectedBefore = true;
		Assertions.assertEquals(expectedBefore, actualBefore);

		service.deleteFile(docName1, username);

		Boolean actualAfter = file.exists();
		Boolean expectedAfter = false;
		Assertions.assertEquals(expectedAfter, actualAfter);
	}

	private void addDocumentForUser(String username, String docName) throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		Resource resource = resourceLoader.getResource("classpath:" + docName);
		File file = resource.getFile();
		addDocumentForUser(username, docName, file);
	}

	private void addDocumentForUser(String username, String docName, File file) throws Exception {
		this.context.getAutowireCapableBeanFactory().autowireBean(this.service);
		try {
			MultipartFile multipartFile = new MockMultipartFile(file.getName(), new FileInputStream(file));
			service.save(multipartFile, username);
		} catch (IOException e) {
           LOGGER.debug(e.getMessage());
		}
	}

}
