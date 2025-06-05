package com.asc.auth.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import com.asc.auth.security.TokenFilter;

public class AuditorAwareImpl implements AuditorAware<Long> {
	@Override
	public Optional<Long> getCurrentAuditor() {
		Long userId = TokenFilter.getGetUserId();
		return Optional.ofNullable(userId).or(() -> Optional.of(0L));
	}
}