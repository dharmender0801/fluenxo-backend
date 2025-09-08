package com.asc.auth.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Setter
@Getter
public class UserInfoDto {

	@JsonProperty("id")
	private Long id;
	@JsonProperty("mobile")
	private String mobile;
	@JsonProperty("email")
	private String email;
	@JsonProperty("authorities")
	private List<Authority> authorities;
	@JsonProperty("attributes")
	private Object attributes;
	@JsonProperty("role")
	private String role;
	@JsonProperty("account")
	private Object account;
	@JsonProperty("firstName")
	private String firstName;
	@JsonProperty("lastName")
	private String lastName;
	@JsonProperty("gender")
	private Object gender;
	@JsonProperty("enabled")
	private Boolean enabled;
	@JsonProperty("username")
	private String username;
	@JsonProperty("accountNonExpired")
	private Boolean accountNonExpired;
	@JsonProperty("accountNonLocked")
	private Boolean accountNonLocked;
	@JsonProperty("credentialsNonExpired")
	private Boolean credentialsNonExpired;
	@JsonProperty("name")
	private String name;
	private String profileImage;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ToString
	@Setter
	@Getter
	public static class Authority {
		@JsonProperty("authority")
		private String authority;
	}
}