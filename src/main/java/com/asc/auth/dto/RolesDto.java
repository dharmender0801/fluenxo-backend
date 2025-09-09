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
public class RolesDto extends UserDateAudit {
	private Long id;
	private String name;
	private String description;
	private Boolean read;
	private Boolean write;
	private Boolean update;
	private Boolean defaultRoute;
	private List<RolePermissionsDto> rolePermissions;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@EqualsAndHashCode(callSuper = false)
	@Data
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RolePermissionsDto extends UserDateAudit {
		private Long id;
		private Long roleId;
		private Long moduleId;
		private String moduleName;
		private Long submoduleId;
		private String subModuleName;
		private Boolean read;
		private Boolean write;
		private Boolean update;
		private Boolean isDefault;
	}
}