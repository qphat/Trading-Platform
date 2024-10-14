package com.koomi.tradingplatfrom.controller;

import com.koomi.tradingplatfrom.model.entity.Order;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.model.entity.Wallet;
import com.koomi.tradingplatfrom.model.entity.WalletTransaction;
import com.koomi.tradingplatfrom.service.UserService;
import com.koomi.tradingplatfrom.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @GetMapping("api/wallet")
    public ResponseEntity<Wallet> getUserWallet(
            @RequestHeader("Authorization") String jwt){

        User user = userService.findUserProfileByJwt(jwt);

        Wallet wallet = walletService.getUserWallet(user);

        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }

    @PutMapping("api/wallet/${walletId}/transfer")
    public ResponseEntity<Wallet> walletToWalletTranfer(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long walletId,
            @RequestBody WalletTransaction walletTransaction){

        User sender = userService.findUserProfileByJwt(jwt);
        Wallet receverWallet = walletService.findWalletByUserId(walletId);
        Wallet wallet = walletService.walletToWalletTransfer(sender, receverWallet, walletTransaction.getAmount());

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("api/wallet/order/{orderId}/payment")
    public ResponseEntity<Wallet> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId){

        User sender = userService.findUserProfileByJwt(jwt);
        Order order = orderSetvice.findOrderById(orderId);

        Wallet wallet = walletService.payOderPayment(order, sender);

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }







}
