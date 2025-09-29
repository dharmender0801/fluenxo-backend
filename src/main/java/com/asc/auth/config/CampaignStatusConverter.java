package com.asc.auth.config;

import com.asc.auth.model.enums.CampaignStatus;

import jakarta.persistence.AttributeConverter;

public class CampaignStatusConverter implements AttributeConverter<CampaignStatus, Integer> {

	@Override
	public Integer convertToDatabaseColumn(CampaignStatus userType) {
		return userType != null ? userType.getId() : null;
	}

	@Override
	public CampaignStatus convertToEntityAttribute(Integer dbData) {
		return dbData != null ? CampaignStatus.fromId(dbData) : null;
	}
}
