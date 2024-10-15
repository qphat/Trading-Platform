package com.koomi.tradingplatfrom.service.imp;


import com.koomi.tradingplatfrom.domain.OrderType;
import com.koomi.tradingplatfrom.exception.WalletException;
import com.koomi.tradingplatfrom.model.entity.Order;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.model.entity.Wallet;
import com.koomi.tradingplatfrom.repository.WalletRepository;
import com.koomi.tradingplatfrom.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletServiceImp implements WalletService {

    @Autowired
    private WalletRepository walletRepository;



    @Override
    public Wallet getUserWallet(User userId) {
        Wallet wallet = walletRepository.findByUserId(userId.getId());
        if(wallet == null){
            wallet = new Wallet();
            wallet.setUser(userId);
            throw new WalletException("Wallet not found");
        }

        return wallet;
    }

    @Override
    public Wallet addBalanceToWallet(Wallet wallet, double money) {
        BigDecimal balance = wallet.getBalance();
        balance = balance.add(BigDecimal.valueOf(money));
        wallet.setBalance(balance);
        return walletRepository.save(wallet);

    }

    @Override
    public Wallet findWalletByUserId(Long id) {
        Optional<Wallet> wallet = walletRepository.findById(id);
        if(wallet.isPresent()){
            return wallet.get();
        }

        throw new WalletException("Wallet not found");

    }

    @Override
    public Wallet walletToWalletTransfer(User sender, Wallet receverWallet, double money) {
        Wallet senderWallet = getUserWallet(sender);
        BigDecimal senderBalance = senderWallet.getBalance();
        BigDecimal receverBalance = receverWallet.getBalance();
        if(senderBalance.compareTo(BigDecimal.valueOf(money)) < 0){
            throw new WalletException("Insufficient balance");
        }
        senderBalance = senderBalance.subtract(BigDecimal.valueOf(money));
        receverBalance = receverBalance.add(BigDecimal.valueOf(money));
        senderWallet.setBalance(senderBalance);
        receverWallet.setBalance(receverBalance);
        walletRepository.save(senderWallet);
        walletRepository.save(receverWallet);

        return senderWallet;
    }

    @Override
    public Wallet payOderPayment(Order order, User user) {
        Wallet wallet = getUserWallet(user);
        if(order.getOrderType().equals(OrderType.BUY)) {
            BigDecimal newBalance = wallet.getBalance().subtract(order.getPrice());
            if(newBalance.compareTo(order.getPrice()) < 0) {
                throw new WalletException("Insuffecient funds for this transaction");
            }
            wallet.setBalance(newBalance);
        } else {
            BigDecimal newBalance = wallet.getBalance().add(order.getPrice());
            wallet.setBalance(newBalance);
        }
        walletRepository.save(wallet);
        return wallet;
    }
}
