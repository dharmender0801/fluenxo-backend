package com.asc.auth.service;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.asc.auth.dto.UserDto;
import com.asc.auth.model.User;
import com.asc.auth.repository.UserRepository;
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
		// TODO Auto-generated method stub
		return null;
	}
}
