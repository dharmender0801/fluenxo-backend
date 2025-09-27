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
import com.asc.auth.dto.WalletDto;
import com.asc.auth.dto.WalletTransactionDto;
import com.asc.auth.model.Wallet;
import com.asc.auth.model.WalletTransaction;
import com.asc.auth.repository.WalletRepository;
import com.asc.auth.repository.WalletTransactionRepository;
import com.asc.auth.security.TokenFilter;
import com.asc.auth.transformer.WalletTransactionFiltersTransformer;
import com.asc.auth.utils.Utils;

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

	public WalletDto recharge(WalletDto walletDto) {
		// TODO Auto-generated method stub
		return null;
	}

	private WalletTransactionDto copyEntityToDto(WalletTransaction walletTransaction) {
		WalletTransactionDto transactionDto = new WalletTransactionDto();
		Utils.copyProperties(walletTransaction, transactionDto);
		return transactionDto;

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
