package com.koomi.tradingplatfrom.repository;

import com.koomi.tradingplatfrom.model.entity.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetaiRepository extends JpaRepository<PaymentDetails, Long> {

    PaymentDetails findByUserId(Long userId);
}
