package io.filemanagement.exceptions;

public class FileManagementIOException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileManagementIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileManagementIOException(String message) {
		super(message);
	}

}
