package com.koomi.tradingplatfrom.service.imp;

import com.koomi.tradingplatfrom.model.entity.PaymentDetails;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.repository.PaymentDetaiRepository;
import com.koomi.tradingplatfrom.service.PaymentDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentDetailsServiceImp implements PaymentDetailsService {

    @Autowired
    private PaymentDetaiRepository paymentDetaiRepository;

    @Override
    public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String bankName, String ifscCode) {
        PaymentDetails paymentDetails = new PaymentDetails();

        paymentDetails.setAccountNumber(accountNumber);
        paymentDetails.setAccountHolderName(accountHolderName);
        paymentDetails.setBankName(bankName);
        paymentDetails.setIfsc(ifscCode);

        return paymentDetaiRepository.save(paymentDetails);
    }

    @Override
    public PaymentDetails getUserPaymentDetails(User user) {
        return paymentDetaiRepository.findByUserId(user.getId());
    }

}
