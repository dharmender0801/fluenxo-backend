package com.asc.auth.model;

import java.math.BigDecimal;

import com.asc.auth.config.TransactionTypeConverter;
import com.asc.auth.model.audit.UserDateAudit;
import com.asc.auth.model.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = PaymentLog.TABLE_NAME)
@ToString
public class PaymentLog extends UserDateAudit {

	public static final String TABLE_NAME = "payment_log";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "order_id", nullable = false, length = 100)
	private String orderId;
	@Column(name = "payment_id", length = 100)
	private String paymentId;
	@Convert(converter = TransactionTypeConverter.class)
	@Column(name = "status", columnDefinition = "int")
	private TransactionType status;
	@Column(precision = 18, scale = 2)
	private BigDecimal amount;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "failure_reason", length = 255)
	private String failureReason;
}
