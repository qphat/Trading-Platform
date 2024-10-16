package com.koomi.tradingplatfrom.service;

import com.koomi.tradingplatfrom.model.entity.PaymentDetails;
import com.koomi.tradingplatfrom.model.entity.User;

public interface PaymentDetailsService {
    public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String bankName, String ifscCode);

    public PaymentDetails getUserPaymentDetails(User user);
}
