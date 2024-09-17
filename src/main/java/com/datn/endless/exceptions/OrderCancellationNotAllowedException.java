package com.datn.endless.exceptions;

public class OrderCancellationNotAllowedException extends RuntimeException{
    public OrderCancellationNotAllowedException(String message) {
        super(message);
    }
}
