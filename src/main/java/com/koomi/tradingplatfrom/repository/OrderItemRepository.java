package com.koomi.tradingplatfrom.repository;

import com.koomi.tradingplatfrom.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
