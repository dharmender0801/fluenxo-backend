package com.asc.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asc.auth.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

	Optional<Wallet> findByUserId(Long getUserId);

}
