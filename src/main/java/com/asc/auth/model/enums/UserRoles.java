package com.asc.auth.model.enums;

import java.util.Objects;

public enum UserRoles {
	ROLE_SUPERUSER(1), ROLE_USER(2), ROLE_VENDOR(3), ROLE_LABOUR(4), ROLE_CUSTOMER(5);

	private Integer id;

	UserRoles(int id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public static UserRoles fromId(Integer id) {
		if (Objects.isNull(id))
			return null;
		for (UserRoles role : UserRoles.values()) {
			if (role.getId().equals(id)) {
				return role;
			}
		}
		return null;
	}

	public static UserRoles fromString(String role) {
		if (Objects.isNull(role))
			return null;
		for (UserRoles roleId : UserRoles.values()) {
			if (roleId.name().equalsIgnoreCase(role)) {
				return roleId;
			}
		}
		return null;
	}
}
