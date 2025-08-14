package com.asc.auth.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.asc.auth.dto.ResponseObject;
import com.asc.auth.exception.ResourceNotFoundException;
import com.asc.auth.security.TokenFilter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommunicationService {

	@Autowired
	RestTemplate restTemplate;

	@Value("${mail.service.url:https://ems-dev.apollosupplychain.com/}")
	String mailerServiceUrl;

	public Object sendMail(Object dto, String token) {
		log.info("Request Need to be send  : {}", dto);
		String mailerUrl = String.format("%scomms-service/email/sendEmail", mailerServiceUrl);
		try {
			HttpEntity<?> quickCodeRequest = new HttpEntity<>(dto, getServiceHeaders(token));
			ResponseEntity<ResponseObject<Object>> result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			log.info("url: {}, MailerRequest: {}", mailerUrl, quickCodeRequest);
			try {
				result = restTemplate.exchange(mailerUrl, HttpMethod.POST, quickCodeRequest,
						new ParameterizedTypeReference<ResponseObject<Object>>() {
						});
			} catch (Exception exp) {
				log.error(exp.getMessage());
			}
			log.trace("Response from Mailer : {}", result);
			if (Boolean.TRUE.equals(result.getStatusCode().is2xxSuccessful())) {
				return result.getBody().getResponse();
			} else {
				throw new ResourceNotFoundException("Quick code Detail Not Found", "", null);
			}
		} catch (Exception exp) {
			return null;
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
		headers.set("AuthUrl", "https://lms-dev.apollosupplychain.com/lms-auth-service/validate/user");
		return headers;
	}
}
