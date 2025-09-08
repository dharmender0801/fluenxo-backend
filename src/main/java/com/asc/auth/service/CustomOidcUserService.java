package com.asc.auth.service;

import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.asc.auth.dto.SignUpRequest;
import com.asc.auth.exception.OAuth2AuthenticationProcessingException;
import com.asc.auth.model.User;
import com.asc.auth.model.enums.AuthProvider;
import com.asc.auth.security.OidcUserPrincipalAdapter;
import com.asc.auth.security.UserPrincipal;
import com.asc.auth.security.oauth2.user.OAuth2UserInfo;
import com.asc.auth.security.oauth2.user.OAuth2UserInfoFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomOidcUserService extends OidcUserService {

	@Autowired
	UserService userService;

	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("Processing OIDC login for provider: {}", userRequest.getClientRegistration().getRegistrationId());
		OidcUser oidcUser = super.loadUser(userRequest);
		try {
			return processOidcUser(userRequest, oidcUser);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
		OAuth2UserInfo userInfo = OAuth2UserInfoFactory
				.getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), oidcUser.getAttributes());
		if (ObjectUtils.isEmpty(userInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OIDC provider");
		}
		User user = findOrCreateUser(userRequest, userInfo);
		return new OidcUserPrincipalAdapter(UserPrincipal.create(user, oidcUser.getAttributes()), oidcUser.getIdToken(),
				oidcUser.getUserInfo());
	}

	private User findOrCreateUser(OidcUserRequest userRequest, OAuth2UserInfo userInfo) {
		Optional<User> userOptional = userService.findByEmail(userInfo.getEmail());
		User user;

		if (userOptional.isPresent()) {
			user = userOptional.get();
			if (!user.getProvider()
					.equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
				throw new OAuth2AuthenticationProcessingException("Account already exists with " + user.getProvider()
						+ " provider. " + "Please use your " + user.getProvider() + " account to login.");
			}
			user = updateExistingUser(userInfo, userRequest);
		} else {
			user = registerNewUser(userRequest, userInfo);
		}
		return user;
	}

	private User registerNewUser(OidcUserRequest userRequest, OAuth2UserInfo userInfo) {
		SignUpRequest signUpRequest = new SignUpRequest();
		signUpRequest.setEmail(userInfo.getEmail());
		signUpRequest.setFirstName(userInfo.getName());
		signUpRequest.setProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
//		signUpRequest.setProviderId(userRequest.getIdToken().getSubject());
		signUpRequest.setProfileImage(userInfo.getImageUrl());
		return userService.createorUpdateUser(signUpRequest);
	}

	private User updateExistingUser(OAuth2UserInfo userInfo, OidcUserRequest userRequest) {
		SignUpRequest signUpRequest = new SignUpRequest();
		signUpRequest.setEmail(userInfo.getEmail());
		signUpRequest.setFirstName(userInfo.getName());
		signUpRequest.setProfileImage(userInfo.getImageUrl());
		signUpRequest.setProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
		return userService.createorUpdateUser(signUpRequest);
	}
}
