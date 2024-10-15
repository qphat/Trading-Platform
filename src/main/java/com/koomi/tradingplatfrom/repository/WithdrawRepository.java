package com.koomi.tradingplatfrom.repository;

import com.koomi.tradingplatfrom.model.entity.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
    List<Withdraw> findByUserId(Long userId);
}
