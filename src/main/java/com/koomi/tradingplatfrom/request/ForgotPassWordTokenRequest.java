package com.koomi.tradingplatfrom.request;

import com.koomi.tradingplatfrom.domain.VerificationType;
import lombok.Data;

@Data
public class ForgotPassWordTokenRequest {
    private String sendTo;
    private VerificationType verificationType;
}
