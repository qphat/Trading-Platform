package com.koomi.tradingplatfrom.exception;

public class TwoFactorAuthenticationRequiredException extends RuntimeException {
    public TwoFactorAuthenticationRequiredException(String message) {
        super(message);
    }
}
