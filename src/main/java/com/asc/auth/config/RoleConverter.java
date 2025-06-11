package com.asc.auth.config;

import com.asc.auth.model.enums.UserRoles;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<UserRoles, Integer> {

	@Override
	public Integer convertToDatabaseColumn(UserRoles role) {
		return role != null ? role.getId() : null;
	}

	@Override
	public UserRoles convertToEntityAttribute(Integer dbData) {
		return dbData != null ? UserRoles.fromId(dbData) : null;
	}
}
