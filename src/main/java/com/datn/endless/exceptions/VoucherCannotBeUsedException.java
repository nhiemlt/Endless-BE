package com.datn.endless.exceptions;

public class VoucherCannotBeUsedException extends RuntimeException{
    public VoucherCannotBeUsedException(String message) {
        super(message);
    }
}
