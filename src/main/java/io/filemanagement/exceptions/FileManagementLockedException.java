package io.filemanagement.exceptions;

public class FileManagementLockedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileManagementLockedException(String message, Throwable cause) {
			super(message, cause);
		}

	public FileManagementLockedException(String message) {
			super(message);
		}

}
