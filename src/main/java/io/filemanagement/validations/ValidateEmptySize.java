package io.filemanagement.validations;

import org.springframework.stereotype.Component;

import io.filemanagement.exceptions.FileManagementNotFoundException;

@Component
public class ValidateEmptySize implements IValidator<Integer>{

	
	@Override
	public void validate(Integer t, String errorMessage) throws FileManagementNotFoundException {
		if (t<=0)
			throw new FileManagementNotFoundException(errorMessage);
	}

}
