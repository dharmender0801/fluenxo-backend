package com.asc.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asc.auth.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

	Optional<Wallet> findByUserId(Long getUserId);

	List<Wallet> findAllByUserIdIn(Set<Long> userIds);

}
