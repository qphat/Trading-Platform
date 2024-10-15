package com.koomi.tradingplatfrom.service.imp;

import com.koomi.tradingplatfrom.domain.WithdrawaStatus;
import com.koomi.tradingplatfrom.exception.WalletException;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.model.entity.Withdraw;
import com.koomi.tradingplatfrom.repository.WithdrawRepository;
import com.koomi.tradingplatfrom.service.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WithdrawServiceImp implements WithdrawService {

    @Autowired
    private WithdrawRepository withdrawRepository;


    @Override
    public Withdraw requestWithdraw(Long amount, User user) {
        Withdraw withdraw = new Withdraw();
        withdraw.setAmount(amount);
        withdraw.setUser(user);
        withdraw.setStatus(WithdrawaStatus.PENDING);

        return withdrawRepository.save(withdraw);
    }

    @Override
    public Withdraw processWithdraw(Long withdrawId, boolean accept) {
        Optional<Withdraw> withdraw = withdrawRepository.findById(withdrawId);

        if(withdraw.isEmpty()) {
            throw new WalletException("Withdraw not found");
        }

        Withdraw withdraw1 = withdraw.get();
        withdraw1.setDate(LocalDateTime.now());

        if(accept) {
            withdraw1.setStatus(WithdrawaStatus.COMPLETED);
        } else {
            withdraw1.setStatus(WithdrawaStatus.CANCELED);
        }

        return withdrawRepository.save(withdraw1);
    }

    @Override
    public List<Withdraw> getUserWithdrawsHistory(User user) {
        return withdrawRepository.findByUserId(user.getId());
    }


    @Override
    public List<Withdraw> getAllWithdrawsRequest() {
        return withdrawRepository.findAll();
    }
}
