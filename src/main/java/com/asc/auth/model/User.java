package com.asc.auth.model;

import com.asc.auth.model.audit.UserDateAudit;
import com.asc.auth.model.enums.AuthProvider;

import jakarta.persistence.Column;
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
	@Column(name = "user_name")
	private String userName;
	@Column(name = "password")
	private String password;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "status", columnDefinition = "int default 1")
	private Integer status;
	@Email
	@Column(name = "email")
	private String email;
	@Column(name = "mobile")
	private String mobile;
	@Column(name = "provider")
	@NotNull
	@Enumerated(EnumType.STRING)
	private AuthProvider provider;
	@Column(name = "mobile_verified", columnDefinition = "int")
	private Boolean mobileVerified;
	@Column(name = "email_verified", columnDefinition = "int")
	private Boolean emailVerified;
	@Column(name = "is_otp", columnDefinition = "int")
	private Boolean isOtp;
	@Column(name = "otp")
	private String otp;
	private String profileImage;
}
