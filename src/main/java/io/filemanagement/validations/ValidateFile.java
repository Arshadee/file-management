package io.filemanagement.validations;

import org.springframework.stereotype.Component;

import io.filemanagement.exceptions.FileManagementRequestException;

@Component
public class ValidateFile implements IValidator<String> {

	@Override
	public void validate(String s, String errorMessage) throws FileManagementRequestException {
		if ((s == null) || (s.isEmpty()) || (!s.toLowerCase().endsWith(".pdf")))
			throw new FileManagementRequestException(errorMessage);
	}

}
