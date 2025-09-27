package com.asc.auth.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.asc.auth.dto.FiltersDto;
import com.asc.auth.dto.PaymentLogDto;
import com.asc.auth.dto.WalletTransactionDto;
import com.asc.auth.exception.RecordNotFoundException;
import com.asc.auth.model.PaymentLog;
import com.asc.auth.model.Wallet;
import com.asc.auth.model.WalletTransaction;
import com.asc.auth.model.enums.TransactionType;
import com.asc.auth.repository.PaymentLogRepository;
import com.asc.auth.repository.WalletRepository;
import com.asc.auth.repository.WalletTransactionRepository;
import com.asc.auth.transformer.PaymentLogsFiltersTransfromer;
import com.asc.auth.transformer.WalletTransactionFiltersTransformer;
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

		}
		Utils.copyProperties(paymentLogDto, log);
		paymentLogRepository.save(log);
		return copyEntityInDto(log);
	}

	private PaymentLogDto copyEntityInDto(PaymentLog log) {
		PaymentLogDto paymentLogDto = new PaymentLogDto();
		Utils.copyProperties(log, paymentLogDto);
		return paymentLogDto;
	}

	public Page<PaymentLogDto> getReachargeLogs(List<FiltersDto> filters, Integer pageNumber, Integer pageSize,
			String sortingColumn, Direction direction) {
		Sort sorting = (Boolean.TRUE.equals(Objects.nonNull(sortingColumn))
				&& Boolean.TRUE.equals(Objects.nonNull(direction))) ? Sort.by(direction, sortingColumn)
						: Sort.by(Direction.ASC, "id");
		filters = (Boolean.FALSE.equals(filters.isEmpty())) ? filters : new ArrayList<>();
		pageSize = (Boolean.FALSE.equals(pageSize < 1)) ? pageSize : 5;
		Page<PaymentLog> pageList = paymentLogRepository.findAll(PaymentLogsFiltersTransfromer.buildCriteria(filters),
				PageRequest.of(pageNumber, pageSize, sorting));
		if (!pageList.isEmpty()) {
			List<PaymentLogDto> userDetail = pageList.stream().map(this::copyEntityInDto).collect(Collectors.toList());
			Page<PaymentLogDto> pageData = new PageImpl<>(userDetail, PageRequest.of(pageNumber, pageSize, sorting),
					pageList.getTotalElements());
			return pageData;
		}
		return null;
	}

}
