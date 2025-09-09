package com.asc.auth.model;

import com.asc.auth.model.audit.UserDateAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = SubModuleMaster.TABLE_NAME)
@ToString
public class SubModuleMaster extends UserDateAudit {
	public static final String TABLE_NAME = "submodule_master";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String subModuleName;
	private String subModuleDescription;
	private String subModuleIcon;
	private String subModuleEndpoint;
	private Integer status;

	@JsonBackReference
	@ToString.Exclude
	@JoinColumn(name = "module_id")
	@ManyToOne
	@JsonIgnore
	private ModuleMaster moduleMaster;
}