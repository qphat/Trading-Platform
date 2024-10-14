package com.koomi.tradingplatfrom.service;

import com.koomi.tradingplatfrom.model.entity.TwoFactorOTP;
import com.koomi.tradingplatfrom.model.entity.User;

public interface TwoFactorOtpService {

    TwoFactorOTP createTwoFactorOTP(User user, String otp, String jwt);

        TwoFactorOTP findByUser(Long userId);

        TwoFactorOTP findById(String id);

        boolean verifyTwoFactorOTP(TwoFactorOTP twoFactorOTP, String otp);

        void deleteTwoFactorOTP(TwoFactorOTP twoFactorOTP);

        TwoFactorOTP save(TwoFactorOTP twoFactorOTP);


}
