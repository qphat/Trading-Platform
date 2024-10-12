package com.koomi.treadingplatfrom.exception;

public class TwoFactorAuthenticationRequiredException extends RuntimeException {
    public TwoFactorAuthenticationRequiredException(String message) {
        super(message);
    }
}
