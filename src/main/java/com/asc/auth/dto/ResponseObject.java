package com.asc.auth.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "responseMessage", "responseStatus", "responseStatusCode", "responseObject" })
@ToString
@Setter
@Getter
public class ResponseObject<T> implements Serializable {
	private static final long serialVersionUID = -1984512988117794338L;
	@JsonProperty("responseMessage")
	private String responseMessage;
	@JsonProperty("responseStatus")
	private Boolean responseStatus;
	@JsonProperty("responseStatusCode")
	private Long responseStatusCode;
	@JsonProperty("responseObject")
	private transient T response;
	@JsonProperty("order")
	private transient T order;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Setter
	@Getter
	@ToString
	public static class Page<E> {
		@JsonProperty("content")
		private List<E> content;
	}
}
