package com.asc.auth.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asc.auth.dto.UserInfoDto;
import com.asc.auth.model.enums.DeviceType;
import com.asc.auth.security.CurrentUser;
import com.asc.auth.security.UserPrincipal;
import com.asc.auth.service.UserService;
import com.asc.auth.utils.Constants;
import com.asc.auth.utils.response.RestResponse;
import com.asc.auth.utils.response.RestUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/validate")
public class ValidationController {

	@Autowired
	UserService userService;

	@GetMapping(path = "/user", produces = "application/json")
	public ResponseEntity<RestResponse<UserPrincipal>> validateUser(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion, @CurrentUser UserPrincipal userPrincipal) {
		return Boolean.TRUE.equals(Objects.nonNull(userPrincipal))
				? RestUtils.successResponse(userPrincipal, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(userPrincipal, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "/getuserInfo", produces = "application/json")
	public ResponseEntity<RestResponse<UserInfoDto>> getUserInfo(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion, @CurrentUser UserPrincipal userPrincipal,
			@RequestParam(value = "id") Long id) {
		log.debug("Get User Info Endpoint called. User Id :{}", id);
		UserInfoDto userInfoDto = userService.getUserInfo(id);
		return Boolean.TRUE.equals(Objects.nonNull(userInfoDto))
				? RestUtils.successResponse(userInfoDto, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(userInfoDto, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}
}
