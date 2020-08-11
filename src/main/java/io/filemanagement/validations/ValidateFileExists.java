package io.filemanagement.validations;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import io.filemanagement.exceptions.FileManagementNotFoundException;

@Component
public class ValidateFileExists implements IValidator<Resource> {

	@Override
	public void validate(Resource r, String errorMessage) throws FileManagementNotFoundException {
		if (!r.exists())
			throw new FileManagementNotFoundException(errorMessage);

	}

}
