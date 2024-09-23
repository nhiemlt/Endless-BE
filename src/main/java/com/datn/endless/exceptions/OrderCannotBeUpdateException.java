package com.datn.endless.exceptions;

public class OrderCannotBeUpdateException extends RuntimeException {
    public OrderCannotBeUpdateException(String message) {
        super(message);
    }
}
