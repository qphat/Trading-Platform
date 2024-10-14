package com.koomi.tradingplatfrom.repository;

import com.koomi.tradingplatfrom.model.entity.ForgotPassWordToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassWordToken, Long> {
    ForgotPassWordToken findByUserId(Long userId);

}
