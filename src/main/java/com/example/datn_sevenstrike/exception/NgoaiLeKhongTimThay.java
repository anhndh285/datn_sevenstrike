package com.example.datn_sevenstrike.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NgoaiLeKhongTimThay extends RuntimeException {
    public NgoaiLeKhongTimThay(String message) {
        super(message);
    }
}
