package com.koomi.tradingplatfrom.service.imp;

import com.koomi.tradingplatfrom.domain.VerificationType;
import com.koomi.tradingplatfrom.model.entity.ForgotPassWordToken;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.repository.ForgotPasswordRepository;
import com.koomi.tradingplatfrom.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgotPasswordServiceImp implements ForgotPasswordService {

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Override
    public ForgotPassWordToken createForgotPasswordToken(User user, String otp, VerificationType verificationType, String sendTo) {
        ForgotPassWordToken forgotPassWordToken = new ForgotPassWordToken();
        forgotPassWordToken.setUser(user);
        forgotPassWordToken.setSendTo(sendTo);
        forgotPassWordToken.setOtp(otp);
        forgotPassWordToken.setVerificationType(verificationType);

        return forgotPasswordRepository.save(forgotPassWordToken);
    }

    @Override
    public ForgotPassWordToken findById(Long id) {
        Optional<ForgotPassWordToken> token = forgotPasswordRepository.findById(id);  // Sửa lại, không gọi lặp `findById(id)`
        return token.orElse(null);
    }


    @Override
    public ForgotPassWordToken findByUser(Long userId) {
        return forgotPasswordRepository.findByUserId(userId);
    }

    @Override
    public void deleteToken(ForgotPassWordToken token) {
        forgotPasswordRepository.delete(token);
    }


}
