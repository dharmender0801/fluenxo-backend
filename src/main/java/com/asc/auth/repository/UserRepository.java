package com.asc.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.asc.auth.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByEmailOrUserName(String email, String user);

	Optional<User> findByEmailOrUserNameOrMobile(String email, String user, String mobile);

	Optional<User> findByEmailAndMobile(String email, String mobile);

	Boolean existsByEmailAndEmailVerified(String email, Boolean flag);

	Boolean existsByMobileAndMobileVerified(String mobile, Boolean flag);

	Optional<User> findByMobile(String mobile);

	Optional<User> findByUserName(String user);

//	Optional<List<User>> findAllByCenterCodeAndUserRole(String centerCode, Integer roleUser);

//	Optional<User> findByAccount(String apiKey);

	Page<User> findAll(Specification<?> build, Pageable pageable);

}
