package com.asc.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
	@NotBlank
	@JsonProperty(value = "user")
	private String principal;

	@NotBlank
	@JsonProperty(value = "credentials")
	private String credentials;
	
	@JsonProperty(value = "fcm_id")
	private String fcmId;
}
