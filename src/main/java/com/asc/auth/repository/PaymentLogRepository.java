package com.asc.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asc.auth.model.PaymentLog;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {

}
