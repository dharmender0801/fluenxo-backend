package com.asc.auth.dto;

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
public class CampaignClickInfoDto extends UserDateAudit {
	private Long id;
	private Long campaignId;
	private String ipAddress;
	private String userAgent;
	private String deviceId;
	private String screenSize;
	private String operator;
	private String latitude;
	private String longitude;
	private String referer;
	private Long influencerId;
	private String connectionType;
	private String deviceRam;
	private String deviceCpuCore;
	private String networkAsn;
	private String networkCity;
	private String networkRegion;
	private String networkCountry;
	private String networkTimezone;
}
