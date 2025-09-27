package com.asc.auth.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asc.auth.dto.PaymentLogDto;
import com.asc.auth.exception.RecordNotFoundException;
import com.asc.auth.model.PaymentLog;
import com.asc.auth.model.Wallet;
import com.asc.auth.model.WalletTransaction;
import com.asc.auth.model.enums.TransactionType;
import com.asc.auth.repository.PaymentLogRepository;
import com.asc.auth.repository.WalletRepository;
import com.asc.auth.repository.WalletTransactionRepository;
import com.asc.auth.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentService {
	@Autowired
	PaymentLogRepository paymentLogRepository;
	@Autowired
	RazorpayService razorpayService;
	@Autowired
	WalletRepository walletRepository;

	@Autowired
	WalletTransactionRepository walletTransactionRepository;

	public PaymentLogDto updatePaymentLogs(PaymentLogDto paymentLogDto) throws Exception {
		PaymentLog log = paymentLogRepository.findById(paymentLogDto.getId())
				.orElseThrow(() -> new RecordNotFoundException("Order not found"));
		if (razorpayService.verifyPayment(paymentLogDto.getOrderId(), paymentLogDto.getPaymentId(),
				paymentLogDto.getRazorpaySignature())) {
			log.setPaymentId(paymentLogDto.getPaymentId());
			log.setStatus(TransactionType.CREDIT);
			paymentLogRepository.save(log);

			Wallet wallet = walletRepository.findByUserId(log.getUserId()).orElseGet(() -> walletRepository
					.save(Wallet.builder().userId(log.getUserId()).balance(BigDecimal.ZERO).build()));
			wallet.setBalance(wallet.getBalance().add(log.getAmount()));
			walletRepository.save(wallet);

			WalletTransaction tx = new WalletTransaction();
			tx.setWallet(wallet);
			tx.setAmount(log.getAmount());
			tx.setType(TransactionType.CREDIT);
			tx.setReference("Razorpay Payment: " + paymentLogDto.getPaymentId());
			walletTransactionRepository.save(tx);
			return copyEntityInDto(log);
		}
		return null;
	}

	private PaymentLogDto copyEntityInDto(PaymentLog log) {
		PaymentLogDto paymentLogDto = new PaymentLogDto();
		Utils.copyProperties(log, paymentLogDto);
		return paymentLogDto;
	}

}
