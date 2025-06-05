package com.asc.auth.security;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.asc.auth.config.AppProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenProvider {

	@Autowired
	AppProperties appProperties;

	public String extractUserName(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails.getUsername());
	}

	public String createToken(Authentication authentication) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof OidcUserPrincipalAdapter) {
			OidcUserPrincipalAdapter oidcUser = (OidcUserPrincipalAdapter) principal;
			return generateToken(new HashMap<>(), Long.toString(oidcUser.getUserPrincipal().getId()));
		} else {
			UserPrincipal userPrincipal = (UserPrincipal) principal;
			return generateToken(new HashMap<>(), Long.toString(userPrincipal.getId()));
		}
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String userName = extractUserName(token);
		return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	public boolean validateToken(String authToken) {
		try {
			log.debug("validate authToken: {}", authToken);
			Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(appProperties.getAuth().getTokenSecret()),
					SignatureAlgorithm.HS256.getJcaName());
			Jws<Claims> jwt = Jwts.parserBuilder().setSigningKey(hmacKey).build().parseClaimsJws(authToken);
			return Boolean.TRUE.equals(Objects.nonNull(jwt));
		} catch (SecurityException ex) {
			log.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
		}
		return false;
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
		final Claims claims = extractAllClaims(token);
		return claimsResolvers.apply(claims);
	}

	private String generateToken(Map<String, Object> extraClaims, String userName) {
		return Jwts.builder().setClaims(extraClaims).setSubject(userName)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + appProperties.getAuth().getTokenExpirationMsec()))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(appProperties.getAuth().getTokenSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
