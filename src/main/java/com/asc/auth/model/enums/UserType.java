package com.asc.auth.model.enums;

import java.util.Objects;

public enum UserType {
	VENDOR(1), ASSOCIATE(2), CUSTOMER(3);

	private Integer id;

	UserType(int id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public static UserType fromId(Integer id) {
		if (Objects.isNull(id))
			return null;
		for (UserType role : UserType.values()) {
			if (role.getId().equals(id)) {
				return role;
			}
		}
		return null;
	}

	public static UserType fromString(String type) {
		if (Objects.isNull(type))
			return null;
		for (UserType roleId : UserType.values()) {
			if (roleId.name().equalsIgnoreCase(type)) {
				return roleId;
			}
		}
		return null;
	}
}
