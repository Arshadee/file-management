package io.filemanagement.validations;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import io.filemanagement.controllers.PostController;
import io.filemanagement.exceptions.FileManagementLockedException;
import io.filemanagement.exceptions.FileManagementRequestException;

@Component
public class ValidateFileLocked implements IValidator<Resource> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidateFileLocked.class);

	
	@Override
	public void validate(Resource r, String errorMessage) throws FileManagementLockedException {
		boolean fileIsNotLocked;
		try {
			fileIsNotLocked = r.getFile().renameTo(r.getFile());
			LOGGER.info("fileIsNotLocked "+fileIsNotLocked);
		} catch (IOException e) {
			throw new FileManagementRequestException(e.getMessage(), e);
		}
		
		if (!fileIsNotLocked && r.exists())
			throw new FileManagementLockedException(errorMessage);

	}

}
