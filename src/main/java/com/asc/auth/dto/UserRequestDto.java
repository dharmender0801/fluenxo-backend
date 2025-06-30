package com.asc.auth.dto;

import java.util.Date;

import com.asc.auth.model.enums.UserType;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
@ToString
public class UserRequestDto {
	private Long id;
	private String name;
	private String mobile;
	private String email;
	private String userCode;
	private String profileImage;
	private Date dateOfBirth;
	private Long aadharNumber;
	private Integer yearsOfExperience;
	private String panNumber;
	private Integer status;
	private String addressLine1;
	private String addressLine2;
	private Long pincodeId;
	private Long cityId;
	private Boolean isProfileComplete;
	private UserType userType;
	private String role;
	private Long roleId;
}
