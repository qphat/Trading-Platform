package com.koomi.tradingplatfrom.controller;

import com.koomi.tradingplatfrom.domain.OrderType;
import com.koomi.tradingplatfrom.exception.OrderException;
import com.koomi.tradingplatfrom.model.entity.*;
import com.koomi.tradingplatfrom.request.CreateOrderRequest;
import com.koomi.tradingplatfrom.service.CoinService;
import com.koomi.tradingplatfrom.service.OrderService;
import com.koomi.tradingplatfrom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;


    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CreateOrderRequest req) {

        User user = userService.findUserProfileByJwt(jwt);
        Coin coin = coinService.findCoinById(req.getCoinId());

        Order order = orderService.processOrder(coin, req.getQuantity(), req.getOrderType(), user);

        return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId) {

        User user = userService.findUserProfileByJwt(jwt);

        Order order = orderService.getOrderById(orderId);
        if(order.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(order, HttpStatus.OK);
        } else {
            throw new OrderException("ypu dont have permission to access this order");
        }
    }

    @GetMapping("")
    public ResponseEntity<List<Order>> getAllOrderOfUser(
            @RequestHeader("Authorization") String jwt,
            @RequestParam OrderType orderType,
            @RequestParam String assetSymbol) {

        Long userId = userService.findUserProfileByJwt(jwt).getId();

        return new ResponseEntity<>(orderService.getAllOrderOfUser(userId, orderType, assetSymbol), HttpStatus.OK);
    }


}
