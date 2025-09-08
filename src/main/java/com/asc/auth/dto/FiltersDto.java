package com.asc.auth.dto;

import java.io.Serializable;
import java.util.Date;

import com.asc.auth.model.enums.FilterKeys;
import com.asc.auth.utils.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The Class FiltersDto.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FiltersDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3323456459925167876L;

	/** The key. */
	private FilterKeys key;

	/** The value. */
	private String value;

	/** The date value. */
	@JsonFormat(shape = Shape.STRING, pattern = Constants.REQUEST_DATE_PATTEREN)
	private Date dateValue;

	/** The date time value. */
	@JsonFormat(shape = Shape.STRING, pattern = Constants.REQUEST_DATETIME_PATTEREN)
	private Date dateTimeValue;

}