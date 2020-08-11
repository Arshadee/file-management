package io.filemanagement.exceptions;

public class FileManagementExtGeneralException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FileManagementExtGeneralException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileManagementExtGeneralException(String message) {
		super(message);
	}

}
