package com.datn.endless.exceptions;

public class OrderCannotBeCancelledException extends RuntimeException {
    public OrderCannotBeCancelledException(String message) {
        super(message);
    }
}
