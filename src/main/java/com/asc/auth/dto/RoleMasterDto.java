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
public class RoleMasterDto extends UserDateAudit {
	public Long id;
	public Long roleId;
	public String description;
	public String name;
	public Long status;
	public List<ModulePermissionDto> modulePermissions;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@EqualsAndHashCode(callSuper = false)
	@Data
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ModulePermissionDto extends UserDateAudit {
		public Long id;
		public Long moduleId;
		public String moduleName;
		public String moduleIcon;
		public String moduleDescription;
		public Integer status;
		public Boolean active;
		public Boolean read;
		public Boolean write;
		public Boolean update;
		public Boolean isDefault;
		public Integer sortSequence;
		public String moduleEndpoint;
		public List<SubModulesDto> submodulePermissions;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		@EqualsAndHashCode(callSuper = false)
		@Data
		@ToString
		@NoArgsConstructor
		@AllArgsConstructor
		public static class SubModulesDto extends UserDateAudit {
			private Long id;
			public Long subModuleId;
			public String subModuleName;
			public String subModuleIcon;
			public String subModuleEndpoint;
			public Boolean read;
			public Boolean write;
			public Boolean update;
			public Long status;
			public Boolean isDefault;
			public Boolean active;
		}
	}
}