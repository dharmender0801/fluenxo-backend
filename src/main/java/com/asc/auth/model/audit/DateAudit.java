package com.asc.auth.model.audit;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.asc.auth.utils.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public abstract class DateAudit{
	@CreatedDate
	@Column(nullable = false, updatable = false)
	@JsonFormat(shape = Shape.STRING, pattern = Constants.REQUEST_DATETIME_PATTEREN, timezone = Constants.REQUEST_TIMEZONE)
	private Date createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	@JsonFormat(shape = Shape.STRING, pattern = Constants.REQUEST_DATETIME_PATTEREN, timezone = Constants.REQUEST_TIMEZONE)
	private Date updatedAt;
}

