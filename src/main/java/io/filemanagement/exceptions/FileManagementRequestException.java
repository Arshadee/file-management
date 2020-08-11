package io.filemanagement.exceptions;

public class FileManagementRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3889338675113143722L;

	public FileManagementRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileManagementRequestException(String message) {
		super(message);
	}

}
