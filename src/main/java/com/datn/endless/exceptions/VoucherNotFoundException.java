package com.datn.endless.exceptions;

public class VoucherNotFoundException extends RuntimeException{
    public VoucherNotFoundException(String message) {
        super(message);
    }
}
