package com.asc.auth.model;

import java.util.List;

import com.asc.auth.model.audit.UserDateAudit;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = ModuleMaster.TABLE_NAME)
@ToString
public class ModuleMaster extends UserDateAudit {
	public static final String TABLE_NAME = "module_master";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String moduleName;
	private String moduleDescription;
	private String moduleIcon;
	private Integer status;
	private String moduleEndpoint;
	private Integer sortSequence;
	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "moduleMaster", cascade = { CascadeType.ALL })
	@EqualsAndHashCode.Exclude
	List<SubModuleMaster> subModules;
}