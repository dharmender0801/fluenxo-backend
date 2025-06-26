package com.asc.auth.model;

import com.asc.auth.config.UserTypeConverter;
import com.asc.auth.model.audit.UserDateAudit;
import com.asc.auth.model.enums.AuthProvider;
import com.asc.auth.model.enums.UserRoles;
import com.asc.auth.model.enums.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = User.TABLE_NAME, uniqueConstraints = {
		@UniqueConstraint(name = "UniqueUserIdAndEmailAndMobile", columnNames = { "user_name", "email", "mobile" }) })
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User extends UserDateAudit {

	public static final String TABLE_NAME = "user";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "fcm_token")
	private String fcmToken;
	@Column(name = "account")
	private String account;
	@Column(name = "user_name")
	private String userName;
	@Column(name = "password")
	private String password;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "full_name")
	private String fullName;
	@Column(name = "status", columnDefinition = "int default 1")
	private Integer status;
	@Column(name = "profile_image")
	private String profileImage;
	@Column(name = "profile_background_image")
	private String profileBackGroundImage;
	@Column(name = "date_of_birth")
	private String dateOfBirth;
	@Column(name = "age")
	private String age;
	@Column(name = "gender")
	private String gender;

	@Email
	@Column(name = "email")
	private String email;
	@Column(name = "mobile")
	private String mobile;
	@Column(name = "address_1")
	private String address1;
	@Column(name = "address_2")
	private String address2;
	@Column(name = "city")
	private String city;
	@Column(name = "state")
	private String state;
	@Column(name = "country")
	private String country;
	@Column(name = "pin_code")
	private String pinCode;
	@Column(name = "latitude")
	private String latitude;
	@Column(name = "longitude")
	private String longitude;
	@Column(name = "provider")
	@NotNull
	@Enumerated(EnumType.STRING)
	private AuthProvider provider;
	@Column(name = "provider_id")
	private String providerId;
	@Column(name = "mobile_verified", columnDefinition = "int")
	private Boolean mobileVerified;
	@Column(name = "email_verified", columnDefinition = "int")
	private Boolean emailVerified;
	@Column(name = "is_otp", columnDefinition = "int")
	private Boolean isOtp;
	@Column(name = "otp")
	private String otp;
	@Convert(converter = UserTypeConverter.class)
	private UserType userType;

}
