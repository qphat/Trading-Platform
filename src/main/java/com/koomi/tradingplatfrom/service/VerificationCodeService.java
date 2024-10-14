package com.koomi.tradingplatfrom.service;

import com.koomi.tradingplatfrom.domain.VerificationType;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.model.entity.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user , VerificationType verificationType);

    VerificationCode getVerificationCodeById(Long Id);

    VerificationCode getVerificationCodeByUserId(Long userId);


    void deleteVerificationCodeById(VerificationCode verificationCode);
}
