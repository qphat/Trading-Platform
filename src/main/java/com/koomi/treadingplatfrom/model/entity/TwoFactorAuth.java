package com.koomi.treadingplatfrom.model.entity;

import com.koomi.treadingplatfrom.domain.VariationType;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean isEnable = false;
    private VariationType sendTo;
}
