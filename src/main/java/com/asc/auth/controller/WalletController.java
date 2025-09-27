package com.asc.auth.controller;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asc.auth.dto.PaymentLogDto;
import com.asc.auth.dto.WalletDto;
import com.asc.auth.model.enums.DeviceType;
import com.asc.auth.security.TokenFilter;
import com.asc.auth.service.PaymentService;
import com.asc.auth.service.RazorpayService;
import com.asc.auth.service.WalletService;
import com.asc.auth.utils.Constants;
import com.asc.auth.utils.response.RestResponse;
import com.asc.auth.utils.response.RestUtils;
import com.razorpay.RazorpayException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/wallet")
@Tag(name = "Wallet Controller", description = "Wallet Management  APIs")
public class WalletController {

	@Autowired
	WalletService walletService;

	@Autowired
	RazorpayService razorpayService;
	@Autowired
	PaymentService paymentService;

	@Operation(summary = "Get Current User Wallet", description = "This api provide wallet Detail", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = WalletDto.class)) }) })
	@GetMapping("/getCurrentUserWallet")
	public ResponseEntity<RestResponse<WalletDto>> addOrUpdate(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion) {
		log.info("Get Wallet Requested By : {} ", TokenFilter.getGetUserId());
		WalletDto walletDto = walletService.getCurrentUserWallet();
		return Objects.nonNull(walletDto) ? RestUtils.successResponse(walletDto, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Wallet Recharge ", description = "This api Reachage the wallet", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = WalletDto.class)) }) })
	@PostMapping("/recharge")
	public ResponseEntity<RestResponse<Map<String, Object>>> recharge(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion, @RequestBody WalletDto walletDto)
			throws RazorpayException {
		log.info("Add Money In Wallet ", TokenFilter.getGetUserId());
		Map<String, Object> response = razorpayService.createOrder(walletDto.getBalance(), "INR");
		return Objects.nonNull(response) ? RestUtils.successResponse(response, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Wallet Recharge ", description = "This api Reachage the wallet", responses = {
			@ApiResponse(responseCode = "200", description = "OK.", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = PaymentLogDto.class)) }) })
	@PostMapping("/updatePaymentStatus")
	public ResponseEntity<RestResponse<PaymentLogDto>> recharge(
			@RequestHeader(name = Constants.DEVICE_TYPE) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION) String appVersion, @RequestBody PaymentLogDto paymentLogDto)
			throws Exception {
		paymentLogDto = paymentService.updatePaymentLogs(paymentLogDto);
		return Objects.nonNull(paymentLogDto)
				? RestUtils.successResponse(paymentLogDto, Constants.SUCCESS, HttpStatus.OK)
				: RestUtils.errorResponse(null, Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
	}

}
