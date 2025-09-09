package com.asc.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asc.auth.model.ModulePermissions;

public interface ModulePermissionsRepository extends JpaRepository<ModulePermissions, Long> {

	List<ModulePermissions> findAllByRoleId(Long roleId);

}
