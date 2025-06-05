package com.asc.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class OAuth2AuthenticationProcessingException extends AuthenticationException {
	private static final long serialVersionUID = -4242943186154915344L;
	private String resourceName;
	private String fieldName;
	private Object fieldValue;

	/*
	 * public OAuth2AuthenticationProcessingException(String msg, Throwable t) {
	 * super(msg, t); }
	 */

	public OAuth2AuthenticationProcessingException(String resourceName, String fieldName, Object fieldValue) {
		super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public OAuth2AuthenticationProcessingException(String msg) {
		super(msg);
	}

	public String getResourceName() {
		return resourceName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Object getFieldValue() {
		return fieldValue;
	}
}
