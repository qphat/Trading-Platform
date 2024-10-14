package com.koomi.tradingplatfrom.repository;
import com.koomi.tradingplatfrom.model.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByUserId(Long userId);
}
