package com.asc.auth.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.asc.auth.model.enums.DeviceType;
import com.asc.auth.service.FileService;
import com.asc.auth.utils.Constants;
import com.asc.auth.utils.response.RestResponse;
import com.asc.auth.utils.response.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/file")
@Slf4j
@Tag(name = "File Controller", description = "File Handling  APIs")
public class FileController {

	@Autowired
	FileService fileService;

	@Operation(summary = "Upload File", description = "This API used for uplaod file ", responses = {
			@ApiResponse(responseCode = "201", description = "Created", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = Boolean.class)) }) })
	@PostMapping(path = "/uploadFile", produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<RestResponse<String>> uploadFile(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion,
			@RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
		String fileUrl = fileService.uploadFile(file);
		return (Boolean.TRUE.equals(Objects.nonNull(fileUrl)))
				? RestUtils.successResponse(fileUrl, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}
}
