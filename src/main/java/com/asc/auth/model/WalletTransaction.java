package com.asc.auth.model;

import java.math.BigDecimal;

import com.asc.auth.config.TransactionTypeConverter;
import com.asc.auth.model.audit.UserDateAudit;
import com.asc.auth.model.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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
@Table(name = WalletTransaction.TABLE_NAME)
@ToString
public class WalletTransaction extends UserDateAudit {

	public static final String TABLE_NAME = "wallet_transaction";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wallet_id", nullable = false)
	private Wallet wallet;
	@Convert(converter = TransactionTypeConverter.class)
	@Column(name = "type", columnDefinition = "int")
	private TransactionType type;
	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal amount;
	@Column(length = 255)
	private String reference;
}
