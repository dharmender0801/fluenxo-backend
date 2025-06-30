package com.asc.auth.service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.asc.auth.dto.ResponseObject;
import com.asc.auth.dto.SignUpRequest;
import com.asc.auth.dto.UserDto;
import com.asc.auth.dto.UserInfoDto;
import com.asc.auth.dto.UserRequestDto;
import com.asc.auth.exception.ResourceNotFoundException;
import com.asc.auth.model.User;
import com.asc.auth.model.enums.AuthProvider;
import com.asc.auth.repository.UserRepository;
import com.asc.auth.security.TokenFilter;
import com.asc.auth.security.TokenProvider;
import com.asc.auth.security.UserPrincipal;
import com.asc.auth.utils.Utils;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	@Lazy
	private PasswordEncoder passwordEncoder;

	@Autowired
	@Lazy
	private OTPService otpService;
	@Autowired
	@Lazy
	private AuthenticationManager authenticationManager;
	@Autowired
	TokenProvider tokenProvider;
	@Value("user.replica.creation.endpoint:/lms-service/labour/addOrUpdate")
	private String acccountUserCreation;
	@Value("service.base.url:https://lms-dev.apollosupplychain.com/")
	private String serviceBaseUrl;
	@Autowired
	RestTemplate restTemplate;

	@Override
	public UserDetails loadUserByUsername(String emailOrUserName) throws UsernameNotFoundException {
		User user = userRepository.findByEmailOrUserNameOrMobile(emailOrUserName, emailOrUserName, emailOrUserName)
				.orElseThrow(() -> new UsernameNotFoundException(
						"User not found with email or userName : " + emailOrUserName));
		return UserPrincipal.create(user);
	}

	public User createorUpdateUser(SignUpRequest requestUser) {
		User user = userRepository
				.findByEmailOrUserNameOrMobile(requestUser.getEmail(), requestUser.getUser(), requestUser.getMobile())
				.orElse(null);
		Boolean isNewUser = Boolean.FALSE;
		if (Boolean.TRUE.equals(Objects.isNull(user))) {
			user = new User();
			isNewUser = Boolean.TRUE;
			user.setProvider(requestUser.getProvider());
			if (Boolean.TRUE.equals(Objects.nonNull(requestUser.getUser()))) {
				user.setUserName(requestUser.getUser());
			}
		}
		try {
			user.setFirstName(
					(Boolean.TRUE.equals(Objects.nonNull(requestUser.getFirstName()))) ? requestUser.getFirstName()
							: (Boolean.TRUE.equals(Objects.nonNull(requestUser.getFullName())))
									? requestUser.getFullName().substring(0, requestUser.getFullName().indexOf(" "))
									: "");
		} catch (StringIndexOutOfBoundsException exp) {
			user.setFirstName(requestUser.getFullName());
		}
		try {
			user.setLastName(
					(Boolean.TRUE.equals(Objects.nonNull(requestUser.getLastName()))) ? requestUser.getLastName()
							: (Boolean.TRUE.equals(Objects.nonNull(requestUser.getFullName())))
									? requestUser.getFullName().substring(requestUser.getFullName().indexOf(" "))
									: "");
		} catch (StringIndexOutOfBoundsException exp) {
			user.setLastName("");
		}
		user.setEmail(requestUser.getEmail());
		user.setMobile(requestUser.getMobile());
		user.setPassword(passwordEncoder
				.encode((Boolean.TRUE.equals(Objects.nonNull(requestUser.getPassword()))) ? requestUser.getPassword()
						: "123456"));
		user.setMobileVerified(isNewUser ? false : user.getMobileVerified());
		user.setEmailVerified(isNewUser ? false : user.getEmailVerified());
		User result = userRepository.save(user);
		return result;
	}

	@Transactional
	public User createUserWithReplica(SignUpRequest requestUser) {
		User user = createorUpdateUser(requestUser);
		Authentication authentication = null;
		try {
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getMobile(), "123456"));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String token = tokenProvider.createToken(authentication);
			UserRequestDto userRequestDto = buildRequest(user);
			log.info("Create User : {} ", create(userRequestDto, token));
		} catch (BadCredentialsException exp) {
			log.error(exp.getMessage());
		}

		return null;
	}

	public UserRequestDto create(UserRequestDto signUpRequestDto, String token) {
		try {
			String signUpUrl = String.format("%s/lms-service/user/add", serviceBaseUrl);
			HttpEntity<UserRequestDto> signupRequest = new HttpEntity<>(signUpRequestDto, getServiceHeaders(token));
			ResponseEntity<ResponseObject<UserRequestDto>> result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			try {
				result = restTemplate.exchange(signUpUrl, HttpMethod.POST, signupRequest,
						new ParameterizedTypeReference<ResponseObject<UserRequestDto>>() {
						});
			} catch (Exception exp) {
				log.error(exp.getMessage());
			}
			log.trace("signupResponse: {}", result);
			if (Boolean.TRUE.equals(result.getStatusCode().is2xxSuccessful())) {
				return result.getBody().getResponse();
			} else {
				throw new ResourceNotFoundException("Account details not found.", "", signUpRequestDto);
			}
		} catch (Exception exp) {
			throw exp;
		}
	}

	public HttpHeaders getServiceHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		log.trace("Jwt ThreadLocal token: {}", TokenFilter.getJwtToken());
		headers.set("Authorization", String.format("Bearer %s", token));
		headers.set("DEVICE-TYPE", "Web");
		headers.set("VER", "1.0");
		return headers;
	}

	private UserRequestDto buildRequest(User user) {
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setId(user.getId());
		userRequestDto.setUserCode(user.getUserName());
		userRequestDto.setEmail(user.getEmail());
		userRequestDto.setMobile(user.getMobile());
		return userRequestDto;
	}

	public UserDetails loadUserByUserId(Long valueOf) {
		User user = userRepository.findById(valueOf)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email or userName : " + valueOf));
		return UserPrincipal.create(user);
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Cacheable(value = "userInfoDetails", key = "#id")
	public UserInfoDto getUserInfo(Long id) {
		log.info("User id to be checked from DB is: {}", id);
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
		log.info("User received from db: {}", user);
		UserInfoDto userDto = new UserInfoDto();
		Utils.copyProperties(user, userDto);
		return userDto;
	}

	public UserDto verifyOtp(Integer otp, Long userID, AuthProvider channel, Boolean isLogin) {
		UserDto userInfo = null;
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Not found with ID: ", userID));
		log.debug("Channel: {},user.getMobileVerified(): {}, user.getEmailVerified(): {} ", channel,
				user.getMobileVerified(), user.getEmailVerified());
		if (!isLogin) {
			if (otp >= 0 && ((Boolean.FALSE.equals(user.getMobileVerified())) && channel.equals(AuthProvider.mobile))
					|| (Boolean.FALSE.equals(user.getEmailVerified())) && channel.equals(AuthProvider.email)) {
				Integer serverOtp = otpService.getOtp(userID);
				log.debug("Retrived OTP: {}, Received OTP: {}", serverOtp, otp);
				if (serverOtp > 0) {
					log.debug("Boolean.TRUE.equals({}.compareTo({}): {})", otp, serverOtp, otp.compareTo(serverOtp));
					if (Boolean.TRUE.equals(otp.compareTo(serverOtp) == 0)) {
						user = validateMobile(userID, channel);
						Authentication authentication = null;
						try {
							authentication = authenticationManager
									.authenticate(new UsernamePasswordAuthenticationToken(user.getMobile(), otp));
							SecurityContextHolder.getContext().setAuthentication(authentication);
							String token = tokenProvider.createToken(authentication);
							userInfo = new UserDto();
							Utils.copyProperties(user, userInfo);
							log.info("Updating the user in DB also");
							Utils.copyProperties(userInfo, user);
							user = userRepository.save(user);
							Utils.copyProperties(user, userInfo);
							userInfo.setBearerToken(token);
						} catch (BadCredentialsException exp) {
							log.error(exp.getMessage());
						}
						otpService.clearOTP(userID);
						Utils.copyProperties(user, userInfo);
						log.debug("{}", user);
					}
				}
			}
		} else {
			Integer serverOtp = otpService.getOtp(userID);
			log.debug("Retrived OTP: {}, Received OTP: {}", serverOtp, otp);
			if (serverOtp > 0) {
				log.debug("Boolean.TRUE.equals({}.compareTo({}): {})", otp, serverOtp, otp.compareTo(serverOtp));
				if (Boolean.TRUE.equals(otp.compareTo(serverOtp) == 0)) {
					Authentication authentication = null;
					try {
						authentication = authenticationManager
								.authenticate(new UsernamePasswordAuthenticationToken(user.getMobile(), otp));
						SecurityContextHolder.getContext().setAuthentication(authentication);
						String token = tokenProvider.createToken(authentication);
						userInfo = new UserDto();
						Utils.copyProperties(user, userInfo);
						userInfo.setBearerToken(token);
					} catch (BadCredentialsException exp) {
						log.error(exp.getMessage());
					}
					otpService.clearOTP(userID);
					Utils.copyProperties(user, userInfo);
					log.debug("{}", user);
				}
			}
		}
		return userInfo;
	}

	public User validateMobile(Long userDescription, AuthProvider channel) {
		User user = userRepository.findById(userDescription).orElse(null);
		if (Boolean.TRUE.equals(Objects.isNull(user))) {
			return null;
		} else {
			if (channel.equals(AuthProvider.mobile)) {
				user.setMobileVerified(true);
			} else if (channel.equals(AuthProvider.email)) {
				user.setEmailVerified(true);
			}
			User result = userRepository.save(user);
			log.debug("user creation result {}", result);
			return result;
		}
	}

	public UserDto createAndUpdateUser(UserDto userDto) {
		User user = userDto.getId() == null
				? userRepository
						.findByEmailOrUserNameOrMobile(userDto.getEmail(), userDto.getUserName(), userDto.getMobile())
						.orElse(new User())
				: userRepository.findById(userDto.getId()).orElseThrow(
						() -> new ResourceNotFoundException("User", "Not found with ID: ", userDto.getId()));
		Utils.copyProperties(userDto, user);
		User result = userRepository.save(user);
		Utils.copyProperties(result, userDto);
		return userDto;
	}
}
