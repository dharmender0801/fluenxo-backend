package com.asc.auth.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
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

import com.asc.auth.dto.SignUpRequest;
import com.asc.auth.dto.UserDto;
import com.asc.auth.dto.UserInfoDto;
import com.asc.auth.exception.ResourceNotFoundException;
import com.asc.auth.model.User;
import com.asc.auth.model.enums.AuthProvider;
import com.asc.auth.model.enums.UserRoles;
import com.asc.auth.repository.UserRepository;
import com.asc.auth.security.TokenProvider;
import com.asc.auth.security.UserPrincipal;
import com.asc.auth.utils.Utils;

import jakarta.validation.Valid;
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

	@Override
	public UserDetails loadUserByUsername(String emailOrUserName) throws UsernameNotFoundException {
		User user = userRepository.findByEmailOrUserNameOrMobile(emailOrUserName, emailOrUserName, emailOrUserName)
				.orElseThrow(() -> new UsernameNotFoundException(
						"User not found with email or userName : " + emailOrUserName));
		return UserPrincipal.create(user);
	}

	public User createorUpdateUser(SignUpRequest requestUser, boolean b) {
		User user = null;
		log.info("requestUser: {}", requestUser);
		if (requestUser.getProvider() != null) {
			user = userRepository.findByEmail(requestUser.getEmail()).orElse(null);

			switch (requestUser.getProvider()) {
			case mobile:
				user = userRepository.findByMobile(requestUser.getMobile()).orElse(null);
				break;
			case email:
				user = userRepository.findByEmail(requestUser.getEmail()).orElse(null);
				break;
			case local:
				user = userRepository.findByUserName(requestUser.getUser()).orElse(null);
				break;
			default:
				user = userRepository.findByEmail(requestUser.getEmail()).orElse(null);
			}
		} else {
			user = userRepository.findByEmail(requestUser.getEmail()).orElse(null);
		}
		user = userRepository.findByEmailAndMobile(requestUser.getEmail(), requestUser.getMobile()).orElse(null);
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
		user.setFullName(user.getFirstName() + " " + user.getLastName());
		user.setEmail(requestUser.getEmail());
		user.setMobile(requestUser.getMobile());
		user.setFcmToken(Objects.nonNull(requestUser.getFcmId()) ? requestUser.getFcmId() : null);
		user.setPassword(passwordEncoder
				.encode((Boolean.TRUE.equals(Objects.nonNull(requestUser.getPassword()))) ? requestUser.getPassword()
						: "123456"));
		user.setMobileVerified(isNewUser ? false : user.getMobileVerified());
		user.setEmailVerified(isNewUser ? false : user.getEmailVerified());
		user.setRole(requestUser.getRole());
		User result = userRepository.save(user);
		return result;
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

	public UserDto verifyOtp(Integer otp, Long userID, AuthProvider channel) {
		UserDto userInfo = null;
		User user = userRepository.findById(userID)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Not found with ID: ", userID));
		log.debug("Channel: {},user.getMobileVerified(): {}, user.getEmailVerified(): {} ", channel,
				user.getMobileVerified(), user.getEmailVerified());
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

	public User updateUserRole(Long userId, UserRoles userRole) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Not found with ID: ", userId));
		user.setRole(userRole);
		User result = userRepository.save(user);
		log.debug("user Update result {}", result);
		return result;
	}
}
