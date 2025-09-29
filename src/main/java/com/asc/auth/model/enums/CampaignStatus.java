package com.asc.auth.model.enums;

import java.util.Objects;

public enum CampaignStatus {

	INACTIVE(0), ACTIVE(1), LIVE(3), PAUSE(4);

	private Integer id;

	CampaignStatus(int id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public static CampaignStatus fromId(Integer id) {
		if (Objects.isNull(id))
			return null;
		for (CampaignStatus role : CampaignStatus.values()) {
			if (role.getId().equals(id)) {
				return role;
			}
		}
		return null;
	}

	public static CampaignStatus fromString(String role) {
		if (Objects.isNull(role))
			return null;
		for (CampaignStatus roleId : CampaignStatus.values()) {
			if (roleId.name().equalsIgnoreCase(role)) {
				return roleId;
			}
		}
		return null;
	}
}
