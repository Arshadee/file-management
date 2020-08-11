package io.filemanagement.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import io.filemanagement.exceptions.FileManagementIOException;
import io.filemanagement.exceptions.FileManagementRequestException;
import io.filemanagement.models.Document;
import io.filemanagement.models.User;
import io.filemanagement.repository.DocumentRepository;
import io.filemanagement.repository.UserRepository;
import io.filemanagement.validations.IValidator;

@Service
public class DocumentService implements Constants{

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	@Qualifier("validateFile")
	private IValidator<String> fileNameValidator;

	@Autowired
	@Qualifier("validateFileExists")
	private IValidator<Resource> fileExistValidator;

	@Autowired
	@Qualifier("validateFileLocked")
	private IValidator<Resource> fileLockedValidator;

	@Autowired
	@Qualifier("validateEmptySize")
	private IValidator<Integer> emptySizeValidator;

	@Value("${root}")
	private String root;

	@Value("${acceptedFileType}")
	private String acceptedFileType;

	public void createDirPath(String username) throws FileManagementIOException, Exception {
		String userDirPath = root + "/" + username;
		try {
			createDir(root);
			createDir(userDirPath);
		} catch (IOException e) {
			LOGGER.info("Expectation Failed cannot create folder " + e.getMessage());
			throw new FileManagementIOException(e.getMessage(), e);
		}
	}

	private void createDir(String dir) throws IOException {
		Path path = Paths.get(dir);
		File directory = new File(dir);
		if (!directory.exists())
			Files.createDirectory(path);
	}

	public String getRoot() {
		return root;
	}

	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void save(MultipartFile file, String username)
			throws FileManagementIOException, FileManagementRequestException, Exception {
		try {
			String userDirPath = root + "/" + username;
			Path uploadPath = Paths.get(userDirPath);

			User user = userRepository.findByUsername(username);
			if (user == null)
				user = new User(username);

			String fileName = ((file.getOriginalFilename() == null) || (file.getOriginalFilename().isEmpty()))
					? file.getName()
					: file.getOriginalFilename();

			fileNameValidator.validate(fileName, INCORRECT_FORMAT_OR_NULL_ERROR_MESSAGE);

			Document document = new Document(user, userDirPath + "/" + fileName);
			user.getDocuments().add(document);
			userRepository.save(user);

			createDirPath(username);
			Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

		} catch (DataIntegrityViolationException de) {

			LOGGER.info("Bad Request - User and Document data cannot be saved to DB " + de.getMessage());
			throw new FileManagementRequestException(de.getMessage(), de);

		} catch (IOException e) {

			LOGGER.info("Expectation Failed - cannot save file " + e.getMessage());
			throw new FileManagementIOException(e.getMessage(), e);

		}

	}

	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public Document update(Document document) throws FileManagementRequestException, Exception {
		Document documentResult = null;
		try {
			documentResult =  documentRepository.save(document);
		} catch (DataIntegrityViolationException de) {

			LOGGER.info("Bad Request - User and Document data cannot be saved to DB " + de.getMessage());
			throw new FileManagementRequestException(de.getMessage(), de);

		}
		return documentResult;
	}

	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteAll(String username) throws FileManagementRequestException, Exception {
		try {

			String userDirPath = root + "/" + username;
			Path uploadPath = Paths.get(userDirPath);

			Resource resource = new UrlResource(uploadPath.toUri());
			fileLockedValidator.validate(resource,FILE_LOCKED_ERROR_MESSAGE);

			User user = userRepository.findByUsername(username);
			documentRepository.deleteByUser(user);
			FileSystemUtils.deleteRecursively(uploadPath.toFile());

		} catch (DataIntegrityViolationException de) {

			LOGGER.info("Bad Request - User's document data cannot be deleted from DB " + de.getMessage());
			throw new FileManagementRequestException(de.getMessage(), de);

		}
	}

	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteFile(String fileName, String username) throws FileManagementRequestException, Exception {
		try {

			String userDirPath = root + "/" + username;
			String filePath = userDirPath + "/" + fileName;

			Path uploadPath = Paths.get(userDirPath);
			Path file = uploadPath.resolve(fileName);
			Resource resource = new UrlResource(file.toUri());
			fileLockedValidator.validate(resource, FILE_LOCKED_ERROR_MESSAGE);

			documentRepository.deleteByFilePath(filePath);
			FileSystemUtils.deleteRecursively(file.toFile());

		} catch (DataIntegrityViolationException de) {

			LOGGER.info("Bad Request - User's document data cannot be deleted from DB " + de.getMessage());
			throw new FileManagementRequestException(de.getMessage(), de);

		} catch (MalformedURLException e) {

			LOGGER.info("Bad Request malformed url " + e.getMessage());
			throw new FileManagementRequestException(e.getMessage(), e);
		}
	}

	public Resource loadUserDocFileSystem(String fileName, String username) throws Exception {
		try {

			Path uploadPath = Paths.get(root + "/" + username);
			Path file = uploadPath.resolve(fileName);

			Resource resource = new UrlResource(file.toUri());
			fileExistValidator.validate(resource, FILE_NOT_FOUND_ERROR_MESSAGE);

			return resource;

		} catch (MalformedURLException e) {

			LOGGER.info("Bad Request malformed url " + e.getMessage());
			throw new FileManagementRequestException(e.getMessage(), e);

		}
	}

	public Stream<Path> loadAllUserDocsFileSystem(String username) throws Exception {
		try {

			Path uploadPath = Paths.get(root + "/" + username);
			Resource resource = new UrlResource(uploadPath.toUri());
			fileExistValidator.validate(resource,FILE_NOT_FOUND_ERROR_MESSAGE);
			List<Path> pathList = Files.walk(uploadPath, 1).filter(path -> !path.equals(uploadPath))
					.filter(path -> path.getFileName().toString().endsWith("pdf")).map(uploadPath::relativize)
					.collect(Collectors.toList());

			emptySizeValidator.validate(pathList.size(), NO_ITEMS_FOUND_ERROR_MESSAGE);
			return Files.walk(uploadPath, 1).filter(path -> !path.equals(uploadPath)).map(uploadPath::relativize);

		} catch (IOException e) {

			LOGGER.info("Bad Request cannot read file");
			throw new FileManagementRequestException("Could not read the file!");

		}
	}

	public List<String> loadAllUserDocsDatabase(String username) throws Exception {
		User user = userRepository.findByUsername(username);
		List<Document> documents = documentRepository.findByUser(user);
		List<String> documentPaths = documents.stream().map(d -> d.getFilePath()).collect(Collectors.toList());
		return documentPaths;

	}

	public Document getDocumentByUsernameAndFileName(String username, String fileName) {
		String filePath = getFilePathByFileName(username, fileName);
		//User user = userRepository.findByUsername(username);
		//Document document = documentRepository.findByUserAndFilePathIgnoreCase(user, filePath);
		Document document = documentRepository.findByUsernameAndFilePath(username, filePath);
		return document;
		// return document.getId();

	}
	
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteAllUsers() {
		userRepository.deleteAll();
	}
	
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteUser(User user) {
		userRepository.delete(user);
	}

	private String getFilePathByFileName(String username, String fileName) {
		String fullFileName = fileName.toLowerCase().endsWith(acceptedFileType) ? fileName
				: fileName + acceptedFileType;
		return root + "/" + username + "/" + fullFileName;
	}

}
