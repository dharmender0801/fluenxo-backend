package com.asc.auth.config;

import com.asc.auth.model.enums.TransactionType;

import jakarta.persistence.AttributeConverter;

public class TransactionTypeConverter implements AttributeConverter<TransactionType, Integer> {

	@Override
	public Integer convertToDatabaseColumn(TransactionType userType) {
		return userType != null ? userType.getId() : null;
	}

	@Override
	public TransactionType convertToEntityAttribute(Integer dbData) {
		return dbData != null ? TransactionType.fromId(dbData) : null;
	}
}