package com.datn.endless.exceptions;

public class ProductVersionAlreadyExistsException extends RuntimeException {
    public ProductVersionAlreadyExistsException(String message) {
        super(message);
    }
}

