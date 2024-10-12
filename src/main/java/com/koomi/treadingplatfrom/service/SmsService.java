//package com.koomi.treadingplatfrom.service;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SmsService {
//
//    @Value("${twilio.account_sid}")
//    private String ACCOUNT_SID;
//
//    @Value("${twilio.auth_token}")
//    private String AUTH_TOKEN;
//
//    @Value("${twilio.phone_number}")
//    private String FROM_NUMBER;
//
//    public void sendSms(String to, String messageBody) {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        Message message = Message.creator(
//                        new PhoneNumber(to),
//                        new PhoneNumber(FROM_NUMBER),
//                        messageBody)
//                .create();
//        System.out.println("SMS sent with SID: " + message.getSid());
//    }
//
//    public void sendOtp(String to, String otp) {
//        String messageBody = String.format(
//                "Your OTP is: %s. This OTP will expire in 5 minutes. Do not share this OTP with anyone.",
//                otp
//        );
//        sendSms(to, messageBody);
//    }
//}