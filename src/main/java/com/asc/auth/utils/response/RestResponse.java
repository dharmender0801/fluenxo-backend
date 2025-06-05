package com.asc.auth.utils.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RestResponse<T> {
	private String responseMessage;
	private Boolean responseStatus = true;
	private Integer responseStatusCode = 200;
	private T responseObject;

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Boolean getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(Boolean responseStatus) {
		this.responseStatus = responseStatus;
	}

	public T getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(T responseObject) {
		this.responseObject = responseObject;
	}

	public Integer getResponseStatusCode() {
		return responseStatusCode;
	}

	public void setResponseStatusCode(Integer responseStatusCode) {
		this.responseStatusCode = responseStatusCode;
	}

	public RestResponse(String responseMessage, T responseObject, Boolean responseStatus, Integer responseStatusCode) {
		this.responseMessage = responseMessage;
		this.responseObject = responseObject;
		this.responseStatus = responseStatus;
		this.responseStatusCode = responseStatusCode;
	}

	public RestResponse(T data) {
		this.responseObject = data;
	}

	public RestResponse(T data, String responseMessage) {
		this.responseObject = data;
		this.responseMessage = responseMessage;
	}

	public RestResponse() {
	}
}
