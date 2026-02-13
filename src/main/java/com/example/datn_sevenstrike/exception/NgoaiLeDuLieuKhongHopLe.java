package com.example.datn_sevenstrike.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NgoaiLeDuLieuKhongHopLe extends RuntimeException {
    public NgoaiLeDuLieuKhongHopLe(String message) {
        super(message);
    }
}

