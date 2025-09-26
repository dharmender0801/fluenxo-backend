package com.asc.auth.dto;

import java.math.BigDecimal;

import com.asc.auth.model.audit.UserDateAudit;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class WalletDto extends UserDateAudit {
	private Long id;
	private Long userId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
	private BigDecimal balance;
}
