package com.asc.auth.dto;

import com.asc.auth.model.enums.AuthProvider;
import com.asc.auth.model.enums.UserRoles;
import com.asc.auth.model.enums.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignUpRequest {
	private String deviceId;
	private String firstName;
	private String lastName;
	private String fullName;
	private String user;
	@Email
	private String email;
	private String mobile;
	private String password;
	@Enumerated(EnumType.STRING)
	private AuthProvider provider;
	@JsonIgnore
	private String imageUrl;
	private String fcmId;
	private Integer status;
	private String profileImage;
	private String profileBackGroundImage;
	private String dateOfBirth;
	private String age;
	private String gender;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private String latitude;
	private String longitude;
	private UserType userType;
}
