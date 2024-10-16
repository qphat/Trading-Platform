package com.koomi.tradingplatfrom.controller;


import com.koomi.tradingplatfrom.domain.PaymentMethod;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.response.PaymentResponse;
import com.koomi.tradingplatfrom.service.PaymentService;
import com.koomi.tradingplatfrom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment/payment/{paymentMethod}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @RequestHeader("Authorization") String jwt,
            @PathVariable("paymentMethod") PaymentMethod paymentMethod,
            @PathVariable("amount") Long amount) {

        User user = userService.findUserProfileByJwt(jwt);

        PaymentResponse paymentResponse;


        if(paymentMethod.equals("RAZOR_PAY")) {
            paymentResponse = paymentService.createRazorpayPaymentLing(user, amount);
        }
        else {
            paymentResponse = paymentService.createStripePaymentLink(user, amount);
        }

        return ResponseEntity.ok(paymentResponse);


    }



}
