package com.asc.auth.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class OidcUserPrincipalAdapter implements OidcUser {
	private final UserPrincipal userPrincipal;
	private final OidcIdToken idToken;
	private final OidcUserInfo userInfo;

	public OidcUserPrincipalAdapter(UserPrincipal userPrincipal, OidcIdToken idToken, OidcUserInfo userInfo) {
		this.userPrincipal = userPrincipal;
		this.idToken = idToken;
		this.userInfo = userInfo;
	}

	@Override
	public Map<String, Object> getClaims() {
		return userInfo.getClaims();
	}

	@Override
	public OidcUserInfo getUserInfo() {
		return userInfo;
	}

	@Override
	public OidcIdToken getIdToken() {
		return idToken;
	}

	// Delegate all UserPrincipal methods
	@Override
	public Map<String, Object> getAttributes() {
		return userPrincipal.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return userPrincipal.getAuthorities();
	}

	@Override
	public String getName() {
		return userPrincipal.getName();
	}

	// Add getters to access the original UserPrincipal
	public UserPrincipal getUserPrincipal() {
		return userPrincipal;
	}

	// Delegate all UserDetails methods
	public String getPassword() {
		return userPrincipal.getPassword();
	}

	public String getUsername() {
		return userPrincipal.getUsername();
	}

	public boolean isAccountNonExpired() {
		return userPrincipal.isAccountNonExpired();
	}

	public boolean isAccountNonLocked() {
		return userPrincipal.isAccountNonLocked();
	}

	public boolean isCredentialsNonExpired() {
		return userPrincipal.isCredentialsNonExpired();
	}

	public boolean isEnabled() {
		return userPrincipal.isEnabled();
	}
}