package com.asc.auth.config;

import com.asc.auth.model.enums.UserType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserTypeConverter implements AttributeConverter<UserType, Integer> {

	@Override
	public Integer convertToDatabaseColumn(UserType userType) {
		return userType != null ? userType.getId() : null;
	}

	@Override
	public UserType convertToEntityAttribute(Integer dbData) {
		return dbData != null ? UserType.fromId(dbData) : null;
	}
}
