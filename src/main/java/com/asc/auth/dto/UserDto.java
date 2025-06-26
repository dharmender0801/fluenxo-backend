package com.asc.auth.dto;

import com.asc.auth.model.enums.AuthProvider;
import com.asc.auth.model.enums.UserRoles;
import com.asc.auth.model.enums.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UserDto {

	private Long id;
	private String deviceImei;
	private String deviceId;
	private String deviceModel;
	private String fcmToken;
	private String account;
	@Enumerated(EnumType.STRING)
	private UserType userType;
	private String userName;
	@JsonIgnore
	private String password;
	private String title;
	private String firstName;
	private String lastName;
	private String fullName;
	private Integer status;
	private String profileImage;
	private String profileBackGroundImage;
	private String dateOfBirth;
	private String age;
	private String gender;
	private String email;
	private String mobile;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private String latitude;
	private String longitude;
	private AuthProvider provider;
	private String providerId;
	private Boolean mobileVerified;
	private Boolean emailVerified;
	private String bearerToken;
	private Integer otp;
}
