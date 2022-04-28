package com.barclays.utility;

import java.time.LocalDateTime;

/** Class to encapsulate relevant failure information in one place.
 * Used to display exception message in a well formatted and user
 * friendly manner.
 * 
 * 
 * @author Shashank Singh (shashank9830@gmail.com)
 *
 */
public class ErrorInfo {
	
	private String errorMessage;
	private Integer errorCode;
	private LocalDateTime timestamp;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

}
