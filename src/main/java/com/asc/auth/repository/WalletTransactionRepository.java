package com.asc.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asc.auth.model.WalletTransaction;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

}
