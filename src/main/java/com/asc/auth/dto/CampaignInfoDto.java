package com.asc.auth.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.asc.auth.model.audit.UserDateAudit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	private BigDecimal cpc;
	private String remarks;
	@JsonProperty("associatedUsers")
	private List<AssociateUserDto> associatedUser = new ArrayList<>();

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AssociateUserDto extends UserDateAudit {
		private Long id;
		private Long userId;
		private BigDecimal cpc;
	}
}
