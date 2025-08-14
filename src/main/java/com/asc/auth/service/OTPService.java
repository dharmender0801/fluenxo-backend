package com.asc.auth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.asc.auth.dto.UserDto;
import com.asc.auth.model.User;
import com.asc.auth.model.enums.AuthProvider;
import com.asc.auth.repository.UserRepository;
import com.asc.auth.security.TokenProvider;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OTPService {

	private static final Random RANDOM = new Random();

	@Value("${app.otp.expiration.duration:4}")
	private String otpExpiryDuration;

	@Autowired
	UserRepository userRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CommunicationService communicationService;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	@Lazy
	private AuthenticationManager authenticationManager;

	@Value("${superuser.email:af-1870}")
	private String superUserEmail;
	@Value("${superuser.password:abc@123}")
	private String password;

	private LoadingCache<Long, Integer> otpCache;

	public OTPService() {
		super();
		int otp = 4;
		try {
			otp = Integer.parseInt(otpExpiryDuration);
		} catch (NumberFormatException exp) {
		}
		log.debug("OTP Expiration Duration {} minutes.", otp);
		otpCache = CacheBuilder.newBuilder().expireAfterWrite(otp, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, Integer>() {
					public Integer load(Long key) {
						return 0;
					}
				});
	}

	public Integer generateOTP(Long key) throws ExecutionException {
		Integer otp = 100000 + RANDOM.nextInt(900000);
		log.debug("key: {}, otp: {}", key, otp);
		otpCache.put(key, otp);
		log.debug("otpCache.asMap(): {}", otpCache.asMap());
		log.debug("otpCache.get({}) = {}", key, otpCache.get(key));
		User userInfo = userRepo.findById(key).orElse(null);
		if (Boolean.TRUE.equals(Objects.nonNull(userInfo))) {
			userInfo.setIsOtp(true);
			userInfo.setOtp(passwordEncoder.encode(otp.toString()));
			userRepo.save(userInfo);
		}
		return otp;
	}

	public Integer getOtp(Long key) {
		try {
			log.debug("otpCache.get(key) = {}", otpCache.get(key));
			return otpCache.get(key);
		} catch (Exception e) {
			return 0;
		}
	}

	public void clearOTP(Long key) {
		User userInfo = userRepo.findById(key).orElse(null);
		if (Boolean.TRUE.equals(Objects.nonNull(userInfo))) {
			userInfo.setIsOtp(Boolean.FALSE);
			userInfo.setOtp(null);
			userRepo.save(userInfo);
		}
		otpCache.invalidate(key);
	}

	public Boolean sendOtp(UserDto userInfo, Integer otp) {
		Authentication authentication = null;
		try {
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(superUserEmail, password));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String token = tokenProvider.createToken(authentication);
			if (AuthProvider.email.equals(userInfo.getProvider())) {
				String bodyText = "Dear User,\n\n" + "Your One Time Password (OTP) is: " + otp + " \n"
						+ "This OTP is valid for 5 minutes. Please do not share it with anyone.\n\n" + "Thank you,\n"
						+ "Apollo Supply Chain";
				Map<String, Object> emailData = new HashMap<>();
				emailData.put("toEmails", userInfo.getEmail());
				emailData.put("subject", "Apollo Supply Chain – Secure OTP for Verification");
				emailData.put("bodyText", bodyText);
				emailData.put("isBodyHtml", false);
				log.info("sending Request : {} ", communicationService.sendMail(emailData, token));

			}
		} catch (BadCredentialsException exp) {
			log.error("Not Genrating : {} ", exp.getMessage());
		}

		return null;
	}
}
