package com.asc.auth.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asc.auth.dto.ModulesDto;
import com.asc.auth.dto.RoleMasterDto;
import com.asc.auth.dto.RoleMasterDto.ModulePermissionDto;
import com.asc.auth.dto.RolesDto;
import com.asc.auth.model.enums.DeviceType;
import com.asc.auth.service.AccessManagerService;
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
@Tag(name = "Access Manager", description = "Access Management APIs")
@RequestMapping("/access")
@Slf4j
public class AccessController {
	@Autowired
	AccessManagerService accessManagerService;

	@Operation(summary = "Get Complete Role List With Permissions", description = "This API Provide Roles List with permissions granted for the role", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = RolesDto.class)) }),
			@ApiResponse(responseCode = "406", description = "NOT Acceptable", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = String.class)) }) })
	@GetMapping(path = "/getRoles", produces = "application/json")
	public ResponseEntity<RestResponse<List<RolesDto>>> getRoleList(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion) throws Exception {
		List<RolesDto> roleList = accessManagerService.getRoleList();
		return !roleList.isEmpty() ? RestUtils.successResponse(roleList, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Add Or Update Role", description = "This api Add Or update Permission Details for a Role", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = RoleMasterDto.class)) }) })
	@PostMapping(path = "/addRole", produces = "application/json")
	public ResponseEntity<RestResponse<RoleMasterDto>> addRole(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion, @RequestBody RoleMasterDto roleMasterDto) {
		log.info("Add  Roles  Request recived : {} ", roleMasterDto);
		RoleMasterDto roleDto = accessManagerService.addRole(roleMasterDto);
		return Objects.nonNull(roleDto) ? RestUtils.successResponse(roleDto, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Add Or Update Role Permissions", description = "This api Add Or update Permission Details for a Role", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = RoleMasterDto.class)) }) })
	@PostMapping(path = "/updateRolePermissions", produces = "application/json")
	public ResponseEntity<RestResponse<RoleMasterDto>> updateRolePermissions(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion, @RequestBody RoleMasterDto roleMasterDto) {
		log.info("Add or Update Roles Permissions Request recived : {} ", roleMasterDto);
		RoleMasterDto roleDto = accessManagerService.updateRolePermissionsNew(roleMasterDto);
		return Objects.nonNull(roleDto) ? RestUtils.successResponse(roleDto, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Get Complete Module List With Submodules", description = "This API Provide Module List with sub-modules for the main module", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = ModulesDto.class)) }),
			@ApiResponse(responseCode = "406", description = "NOT Acceptable", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = String.class)) }) })
	@GetMapping(path = "/getModules", produces = "application/json")
	public ResponseEntity<RestResponse<List<ModulesDto>>> getModules(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion) throws Exception {
		List<ModulesDto> roleList = accessManagerService.getModuleList();
		return !roleList.isEmpty() ? RestUtils.successResponse(roleList, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Add Or Update Modules and Submodules", description = "This api Add Or update modules and submodules of the portal", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = ModulesDto.class)) }) })
	@PostMapping(path = "/updateModules", produces = "application/json")
	public ResponseEntity<RestResponse<List<ModulesDto>>> updateModules(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion,
			@RequestBody List<ModulesDto> modulesDtoList) {
		modulesDtoList = accessManagerService.updateModules(modulesDtoList);
		return !modulesDtoList.isEmpty() ? RestUtils.successResponse(modulesDtoList, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Get Menu List With Permissions", description = "This API Provide Menu modules List with permissions granted for the modules based on role", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = ModulePermissionDto.class)) }),
			@ApiResponse(responseCode = "406", description = "NOT Acceptable", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = String.class)) }) })
	@GetMapping(path = "/getSideMenuPermissions", produces = "application/json")
	public ResponseEntity<RestResponse<List<ModulePermissionDto>>> getSideMenuPermisions(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion, @RequestParam Long roleId)
			throws Exception {
		List<ModulePermissionDto> sideMenuList = accessManagerService.getSideMenuPermisions(roleId);
		return !sideMenuList.isEmpty() ? RestUtils.successResponse(sideMenuList, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

}