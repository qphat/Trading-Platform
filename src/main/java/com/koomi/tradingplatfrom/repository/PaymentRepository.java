package com.koomi.tradingplatfrom.repository;

import com.koomi.tradingplatfrom.model.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentOrder, Long> {

}
