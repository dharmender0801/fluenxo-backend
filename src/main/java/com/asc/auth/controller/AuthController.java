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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asc.auth.dto.AuthResponse;
import com.asc.auth.dto.LoginRequest;
import com.asc.auth.dto.SignUpRequest;
import com.asc.auth.dto.UserDto;
import com.asc.auth.model.User;
import com.asc.auth.model.enums.AuthProvider;
import com.asc.auth.model.enums.DeviceType;
import com.asc.auth.repository.UserRepository;
import com.asc.auth.security.TokenProvider;
import com.asc.auth.security.UserPrincipal;
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

	@PostMapping("/signup")
	public ResponseEntity<RestResponse<UserDto>> registerUser(
			@RequestHeader(name = Constants.DEVICE_TYPE, required = false) DeviceType deviceType,
			@RequestHeader(name = Constants.APP_VERSION, required = false) String appVersion,
			@Valid @RequestBody SignUpRequest signUpRequest) throws ExecutionException, BadRequestException {
		if (Boolean.TRUE.equals(signUpRequest.getProvider().equals(AuthProvider.email))
				&& Boolean.TRUE.equals(userRepository.existsByEmailAndEmailVerified(signUpRequest.getEmail(), true))) {
			throw new BadRequestException("Email address already in use.");
		} else if (Boolean.TRUE.equals(signUpRequest.getProvider().equals(AuthProvider.mobile)) && Boolean.TRUE
				.equals(userRepository.existsByMobileAndMobileVerified(signUpRequest.getMobile(), true))) {
			throw new BadRequestException("Mobile Number already in use.");
		} else {
			signUpRequest.setProvider(signUpRequest.getProvider());
			User result = userService.createorUpdateUser(signUpRequest, false);
			log.info("User Creation : {} ", result);
			UserDto userInfo = new UserDto();
			Utils.copyProperties(result, userInfo);
			return RestUtils.successResponse(userInfo, "User has been provisioned for channel: ", HttpStatus.CREATED);
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
			if (Objects.nonNull(loginRequest.getFcmId())) {
				userInfo.setFcmToken(loginRequest.getFcmId());
				userRepository.save(userInfo);
			}
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
}
