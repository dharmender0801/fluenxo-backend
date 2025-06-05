package com.asc.auth.utils.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class RestUtils {
	static Logger logger = LoggerFactory.getLogger(RestUtils.class);

	public static <T> ResponseEntity<RestResponse<T>> successResponse(T data, String message, HttpStatus statusCode) {
		logger.info("{} - {} - {}", statusCode.name(), statusCode.value(), message);
		return new ResponseEntity<>(new RestResponse<>(data, message), statusCode);
	}

	public static <T> ResponseEntity<RestResponse<T>> successResponseWithCache(T data, String message,
			HttpStatus statusCode, Integer time) {
		logger.info("{} - {} - {}", statusCode.name(), statusCode.value(), message);
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.set("Cache-Control", "max-age=" + time);
		return new ResponseEntity<>(new RestResponse<>(data, message), headers, statusCode);
	}

	public static <T> ResponseEntity<RestResponse<T>> errorResponse(T errorDescription, String errorMessage,
			HttpStatus statusCode) {
		return new ResponseEntity<>(
				new RestResponse<>(errorMessage, errorDescription, Boolean.FALSE, statusCode.value()), statusCode);
	}
}
