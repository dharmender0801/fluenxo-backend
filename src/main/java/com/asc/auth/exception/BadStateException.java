package com.asc.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BadStateException extends RuntimeException {
	private static final long serialVersionUID = -5962307187118538361L;

	public BadStateException(String message) {
		super(message);
	}

	public BadStateException(String message, Throwable cause) {
		super(message, cause);
	}
}
