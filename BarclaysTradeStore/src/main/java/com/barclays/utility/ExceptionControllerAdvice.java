package com.barclays.utility;

import java.time.LocalDateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.barclays.exception.BarclaysTradeStoreException;

/** Exception handling using Aspect Oriented Programming (AOP).
 * Handles exceptions for the REST Controller.
 * 
 * 
 * @author Shashank Singh (shashank9830@gmail.com)
 *
 */
@RestControllerAdvice
public class ExceptionControllerAdvice {
	
	@Autowired
	Environment environment;

	/** Exception handler for generic Exception.
	 * 
	 * 
	 * @param exception -- exception of type Exception.
	 * @return -- HTTP Response containing appropriate exception info. 
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorInfo> exceptionHandler(Exception exception) {
		
		ErrorInfo error = new ErrorInfo();
		error.setErrorMessage(environment.getProperty("General.EXCEPTION_MESSAGE"));
		error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		error.setTimestamp(LocalDateTime.now());
		
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/** Exception handler for BarclaysTradeStoreException.
	 * 
	 * 
	 * @param exception -- exception of type Exception.
	 * @return -- HTTP Response containing appropriate exception info.
	 */
	@ExceptionHandler(BarclaysTradeStoreException.class)
	public ResponseEntity<ErrorInfo> barclaysTradeStoreExceptionHandler(BarclaysTradeStoreException exception) {
		
		ErrorInfo error = new ErrorInfo();
		error.setErrorMessage(environment.getProperty(exception.getMessage()));
		error.setTimestamp(LocalDateTime.now());
		error.setErrorCode(HttpStatus.BAD_REQUEST.value());
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}
