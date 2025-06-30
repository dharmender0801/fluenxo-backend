package com.asc.auth.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.asc.auth.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByEmailOrUserNameOrMobile(String email, String user, String mobile);

	Optional<User> findByMobile(String mobile);

	Optional<User> findByUserName(String user);

	Page<User> findAll(Specification<?> build, Pageable pageable);

}
