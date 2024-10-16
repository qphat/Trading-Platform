package com.koomi.tradingplatfrom.request;

import lombok.Data;

@Data
public class PaymentDetailsRequest {
    private String accountNumber;
    private String accountHolderName;
    private String bankName;
    private String ifscCode;

}
