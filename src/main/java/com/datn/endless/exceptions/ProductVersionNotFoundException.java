package com.datn.endless.exceptions;

public class ProductVersionNotFoundException extends RuntimeException{
    public ProductVersionNotFoundException(String message) {
        super(message);
    }
}
