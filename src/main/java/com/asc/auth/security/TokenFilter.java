package com.asc.auth.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.asc.auth.model.User;
import com.asc.auth.service.UserService;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenFilter extends OncePerRequestFilter {

	@Autowired
	@Lazy
	private TokenProvider tokenProvider;

	@Autowired
	@Lazy
	private UserService userService;

	private static InheritableThreadLocal<String> jwtToken = new InheritableThreadLocal<>();
	private static InheritableThreadLocal<Long> _userId = new InheritableThreadLocal<>();

	public static void setJwtToken(String value) {
		jwtToken.set(value);
	}

	public static String getJwtToken() {
		return jwtToken.get();
	}

	public static void setUserId(Long value) {
		_userId.set(value);
	}

	public static Long getGetUserId() {
		return _userId.get();
	}

	public static void clearJwt() {
		jwtToken.remove();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		System.out.println(authHeader);
		String jwt;
		String userId;
		if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		jwt = authHeader.substring(7);
		userId = tokenProvider.extractUserName(jwt);
		if (!StringUtils.isEmpty(userId) && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userService.loadUserByUserId(Long.valueOf(userId));
			setUserId(Long.valueOf(userId));
			if (tokenProvider.isTokenValid(jwt, userDetails)) {
				SecurityContext context = SecurityContextHolder.createEmptyContext();
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				context.setAuthentication(authToken);
				SecurityContextHolder.setContext(context);

			} else {

			}
		}
		filterChain.doFilter(request, response);

	}
}
