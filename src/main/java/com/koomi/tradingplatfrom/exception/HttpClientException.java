package com.koomi.tradingplatfrom.exception;

import org.springframework.http.HttpStatus;

public class HttpClientException extends RuntimeException {
    private HttpStatus status;

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}