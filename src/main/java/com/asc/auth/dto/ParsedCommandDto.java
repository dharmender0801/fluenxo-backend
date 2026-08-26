package com.asc.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParsedCommandDto {
	private String type;
	private String contactName;
	private String message;
	private String query;
	private Integer durationSeconds;
	private String appName;
	private String websiteName;
	private String url;
	private String rawText;
	private Double confidence;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CommandRequestDto {
		private String command;
	}
}
