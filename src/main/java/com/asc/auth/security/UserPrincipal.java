package com.asc.auth.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.asc.auth.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserPrincipal implements OAuth2User, UserDetails {
	private static final long serialVersionUID = -5062647961800884179L;
	private Long id;
	private String mobile;

	@Override
	public String toString() {
		return "UserPrincipal [id=" + id + ", mobile=" + mobile + ", email=" + email + ", password=" + password
				+ ", authorities=" + authorities + ", attributes=" + attributes + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", isOtp=" + isOtp + ", otp=" + otp + ", user=" + user + "]";
	}

	private String email;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;
	private String firstName;
	private String lastName;
	private Boolean isOtp = false;
	private String otp;
	private User user;

	public UserPrincipal(Long id, String mobile, String email, String password,
			Collection<? extends GrantedAuthority> authorities, String firstName, String lastName, Boolean isOtp,
			String otp, User user) {
		this.id = id;
		this.mobile = mobile;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isOtp = isOtp;
		this.otp = otp;
		this.user = user;
	}

	public static UserPrincipal create(User user) {
		log.debug("User: {}", user);
		SimpleGrantedAuthority grantAuthority = new SimpleGrantedAuthority("ROLE_USER");
		List<GrantedAuthority> authorities = Collections.singletonList(grantAuthority);
		return new UserPrincipal(user.getId(), user.getMobile(), user.getEmail(), user.getPassword(), authorities,
				user.getFirstName(), user.getLastName(), user.getIsOtp(), user.getOtp(), user);
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static UserPrincipal create(User user, Map<String, Object> attributes) {
		UserPrincipal userPrincipal = UserPrincipal.create(user);
		userPrincipal.setAttributes(attributes);
		return userPrincipal;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		if (Boolean.TRUE.equals(isOtp)) {
			return otp;
		}
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getName() {
		return String.valueOf(id);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
