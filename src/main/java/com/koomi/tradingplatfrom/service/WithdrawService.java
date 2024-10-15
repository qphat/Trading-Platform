package com.koomi.tradingplatfrom.service;

import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.model.entity.Withdraw;

import java.util.List;

public interface WithdrawService {

    Withdraw requestWithdraw(Long amount, User user);

    Withdraw processWithdraw(Long withdrawId, boolean accept);

    List<Withdraw> getUserWithdrawsHistory(User user);

    List<Withdraw> getAllWithdrawsRequest();




}
