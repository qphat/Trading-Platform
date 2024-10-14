package com.koomi.tradingplatfrom.service;

import com.koomi.tradingplatfrom.model.entity.Order;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.model.entity.Wallet;


public interface WalletService {
    Wallet getUserWallet(User userId);

    Wallet addBalanceToWallet(Wallet wallet, double money);

    Wallet findWalletByUserId(Long Id);

    Wallet walletToWalletTransfer(User sender, Wallet receverWallet, double money);

    Wallet payOderPayment(Order order, User user);

}
