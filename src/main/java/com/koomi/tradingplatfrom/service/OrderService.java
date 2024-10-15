package com.koomi.tradingplatfrom.service;

import com.koomi.tradingplatfrom.domain.OrderStatus;
import com.koomi.tradingplatfrom.domain.OrderType;
import com.koomi.tradingplatfrom.model.entity.Coin;
import com.koomi.tradingplatfrom.model.entity.Order;
import com.koomi.tradingplatfrom.model.entity.OrderItem;
import com.koomi.tradingplatfrom.model.entity.User;


import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId);

    List<Order> getAllOrderOfUser(Long userId, OrderType orderType, String assetSymbol);

    Order processOrder(Coin coin, double quantity, OrderType orderType, User user);



}
