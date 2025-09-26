package com.asc.auth.model;

import java.math.BigDecimal;

import com.asc.auth.model.audit.UserDateAudit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
@Entity
@Table(name = Wallet.TABLE_NAME)
@ToString
@Builder
public class Wallet extends UserDateAudit {

	public static final String TABLE_NAME = "wallet";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	@Builder.Default
	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal balance = BigDecimal.ZERO;
}
