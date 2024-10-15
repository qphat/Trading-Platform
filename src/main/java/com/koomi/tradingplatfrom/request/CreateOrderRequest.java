package com.koomi.tradingplatfrom.request;

import com.koomi.tradingplatfrom.domain.OrderType;
import lombok.Data;

@Data
public class CreateOrderRequest {
    private  String coinId;
    private double quantity;
    private OrderType orderType;
}
