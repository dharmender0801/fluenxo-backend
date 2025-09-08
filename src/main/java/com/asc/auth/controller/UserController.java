package com.asc.auth.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asc.auth.dto.FiltersDto;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/user")
@Tag(name = "User Controller", description = "User Management  APIs")
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

	@Operation(summary = "Get User Detail", description = "This api provide User Detail", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = UserDto.class)) }) })
	@GetMapping(path = "/getByUserId", produces = "application/json")
	public ResponseEntity<RestResponse<UserDto>> getByUserId(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion, @RequestParam Long userId) {
		log.info("Add Account Request recived : {} ", userId);
		UserDto userDto = userService.getUserById(userId);
		return Objects.nonNull(userDto) ? RestUtils.successResponse(userDto, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Get User Detail List With Pagination", description = "This API Provide User Details List with pagination <br>Filters List: "
			+ "<br>&#9679; VENDOR_ID_IN   <br>&#9679; ACCOUNT_ID_IN  <br>&#9679; USER_ID_IN <br>&#9679; SKILL_IN <br>&#9679; USER_NAME <br>&#9679; EXPERIENCE_IN", responses = {
					@ApiResponse(responseCode = "200", description = "OK.", content = {
							@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = UserDto.class)) }),
					@ApiResponse(responseCode = "406", description = "NOT Acceptable", content = {
							@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = String.class)) }) })
	@PostMapping(path = "/getUsers", produces = "application/json")
	public ResponseEntity<RestResponse<Page<UserDto>>> getUsers(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion,
			@RequestBody(required = false) List<FiltersDto> filters, @RequestParam(required = true) Integer pageNumber,
			@RequestParam(required = true) Integer pageSize, @RequestParam(required = false) String sortingColumn,
			@RequestParam(required = false) Direction direction) throws Exception {
		Page<UserDto> pageList = userService.getUsers(filters, pageNumber, pageSize, sortingColumn, direction);
		return pageList != null && !pageList.isEmpty()
				? RestUtils.successResponse(pageList, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

}
