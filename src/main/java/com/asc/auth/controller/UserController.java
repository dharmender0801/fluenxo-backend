package com.asc.auth.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asc.auth.dto.UserDto;
import com.asc.auth.model.enums.DeviceType;
import com.asc.auth.service.UserService;
import com.asc.auth.utils.Constants;
import com.asc.auth.utils.response.RestResponse;
import com.asc.auth.utils.response.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Operation(summary = "Add or Update User Detail", description = "This api Add Or Update User Detail", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = UserDto.class)) }) })
	@PostMapping(path = "/addOrUpdate", produces = "application/json")
	public ResponseEntity<RestResponse<UserDto>> addOrUpdate(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion, @RequestBody UserDto userDto) {
		log.info("Add Account Request recived : {} ", userDto);
		userDto = userService.createAndUpdateUser(userDto);
		return Objects.nonNull(userDto) ? RestUtils.successResponse(userDto, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

}
