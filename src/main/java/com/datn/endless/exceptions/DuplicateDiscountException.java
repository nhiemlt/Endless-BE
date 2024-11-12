package com.datn.endless.exceptions;

public class DuplicateDiscountException extends RuntimeException {
    public DuplicateDiscountException(String message) {
        super(message);
    }
}