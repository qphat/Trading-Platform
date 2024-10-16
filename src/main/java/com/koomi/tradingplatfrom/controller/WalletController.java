package com.koomi.tradingplatfrom.controller;

import com.koomi.tradingplatfrom.model.entity.*;
import com.koomi.tradingplatfrom.response.PaymentResponse;
import com.koomi.tradingplatfrom.service.OrderService;
import com.koomi.tradingplatfrom.service.PaymentService;
import com.koomi.tradingplatfrom.service.UserService;
import com.koomi.tradingplatfrom.service.WalletService;
import com.razorpay.RazorpayException;
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

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;


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
    public ResponseEntity<Wallet> addBalanceToWallet(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(name = "orderId") Long orderId,
            @RequestParam(name = "payment_id") String paymentId) throws RazorpayException {

        User sender = userService.findUserProfileByJwt(jwt);

        Wallet wallet = walletService.getUserWallet(sender);

        PaymentOrder paymentOrder = paymentService.getPaymentOrderById(orderId);

        Boolean status = paymentService.ProcessPayment(paymentOrder, paymentId);

       if(status) {
           wallet = walletService.addBalanceToWallet(wallet, paymentOrder.getAount());

       }

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

}
