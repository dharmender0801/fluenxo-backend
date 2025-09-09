package com.asc.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asc.auth.model.RoleMaster;

public interface RoleMasterRepository extends JpaRepository<RoleMaster, Long> {

	Optional<RoleMaster> findByName(String name);

}