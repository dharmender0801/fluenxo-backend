package com.asc.auth.dto;

import java.math.BigDecimal;

import com.asc.auth.model.audit.UserDateAudit;
import com.asc.auth.model.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLogDto extends UserDateAudit {
	private Long id;
	private String orderId;
	private String paymentId;
	private TransactionType status;
	private BigDecimal amount;
	private Long userId;
	private String failureReason;
	private String razorpaySignature;
}
