package com.asc.auth.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.asc.auth.model.RoleMaster;

public interface RoleMasterRepository extends JpaRepository<RoleMaster, Long> {

	Optional<RoleMaster> findByName(String name);

	Page<RoleMaster> findAll(Specification<?> criteria, Pageable pageable);

}