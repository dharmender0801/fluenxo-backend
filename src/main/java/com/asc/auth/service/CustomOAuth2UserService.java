package com.asc.auth.service;

import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.asc.auth.dto.SignUpRequest;
import com.asc.auth.exception.OAuth2AuthenticationProcessingException;
import com.asc.auth.model.User;
import com.asc.auth.model.enums.AuthProvider;
import com.asc.auth.security.UserPrincipal;
import com.asc.auth.security.oauth2.user.OAuth2UserInfo;
import com.asc.auth.security.oauth2.user.OAuth2UserInfoFactory;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private UserService userService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("@UserRequest :{} ", userRequest.getClientRegistration().getRegistrationId());
		OAuth2User oAuth2User = super.loadUser(userRequest);

		try {
			return processOAuth2User(userRequest, oAuth2User);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
//		
		}
	}

	private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
				oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
		if (ObjectUtils.isEmpty(oAuth2UserInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}
		Optional<User> userOptional = userService.findByEmail(oAuth2UserInfo.getEmail());
		User user;
		log.debug("userOptional.isPresent(): {}", userOptional.isPresent());
		if (userOptional.isPresent()) {
			user = userOptional.get();
			if (!user.getProvider()
					.equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
				throw new OAuth2AuthenticationProcessingException(
						"Looks like you're signed up with " + user.getProvider() + " account. Please use your "
								+ user.getProvider() + " account to login with email id " + user.getEmail() + ".");
			}
			user = updateExistingUser(oAuth2UserInfo);
		} else {
			user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
		}

		return UserPrincipal.create(user, oAuth2User.getAttributes());
	}

	private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
		SignUpRequest signUpRequest = new SignUpRequest();
		signUpRequest.setEmail(oAuth2UserInfo.getEmail());
		signUpRequest.setFirstName(oAuth2UserInfo.getName());
		signUpRequest.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
//		signUpRequest.setProviderId(oAuth2UserInfo.getId());
		signUpRequest.setImageUrl(oAuth2UserInfo.getImageUrl());
		return userService.createorUpdateUser(signUpRequest, true);
	}

	private User updateExistingUser(OAuth2UserInfo oAuth2UserInfo) {
		SignUpRequest signUpRequest = new SignUpRequest();
		signUpRequest.setEmail(oAuth2UserInfo.getEmail());
		signUpRequest.setFirstName(oAuth2UserInfo.getName());
//		signUpRequest.setProviderId(oAuth2UserInfo.getId());
		signUpRequest.setImageUrl(oAuth2UserInfo.getImageUrl());
		return userService.createorUpdateUser(signUpRequest, false);
	}
}