package com.asc.auth.dto;

import java.util.List;

import com.asc.auth.model.audit.UserDateAudit;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = false)
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ModulesDto extends UserDateAudit {
	private Long id;
	private String moduleName;
	private String moduleDescription;
	private String moduleIcon;
	private Integer status;
	private String moduleEndpoint;
	private Integer sortSequence;
	List<SubModuleDto> subModuleMapping;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@EqualsAndHashCode(callSuper = false)
	@Data
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SubModuleDto extends UserDateAudit {
		private Long id;
		private Long moduleId;
		private String subModuleName;
		private String subModuleDescription;
		private String subModuleIcon;
		private String subModuleEndpoint;
		private Integer status;
	}
}