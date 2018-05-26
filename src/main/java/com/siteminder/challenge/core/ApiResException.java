package com.siteminder.challenge.core;

import org.springframework.http.HttpStatus;

/*
 * Custom exception to throw error responses.
 */
public class ApiResException extends Exception {
	// To satisfy the compiler expectation
	private static final long serialVersionUID = 1L;
	
	private HttpStatus status;
	private String message;
	
	public ApiResException(HttpStatus status, String message) {
		super(message);
		this.message = message;
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}	
}
