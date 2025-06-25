package com.asc.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(defaultValue = "af-1870")
	private String principal;

	@NotBlank
	@JsonProperty(value = "credentials")
	@Schema(defaultValue = "abc@123")
	private String credentials;

	@JsonProperty(value = "fcm_id")
	private String fcmId;
}
