package com.asc.auth.model;

import com.asc.auth.model.audit.UserDateAudit;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = CampaignClickInfo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CampaignClickInfo extends UserDateAudit {

	public static final String TABLE_NAME = "campaign_click_info";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
