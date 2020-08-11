package io.filemanagement.validations;

import org.springframework.stereotype.Component;

import io.filemanagement.exceptions.FileManagementNotFoundException;

@Component
public class ValidateNotNull  implements IValidator<Object> {

	@Override
	public void validate(Object t, String errorMessage) throws FileManagementNotFoundException {
		if(t==null)
			throw new FileManagementNotFoundException(errorMessage);
		
	}

}
