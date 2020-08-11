package io.filemanagement.exceptions;

public class FileManagementExtServerDownException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileManagementExtServerDownException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileManagementExtServerDownException(String message) {
		super(message);
	}

}
