package com.koomi.tradingplatfrom.controller;

import com.koomi.tradingplatfrom.domain.WalletTransactionType;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.model.entity.Wallet;
import com.koomi.tradingplatfrom.model.entity.WalletTransaction;
import com.koomi.tradingplatfrom.model.entity.Withdraw;
import com.koomi.tradingplatfrom.service.UserService;
import com.koomi.tradingplatfrom.service.WalletService;
import com.koomi.tradingplatfrom.service.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WithdrawController {

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    UserService userService;

    @Autowired
    WalletService walletService;

//    @Autowired
//    private WalletTransactionService walletTransactionService;

    @PostMapping("/api/withdraw/{amount}")
    public ResponseEntity<?> requestWithdraw(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long amount) {

        User user = userService.findUserProfileByJwt(jwt);
        Wallet userWallet = walletService.getUserWallet(user);

        Withdraw withdraw = withdrawService.requestWithdraw(amount, user);
        walletService.addBalanceToWallet(userWallet, -withdraw.getAmount());


        return new ResponseEntity<>(withdraw, HttpStatus.CREATED);

    }

    @PatchMapping("/api/admin/withdraw/{id}/process/{accept}")
    public ResponseEntity<?> processWithdraw(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id,
            @PathVariable boolean accept) {

        User user = userService.findUserProfileByJwt(jwt);
        Withdraw withdraw = withdrawService.processWithdraw(id, accept);

        Wallet userWallet = walletService.getUserWallet(user);

        if(!accept) {
            walletService.addBalanceToWallet(userWallet, withdraw.getAmount());
        }

        return new ResponseEntity<>(withdraw, HttpStatus.ACCEPTED);
    }

    @GetMapping("/api/admin/withdraw")
    public ResponseEntity<?> getAllWithdrawsRequest(
            @RequestHeader("Authorization") String jwt) {

        User user = userService.findUserProfileByJwt(jwt);

        return new ResponseEntity<>(withdrawService.getAllWithdrawsRequest(), HttpStatus.OK);
    }

}
