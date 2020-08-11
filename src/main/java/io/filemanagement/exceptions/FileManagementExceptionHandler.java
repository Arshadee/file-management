package io.filemanagement.exceptions;

import java.sql.SQLIntegrityConstraintViolationException;
//import org.springframework.validation.BindException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FileManagementExceptionHandler {

	@ExceptionHandler(value = { FileManagementRequestException.class, ConstraintViolationException.class, SQLIntegrityConstraintViolationException.class})
	public ResponseEntity<Object> handleFileManagementRequestException(FileManagementRequestException e) {
		HttpStatus badRequest = HttpStatus.BAD_REQUEST;
		FileManagementException fileManagementException = new FileManagementException(e.getMessage(), e, badRequest, ZonedDateTime.now(ZoneId.of("Z")));
		return new ResponseEntity<>(fileManagementException, badRequest);
	}

	@ExceptionHandler(value = { FileManagementNotFoundException.class, NullPointerException.class })
	public ResponseEntity<Object> handleFileManagementNotFoundException(FileManagementNotFoundException e) {
		HttpStatus notFound = HttpStatus.NOT_FOUND;
		FileManagementException fileManagementException = new FileManagementException(e.getMessage(), e, notFound, ZonedDateTime.now(ZoneId.of("Z")));
		return new ResponseEntity<>(fileManagementException, notFound);
	}
	
	@ExceptionHandler(value = { FileManagementIOException.class })
	public ResponseEntity<Object> handleFileManagementNotFoundException(FileManagementIOException e) {
		HttpStatus expectationFailed = HttpStatus.EXPECTATION_FAILED;
		FileManagementException fileManagementException = new FileManagementException(e.getMessage(), e, expectationFailed, ZonedDateTime.now(ZoneId.of("Z")));
		return new ResponseEntity<>(fileManagementException, expectationFailed);
	}
	
	@ExceptionHandler(value = { FileManagementLockedException.class })
	public ResponseEntity<Object> handledFileManagementLockedException(FileManagementLockedException e) {
		HttpStatus locked = HttpStatus.LOCKED;
		FileManagementException fileManagementException = new FileManagementException(e.getMessage(), e, locked, ZonedDateTime.now(ZoneId.of("Z")));
		return new ResponseEntity<>(fileManagementException, locked);
	}	
	
	@ExceptionHandler(value = { FileManagementExtServerDownException.class })
	public ResponseEntity<Object> handledFileManagementExtServerDownException(FileManagementExtServerDownException e) {
		HttpStatus serviceUnavailable = HttpStatus.SERVICE_UNAVAILABLE;
		FileManagementException fileManagementException = new FileManagementException(e.getMessage(), e, serviceUnavailable, ZonedDateTime.now(ZoneId.of("Z")));
		return new ResponseEntity<>(fileManagementException, serviceUnavailable);
	}
	
	@ExceptionHandler(value = { FileManagementExtBandWidthException.class })
	public ResponseEntity<Object> handledFileManagementExtBandwidthException(FileManagementExtBandWidthException e) {
		HttpStatus bandWidth = HttpStatus.BANDWIDTH_LIMIT_EXCEEDED;
		FileManagementException fileManagementException = new FileManagementException(e.getMessage(), e, bandWidth, ZonedDateTime.now(ZoneId.of("Z")));
		return new ResponseEntity<>(fileManagementException, bandWidth);
	}
	
	@ExceptionHandler(value = { FileManagementExtGeneralException.class })
	public ResponseEntity<Object> handledFileManagementExtGeneralException(FileManagementExtGeneralException e) {
		HttpStatus serverError = HttpStatus.INTERNAL_SERVER_ERROR;
		FileManagementException fileManagementException = new FileManagementException(e.getMessage(), e, serverError, ZonedDateTime.now(ZoneId.of("Z")));
		return new ResponseEntity<>(fileManagementException, serverError);
	}
}
