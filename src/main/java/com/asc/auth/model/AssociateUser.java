package com.asc.auth.model;

import java.math.BigDecimal;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = AssociateUser.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AssociateUser extends UserDateAudit {
	public static final String TABLE_NAME = "associated_user";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	private BigDecimal cpc;

	@JsonBackReference
	@ToString.Exclude
	@JoinColumn(name = "campaign_id")
	@ManyToOne
	@JsonIgnore
	private CampaignInfo campaign;

}
