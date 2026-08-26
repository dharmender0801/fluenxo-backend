package com.asc.auth.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asc.auth.dto.ParsedCommandDto;
import com.asc.auth.dto.ParsedCommandDto.CommandRequestDto;
import com.asc.auth.service.IntentService;
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
@Slf4j
@RequestMapping("/intent")
@Tag(name = "Intent Detection Controller", description = "Intent Detection Management  APIs")
public class IntentController {

	@Autowired
	IntentService intentService;

	@Operation(summary = "Parse User Command", description = "This API parses the user command and detects the intent.", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = ParsedCommandDto.class)) }) })
	@PostMapping(path = "/parse", produces = "application/json")
	public ResponseEntity<RestResponse<ParsedCommandDto>> parseCommand(
			@RequestBody CommandRequestDto commandRequestDto) {
		log.info("Intent parse request received: {}", commandRequestDto);
		ParsedCommandDto parsedCommandDto = intentService.parseCommand(commandRequestDto.getCommand());
		return Objects.nonNull(parsedCommandDto)
				? RestUtils.successResponse(parsedCommandDto, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}
}
