package com.asc.auth.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = ModulePermissions.TABLE_NAME)
@ToString
public class ModulePermissions {

	public static final String TABLE_NAME = "module_permissions";

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
	@ManyToOne
	@JoinColumn(name = "role_id")
	private RoleMaster role;

	@JsonBackReference
	@ToString.Exclude
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "module_id")
	private ModuleMaster moduleMaster;

	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modulePermission", cascade = { CascadeType.ALL })
	@EqualsAndHashCode.Exclude
	List<SubmodulePermissions> submodulePermission = new ArrayList<>();

	public Long getModuleId() {
		return moduleMaster.getId();
	}

	public Integer getSortSequence() {
		return moduleMaster.getSortSequence();
	}

	public String getModuleEndpoint() {
		return moduleMaster.getModuleEndpoint();
	}

	public String getModuleName() {
		return moduleMaster.getModuleName();
	}

	public String getModuleDescription() {
		return moduleMaster.getModuleDescription();
	}

	public String getModuleIcon() {
		return moduleMaster.getModuleIcon();
	}

	public Integer getStatus() {
		return moduleMaster.getStatus();
	}

}