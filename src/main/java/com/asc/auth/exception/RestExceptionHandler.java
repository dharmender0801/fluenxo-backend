package com.asc.auth.exception;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.asc.auth.utils.response.RestResponse;
import com.asc.auth.utils.response.RestUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private Environment env;
//	@Autowired
//	QueueProducerService producerService;
	/** The environment. */
	@Value("${application.base.url:/}")
	private String environment;

	@Value("${spring.servlet.multipart.max-file-size:5}")
	private String maxFileSize;

	/**
	 * Handle method argument not valid.
	 *
	 * @param ex      the ex
	 * @param headers the headers
	 * @param status  the status
	 * @param request the request
	 * @return the response entity
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.method.annotation.
	 * ResponseEntityExceptionHandler
	 * #handleMethodArgumentNotValid(org.springframework
	 * .web.bind.MethodArgumentNotValidException,
	 * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
	 * org.springframework.web.context.request.WebRequest)
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		// publishToQueue(ex);
		return new ResponseEntity<>(
				new RestResponse<>(convertConstraintViolation(ex), null, Boolean.FALSE, status.value()),
				HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		// publishToQueue(ex);
		return new ResponseEntity<>(new RestResponse<>(ex.getLocalizedMessage(), null, Boolean.FALSE, status.value()),
				HttpStatus.BAD_REQUEST);
	}

	/**
	 * Convert constraint violation.
	 *
	 * @param ex the ex
	 * @return the string
	 */
	protected String convertConstraintViolation(MethodArgumentNotValidException ex) {
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<String> errorMessages = new ArrayList<>();
		for (FieldError c : fieldErrors) {
			errorMessages.add(c.getField().concat(" - ")
					.concat(c.getDefaultMessage().charAt(0) == '{'
							? env.resolvePlaceholders("$".concat(c.getDefaultMessage()))
							: c.getDefaultMessage()));
		}
		// publishToQueue(ex);
		return String.join(", ", errorMessages);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<RestResponse<String>> handle(ConstraintViolationException constraintViolationException) {
		Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
		String errorMessage = "";
		if (!violations.isEmpty()) {
			List<String> errorMessages = new ArrayList<>();
			violations.forEach(violation -> errorMessages.add(violation.getMessage().charAt(0) == '{'
					? env.resolvePlaceholders("$".concat(violation.getMessage()))
					: violation.getMessage()));
			errorMessage = String.join(", ", errorMessages);
		} else {
			errorMessage = "ConstraintViolationException occured.";
		}
//		publishToQueue(constraintViolationException);
		return RestUtils.errorResponse(errorMessage, null, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle Data Integrity Violation exception.
	 *
	 * @param ex      the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(value = { DataIntegrityViolationException.class })
	protected ResponseEntity<RestResponse<String>> handleDataIntegrityViolationException(
			DataIntegrityViolationException ex, WebRequest request) {
		log.error(ex.getRootCause().getLocalizedMessage(), ex);
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getRootCause().getLocalizedMessage(), ex.getLocalizedMessage(),
				HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * Handle lazy initialization exception.
	 *
	 * @param ex      the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(value = { LazyInitializationException.class })
	protected ResponseEntity<?> handleLazyInitializationException(LazyInitializationException ex, WebRequest request) {
		log.error(ex.getLocalizedMessage(), ex);
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getLocalizedMessage(), ex.getLocalizedMessage(), HttpStatus.CONFLICT);
	}

	/**
	 * Handle incorrect result size data access exception.
	 *
	 * @param ex      the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(value = { IncorrectResultSizeDataAccessException.class })
	protected ResponseEntity<RestResponse<String>> handleIncorrectResultSizeDataAccessException(
			IncorrectResultSizeDataAccessException ex, WebRequest request) {
		log.error(ex.getRootCause().getLocalizedMessage(), ex);
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getRootCause().getLocalizedMessage(), ex.getLocalizedMessage(),
				HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * Handle SQL exception.
	 *
	 * @param ex      the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(value = { SQLException.class })
	protected ResponseEntity<RestResponse<String>> handleSQLException(SQLException ex, WebRequest request) {
		log.error(ex.getMessage(), ex);
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getLocalizedMessage(), ex.getLocalizedMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * Handle unknown exception.
	 *
	 * @param ex      the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(value = { Exception.class })
	protected ResponseEntity<RestResponse<String>> handleUnknownException(Exception ex, WebRequest request) {
		log.error(ex.getMessage(), ex);
		log.error(ex.getClass().getName());
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle null pointer exception.
	 *
	 * @param ex      the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(value = { NullPointerException.class })
	protected ResponseEntity<RestResponse<String>> handleNullPointerException(NullPointerException ex,
			WebRequest request) {
		log.error(ex.getMessage(), ex);
		log.error(ex.getClass().getName());
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle BadRequestException exception.
	 *
	 * @param ex      the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(value = { BadRequestException.class })
	protected ResponseEntity<RestResponse<String>> handleEntityAlreadyExistsException(BadRequestException ex,
			WebRequest request) {
		log.error(ex.getMessage(), ex);
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { UnexpectedRollbackException.class })
	protected ResponseEntity<RestResponse<String>> unexpectedRollbackException(UnexpectedRollbackException ex,
			WebRequest request) {
		log.error(ex.getMessage(), ex);
		log.error(ex.getClass().getName());
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getLocalizedMessage(), ex.getLocalizedMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(PessimisticLockingFailureException.class)
	public ResponseEntity<RestResponse<String>> lockException(PessimisticLockingFailureException ex,
			WebRequest request) {
		log.error(ex.getRootCause().getLocalizedMessage(), ex);
		// publishToQueue(ex);
		return RestUtils.errorResponse("PessimisticLockingFailureException", "PessimisticLockingFailureException",
				HttpStatus.BAD_GATEWAY);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<RestResponse<String>> resourceNotFoundException(ResourceNotFoundException ex,
			WebRequest request) {
		log.error(ex.getMessage(), ex);
		log.error(ex.getClass().getName());
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getLocalizedMessage(), ex.getLocalizedMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { EntityAlreadyExistsException.class })
	protected ResponseEntity<RestResponse<String>> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex,
			WebRequest request) {
		log.error(ex.getMessage(), ex);
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(value = { EntityNotFoundException.class })
	protected ResponseEntity<RestResponse<String>> handleEntityNotFoundException(EntityNotFoundException ex,
			WebRequest request) {
		log.error(ex.getMessage(), ex);
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { RecordNotFoundException.class })
	protected ResponseEntity<RestResponse<String>> handleRecordNotFoundException(RecordNotFoundException ex,
			WebRequest request) {
		log.error(ex.getMessage(), ex);
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { ConcurrentTransactionException.class })
	protected ResponseEntity<RestResponse<String>> handleConcurrentTransactionException(
			ConcurrentTransactionException ex, WebRequest request) {
		log.error(ex.getMessage(), ex);
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(value = { BadStateException.class })
	protected ResponseEntity<RestResponse<String>> handleBadStateException(ConcurrentTransactionException ex,
			WebRequest request) {
		log.error(ex.getMessage(), ex);
		// publishToQueue(ex);
		return RestUtils.errorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.CONFLICT);
	}

}
