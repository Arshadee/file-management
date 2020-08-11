package io.filemanagement.exceptions;

public class FileManagementNotFoundException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileManagementNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileManagementNotFoundException(String message) {
		super(message);
	}

}
