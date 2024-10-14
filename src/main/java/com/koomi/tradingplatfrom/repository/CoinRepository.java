package com.koomi.tradingplatfrom.repository;

import com.koomi.tradingplatfrom.model.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin, String> {
}
