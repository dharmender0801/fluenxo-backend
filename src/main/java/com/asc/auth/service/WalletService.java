package com.asc.auth.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asc.auth.dto.WalletDto;
import com.asc.auth.model.Wallet;
import com.asc.auth.repository.WalletRepository;
import com.asc.auth.security.TokenFilter;
import com.asc.auth.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WalletService {

	@Autowired
	WalletRepository walletRepository;

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

}
