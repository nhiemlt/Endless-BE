package com.datn.endless.exceptions;

public class ProductVersionConflictException extends RuntimeException {
    public ProductVersionConflictException(String message) {
        super(message);
    }
}
