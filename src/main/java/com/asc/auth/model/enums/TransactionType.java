package com.asc.auth.model.enums;

import java.util.Objects;

public enum TransactionType {

	CREDIT(1), DEBIT(2), CREATED(3), CAPTURED(4), FAILED(5), PENDING(6);

	private Integer id;

	TransactionType(int id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public static TransactionType fromId(Integer id) {
		if (Objects.isNull(id))
			return null;
		for (TransactionType role : TransactionType.values()) {
			if (role.getId().equals(id)) {
				return role;
			}
		}
		return null;
	}

	public static TransactionType fromString(String role) {
		if (Objects.isNull(role))
			return null;
		for (TransactionType roleId : TransactionType.values()) {
			if (roleId.name().equalsIgnoreCase(role)) {
				return roleId;
			}
		}
		return null;
	}
}
