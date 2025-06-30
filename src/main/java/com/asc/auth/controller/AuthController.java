package com.asc.auth.controller;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asc.auth.dto.AuthResponse;
import com.asc.auth.dto.LoginRequest;
import com.asc.auth.dto.SignUpRequest;
import com.asc.auth.dto.UserDto;
import com.asc.auth.model.User;
import com.asc.auth.model.enums.AuthProvider;
import com.asc.auth.model.enums.DeviceType;
import com.asc.auth.model.enums.UserRoles;
import com.asc.auth.repository.UserRepository;
import com.asc.auth.security.TokenProvider;
import com.asc.auth.security.UserPrincipal;
import com.asc.auth.service.OTPService;
import com.asc.auth.service.UserService;
import com.asc.auth.utils.Constants;
import com.asc.auth.utils.Utils;
import com.asc.auth.utils.response.RestResponse;
import com.asc.auth.utils.response.RestUtils;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private OTPService otpService;

	@PostMapping("/signup")
	public ResponseEntity<RestResponse<UserDto>> registerUser(
			@RequestHeader(name = Constants.DEVICE_TYPE, required = false) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION, required = false) String appVersion,
			@Valid @RequestBody SignUpRequest signUpRequest) throws ExecutionException, BadRequestException {
		User userInfo = null;
		switch (signUpRequest.getProvider()) {
		case local:
			userInfo = userRepository.findByEmailOrUserNameOrMobile(signUpRequest.getUser(), signUpRequest.getUser(),
					signUpRequest.getUser()).orElse(null);
			break;
		case mobile:
			userInfo = userRepository.findByEmailOrUserNameOrMobile(signUpRequest.getMobile(),
					signUpRequest.getMobile(), signUpRequest.getMobile()).orElse(null);
			log.info("Requets Checking : {} ", userInfo);
			break;
		case email:
			userInfo = userRepository.findByEmailOrUserNameOrMobile(signUpRequest.getEmail(), signUpRequest.getEmail(),
					signUpRequest.getEmail()).orElse(null);
			break;
		default:
			userInfo = userRepository.findByEmailOrUserNameOrMobile(signUpRequest.getEmail(), signUpRequest.getUser(),
					signUpRequest.getMobile()).orElse(null);
			break;
		}

		if (userInfo != null) {
			UserDto userInfoDto = new UserDto();
			Utils.copyProperties(userInfo, userInfoDto);
			return RestUtils.errorResponse(userInfoDto,
					"This account already exists. Please log in or use a different email/mobile.", HttpStatus.CONFLICT);
		} else {
			User result = userService.createUserWithReplica(signUpRequest);
			log.info("User Creation : {} ", result);
			int otp = otpService.generateOTP(result.getId());
			UserDto userInfoDto = new UserDto();
			Utils.copyProperties(result, userInfoDto);
			userInfoDto.setOtp(otp);
			return RestUtils.successResponse(userInfoDto, "User has been provisioned for channel: ",
					HttpStatus.CREATED);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<RestResponse<AuthResponse>> authenticateUser(
			@RequestHeader(name = Constants.DEVICE_TYPE, required = false) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION, required = false) String appVersion,
			@Valid @RequestBody LoginRequest loginRequest) throws BadCredentialsException, ExecutionException {
		User userInfo = userRepository.findByEmailOrUserNameOrMobile(loginRequest.getPrincipal(),
				loginRequest.getPrincipal(), loginRequest.getPrincipal()).orElse(null);
		log.info("{}", userInfo);
		if (Boolean.TRUE.equals(Objects.isNull(userInfo))) {
			return RestUtils.errorResponse(null, Constants.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
		} else if (Boolean.FALSE.equals(userInfo.getMobileVerified()) || loginRequest.getIsOtpLogin()) {
			log.error("mobile number: {}, of user: {}, not verified. ");
			Integer otp = otpService.generateOTP(userInfo.getId());
			UserDto userInfoDto = new UserDto();
			Utils.copyProperties(userInfo, userInfoDto);
			log.info("OTP send result: {}", sendOtp(userInfoDto, otp, AuthProvider.local));
			AuthResponse auth = new AuthResponse();
			Utils.copyProperties(userInfo, auth);
			auth.setOtp(otp);
			return RestUtils.errorResponse(auth, "User has been provisioned for reverification on channel: "
					+ userInfo.getProvider() + ", please reverify the OTP. (" + otp + ")",
					HttpStatus.FAILED_DEPENDENCY);
		} else {
			log.info("Going into user authentication block {}.", userInfo);
			Authentication authentication = null;
			try {
				authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
						loginRequest.getPrincipal(), loginRequest.getCredentials()));
			} catch (BadCredentialsException exp) {
				log.error("{}", exp.getLocalizedMessage());
				return RestUtils.errorResponse(null, Constants.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
			}
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String token = tokenProvider.createToken(authentication);
			authentication = SecurityContextHolder.getContext().getAuthentication();
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			AuthResponse auth = new AuthResponse();
			auth.setId(userPrincipal.getId());
			auth.setAccessToken(token);
			auth.setUserName(userPrincipal.getUsername());
			auth.setUserEmail(userPrincipal.getEmail());
			auth.setFirstName(userPrincipal.getFirstName());
			auth.setLastName(userPrincipal.getLastName());
			auth.setTokenType("Bearer");
			return (Boolean.TRUE.equals(Objects.nonNull(token)))
					? RestUtils.successResponse(auth, Constants.SUCCESS, HttpStatus.OK)
					: RestUtils.errorResponse(auth, Constants.FAIL, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(path = "/verifyOtp", produces = "application/json")
	public ResponseEntity<RestResponse<UserDto>> verifyOtp(
			@RequestHeader(name = Constants.DEVICE_TYPE, required = false) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION, required = false) String appVersion,
			@RequestParam(value = "otp", required = true) Integer otp,
			@RequestParam(value = "userId", required = true) Long userID,
			@RequestParam(value = "channel", required = true) AuthProvider channel, @RequestParam Boolean isOtpLogin) {
		log.debug("Received OTP: {}, userID: {}", otp, userID);
		UserDto userInfo = userService.verifyOtp(otp, userID, channel, isOtpLogin);
		return (Boolean.TRUE.equals(Objects.nonNull(userInfo)))
				? RestUtils.successResponse(userInfo, "OTP verified and User registered successfully.", HttpStatus.OK)
				: RestUtils.errorResponse(null, "OTP cannot be verified. Please retry.", HttpStatus.BAD_REQUEST);
	}

	@GetMapping(path = "/reSendOtp", produces = "application/json")
	public ResponseEntity<RestResponse<UserDto>> reSendOtp(
			@RequestHeader(name = Constants.DEVICE_TYPE, required = false) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION, required = false) String appVersion,
			@RequestParam(value = "mobile", required = false) String mobile,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "channel", required = true) AuthProvider channel) throws ExecutionException {
		log.debug("Received reverify OTP request for, mobile: {}", mobile);
		User user = (Boolean.TRUE.equals(channel.name().equalsIgnoreCase("mobile")))
				? userRepository.findByMobile(mobile).orElse(null)
				: userRepository.findByEmail(email).orElse(null);
		if (Boolean.TRUE.equals(Objects.nonNull(user))) {
			user.setMobileVerified(Boolean.FALSE);
			user.setIsOtp(Boolean.FALSE);
			user.setOtp(null);
			user = userRepository.save(user);
			log.debug("Mobile: {},user.getMobileVerified(): {}, user.getEmailVerified(): {} ", mobile,
					user.getMobileVerified(), user.getEmailVerified());
			UserDto userInfo = new UserDto();
			Utils.copyProperties(user, userInfo);
			Integer otp = otpService.generateOTP(user.getId());
			log.info("OTP send result: {}", sendOtp(userInfo, otp, channel));
			return RestUtils.successResponse(userInfo, "User has been provisioned for reverification on channel: "
					+ user.getProvider() + ", please reverify the OTP. (" + otp + ")", HttpStatus.CREATED);
		} else {
			return RestUtils.successResponse(null, "User not found.", HttpStatus.NOT_FOUND);
		}
	}

	private Boolean sendOtp(UserDto userInfo, Integer otp, AuthProvider channel) {
//		otpService.sendOtpMail(userInfo, otp);
		return otpService.sendOtp(userInfo, otp);
	}
}
