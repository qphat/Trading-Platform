package com.koomi.tradingplatfrom.model.entity;

import com.koomi.tradingplatfrom.domain.VerificationType;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean isEnable = false;
    private VerificationType sendTo;
}
