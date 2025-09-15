package com.asc.auth.dto;

import java.math.BigDecimal;

import com.asc.auth.model.audit.UserDateAudit;
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
public class CampaignInfoDto extends UserDateAudit {
	private Long id;
	private String title;
	private String image;
	private String campaignLink;
	private BigDecimal budget;
	private Integer status;
	private Integer reach;
	private Integer clicks;
	private BigDecimal spent;
	
}
