package com.asc.auth.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.asc.auth.dto.SignUpRequest;
import com.asc.auth.model.User;
import com.asc.auth.repository.UserRepository;
import com.asc.auth.security.UserPrincipal;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	@Lazy
	private PasswordEncoder passwordEncoder;

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
		user.setMobileVerified(false);
		user.setEmailVerified(false);
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

}
