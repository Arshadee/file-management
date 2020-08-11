package io.filemanagement.validations;

@FunctionalInterface
public interface IValidator<T> {
	
	public void validate(T t, String errorMessage) throws Exception;

}
