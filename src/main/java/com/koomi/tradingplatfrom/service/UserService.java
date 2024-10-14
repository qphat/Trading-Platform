package com.koomi.tradingplatfrom.service;

import com.koomi.tradingplatfrom.domain.VerificationType;
import com.koomi.tradingplatfrom.model.entity.User;


public interface UserService  {
    User findUserProfileByJwt(String jwt);
    User findUserByEmail(String email);
    User findUserById(Long id);

    User enableTwoFactorAuth(VerificationType verificationType,String sendTo, User user);

    User updatePassword(User user, String newPassword);


}
