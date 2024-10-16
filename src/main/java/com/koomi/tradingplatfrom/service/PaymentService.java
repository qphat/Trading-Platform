package com.koomi.tradingplatfrom.service;

import com.koomi.tradingplatfrom.domain.PaymentMethod;
import com.koomi.tradingplatfrom.model.entity.PaymentOrder;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.response.PaymentResponse;
import com.razorpay.RazorpayException;

public interface PaymentService {

    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id);

    Boolean ProcessPayment(PaymentOrder paymentOrder, String paymentId) throws RazorpayException;

    PaymentResponse createRazorpayPaymentLing(User user, Long amount);

    PaymentResponse createStripePaymentLink(User user, Long amount);
}
