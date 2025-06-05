package com.asc.auth.dto;

import com.asc.auth.model.enums.UserRoles;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
	private Long id;
	private String accessToken;
	private String tokenType = "Bearer";
	private String userName;
	private String userEmail;
	private String firstName;
	private String lastName;
}
