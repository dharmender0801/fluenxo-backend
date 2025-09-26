package com.asc.auth.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asc.auth.dto.CampaignInfoDto;
import com.asc.auth.dto.FiltersDto;
import com.asc.auth.model.enums.DeviceType;
import com.asc.auth.service.CampaignService;
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
@RequestMapping("/campaign")
@Tag(name = "Campaign Controller", description = "Campaign Management Api's")
public class CampaignController {

	@Autowired
	CampaignService campaignService;

	@Operation(summary = "Add or Update User Detail", description = "This api Add Or Update User Detail", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = CampaignInfoDto.class)) }) })
	@PostMapping(path = "/addOrUpdate", produces = "application/json")
	public ResponseEntity<RestResponse<CampaignInfoDto>> addOrUpdate(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion,
			@RequestBody CampaignInfoDto campaignInfoDto) {
		log.info("Add Account Request recived : {} ", campaignInfoDto);
		campaignInfoDto = campaignService.addOrUpdate(campaignInfoDto);
		return Objects.nonNull(campaignInfoDto)
				? RestUtils.successResponse(campaignInfoDto, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Get User Detail List With Pagination", description = "This API Provide User Details List with pagination <br>Filters List: "
			+ "<br>&#9679; CREATED_BY   <br>&#9679; USER_ID_IN ", responses = {
					@ApiResponse(responseCode = "200", description = "OK.", content = {
							@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = CampaignInfoDto.class)) }),
					@ApiResponse(responseCode = "406", description = "NOT Acceptable", content = {
							@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = String.class)) }) })
	@PostMapping(path = "/getCampaigns", produces = "application/json")
	public ResponseEntity<RestResponse<Page<CampaignInfoDto>>> getCampaigns(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion,
			@RequestBody(required = false) List<FiltersDto> filters, @RequestParam(required = true) Integer pageNumber,
			@RequestParam(required = true) Integer pageSize, @RequestParam(required = false) String sortingColumn,
			@RequestParam(required = false) Direction direction) throws Exception {
		Page<CampaignInfoDto> pageList = campaignService.getCampaigns(filters, pageNumber, pageSize, sortingColumn,
				direction);
		return pageList != null && !pageList.isEmpty()
				? RestUtils.successResponse(pageList, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	@PostMapping(path = "/getJson", produces = "application/json")
	public ResponseEntity<RestResponse<Map<String, Object>>> getJson(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion) {
		Map<String, Object> json = campaignService.getJson();
		return Objects.nonNull(json) ? RestUtils.successResponse(json, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

}
