package com.koomi.tradingplatfrom.service.imp;

import com.koomi.tradingplatfrom.domain.PaymentMethod;
import com.koomi.tradingplatfrom.domain.PaymentOrderStatus;
import com.koomi.tradingplatfrom.exception.PaymentOrderException;
import com.koomi.tradingplatfrom.model.entity.PaymentOrder;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.repository.PaymentRepository;
import com.koomi.tradingplatfrom.response.PaymentResponse;
import com.koomi.tradingplatfrom.service.PaymentService;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.param.checkout.SessionCreateParams;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImp implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecretKey;

    @Override
    public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod) {

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setAount(amount);
        paymentOrder.setUser(user);
        paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.PENDING);

        return paymentRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentOrderException("payment order not found"));
    }


    @Override
    public Boolean ProcessPayment(PaymentOrder paymentOrder, String paymentId) throws RazorpayException {
        if(paymentOrder.getPaymentOrderStatus().equals(PaymentOrderStatus.PENDING)){
            if(paymentOrder.getPaymentOrderStatus().equals(PaymentMethod.RAZOR_PAY)) {
                RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecretKey);
                Payment payment = razorpayClient.payments.fetch(paymentId);

                Integer amount = payment.get("amount");
                String status = payment.get("status");

                if (status.equals("captured") && amount.equals(paymentOrder.getAount())) {
                    paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.SUCCESS);
                    return true;
                }
                paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.FAILED);
                paymentRepository.save(paymentOrder);
                return false;
            }
            paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.SUCCESS);
            paymentRepository.save(paymentOrder);
            return true;
        }

        return false;
    }

    @Override
    public PaymentResponse createRazorpayPaymentLing(User user, Long amount) {

        Long amountInPaise = amount * 100;

        try {
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecretKey);

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amountInPaise);
            paymentLinkRequest.put("currency", "INR");

            JSONObject customer = new JSONObject();
            customer.put("name", user.getFullName());
            customer.put("contact", user.getPhoneNumber());

            paymentLinkRequest.put("customer", customer);


            // create JSON notifical setting
            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);

            //set the remider setting
            paymentLinkRequest.put("reminder_enable", true);

            // Set the callback URL and method
            paymentLinkRequest.put("callback_url", "https://example.com/callback");
            paymentLinkRequest.put("callback_method", "get");

            // Create the payment link using the payment link.create() method
            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);

            String paymentLinkId = paymentLink.get("id");
            String paymentLinkUrl = paymentLink.get("short_url");

            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setPaymentLink(paymentLinkUrl);

            return paymentResponse;


        } catch (RazorpayException e) {
            System.out.println("Error: " + e.getMessage());
            throw new PaymentOrderException("Error while creating payment link");
        }
    }

    @Override
    public PaymentResponse createStripePaymentLink(User user, Long amount) {

        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://example.com/success")
                .setCancelUrl("https://example.com/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amount*100)
                                                .setProductData(
                                                        SessionCreateParams.
                                                                LineItem.
                                                                PriceData.
                                                                ProductData.
                                                                builder()
                                                                .setName("Donation")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        try {
            com.stripe.model.checkout.Session session = com.stripe.model.checkout.Session.create(params);

            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setPaymentLink(session.getUrl());

            return paymentResponse;

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new PaymentOrderException("Error while creating payment link");
        }
    }
}
