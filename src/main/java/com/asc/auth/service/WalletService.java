package com.asc.auth.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.asc.auth.dto.FiltersDto;
import com.asc.auth.dto.WalletDto;
import com.asc.auth.dto.WalletTransactionDto;
import com.asc.auth.model.Wallet;
import com.asc.auth.model.WalletTransaction;
import com.asc.auth.model.enums.TransactionType;
import com.asc.auth.repository.WalletRepository;
import com.asc.auth.repository.WalletTransactionRepository;
import com.asc.auth.security.TokenFilter;
import com.asc.auth.transformer.WalletTransactionFiltersTransformer;
import com.asc.auth.utils.Utils;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WalletService {

	@Autowired
	WalletRepository walletRepository;

	@Autowired
	WalletTransactionRepository walletTransactionRepository;

	public WalletDto getCurrentUserWallet() {
		return copyEntityToDto(walletRepository.findByUserId(TokenFilter.getGetUserId()).orElseGet(() -> {
			Wallet newWallet = Wallet.builder().userId(TokenFilter.getGetUserId()).balance(BigDecimal.ZERO).build();
			return walletRepository.save(newWallet);
		}));
	}

	private WalletDto copyEntityToDto(Wallet wallet) {
		WalletDto walletDto = new WalletDto();
		Utils.copyProperties(wallet, walletDto);
		return walletDto;
	}

	public List<WalletDto> getWalletListByUserIds(Set<Long> userIds) {
		List<Wallet> wallets = walletRepository.findAllByUserIdIn(userIds);
		return wallets.stream().map(this::copyEntityToDto).collect(Collectors.toList());
	}

	public WalletDto recharge(WalletDto walletDto) {
		// TODO Auto-generated method stub
		return null;
	}

	private WalletTransactionDto copyEntityToDto(WalletTransaction walletTransaction) {
		WalletTransactionDto transactionDto = new WalletTransactionDto();
		Utils.copyProperties(walletTransaction, transactionDto);
		return transactionDto;

	}

	@Transactional
	public WalletDto applyTransaction(Long userId, BigDecimal amount, TransactionType type, String reference) {
		Wallet wallet = walletRepository.findByUserId(userId).orElseGet(
				() -> walletRepository.save(Wallet.builder().userId(userId).balance(BigDecimal.ZERO).build()));
		if (TransactionType.DEBIT.equals(type)) {
			if (wallet.getBalance().compareTo(amount) < 0) {
				throw new RuntimeException("Insufficient balance");
			}
			wallet.setBalance(wallet.getBalance().subtract(amount));
		} else {
			wallet.setBalance(wallet.getBalance().add(amount));
		}
		walletRepository.save(wallet);
		WalletTransaction tx = new WalletTransaction();
		tx.setWallet(wallet);
		tx.setAmount(amount);
		tx.setType(type);
		tx.setReference(reference);
		walletTransactionRepository.save(tx);
		return copyEntityToDto(wallet);
	}

	public Page<WalletTransactionDto> geTransactions(List<FiltersDto> filters, Integer pageNumber, Integer pageSize,
			String sortingColumn, Direction direction) {
		Sort sorting = (Boolean.TRUE.equals(Objects.nonNull(sortingColumn))
				&& Boolean.TRUE.equals(Objects.nonNull(direction))) ? Sort.by(direction, sortingColumn)
						: Sort.by(Direction.ASC, "id");
		filters = (Boolean.FALSE.equals(filters.isEmpty())) ? filters : new ArrayList<>();
		pageSize = (Boolean.FALSE.equals(pageSize < 1)) ? pageSize : 5;
		Page<WalletTransaction> pageList = walletTransactionRepository.findAll(
				WalletTransactionFiltersTransformer.buildCriteria(filters),
				PageRequest.of(pageNumber, pageSize, sorting));
		if (!pageList.isEmpty()) {
			List<WalletTransactionDto> userDetail = pageList.stream().map(this::copyEntityToDto)
					.collect(Collectors.toList());
			Page<WalletTransactionDto> pageData = new PageImpl<>(userDetail,
					PageRequest.of(pageNumber, pageSize, sorting), pageList.getTotalElements());
			return pageData;
		}
		return null;
	}

}
