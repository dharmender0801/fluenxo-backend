package com.asc.auth.model;

import java.math.BigDecimal;

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
@Table(name = CampaignInfo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CampaignInfo extends UserDateAudit {

	public static final String TABLE_NAME = "campaign_info";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
