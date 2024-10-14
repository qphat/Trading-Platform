package com.koomi.tradingplatfrom.service;

import com.koomi.tradingplatfrom.domain.VerificationType;
import com.koomi.tradingplatfrom.model.entity.ForgotPassWordToken;
import com.koomi.tradingplatfrom.model.entity.User;

public interface ForgotPasswordService {
    ForgotPassWordToken createForgotPasswordToken(User user,
                                                  String otp,
                                                  VerificationType verificationType,
                                                  String sendTo);

    ForgotPassWordToken findById(Long id);

    ForgotPassWordToken findByUser(Long userId);

    void deleteToken(ForgotPassWordToken token);

}
