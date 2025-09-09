package com.asc.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
@Entity
@Table(name = SubmodulePermissions.TABLE_NAME)
@ToString
public class SubmodulePermissions {

	public static final String TABLE_NAME = "submodule_permissions";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "`read`", columnDefinition = "int")
	private Boolean read;
	@Column(name = "`write`", columnDefinition = "int")
	private Boolean write;
	@Column(name = "`update`", columnDefinition = "int")
	private Boolean update;
	@Column(columnDefinition = "int")
	private Boolean isDefault;

	@JsonBackReference
	@ToString.Exclude
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "submodule_id")
	private SubModuleMaster submoduleMaster;

	@JsonBackReference
	@ToString.Exclude
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "module_permission_id")
	private ModulePermissions modulePermission;

	public Long getSubModuleId() {
		return submoduleMaster.getId();
	}

	public String getSubModuleName() {
		return submoduleMaster.getSubModuleName();
	}

	public String getSubModuleDescription() {
		return submoduleMaster.getSubModuleDescription();
	}

	public String getSubModuleIcon() {
		return submoduleMaster.getSubModuleIcon();
	}

	public String getSubModuleEndpoint() {
		return submoduleMaster.getSubModuleEndpoint();
	}

	public Integer getStatus() {
		return submoduleMaster.getStatus();
	}

}
