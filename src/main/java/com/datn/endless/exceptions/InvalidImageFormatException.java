package com.datn.endless.exceptions;

public class InvalidImageFormatException extends RuntimeException { // Change here
    public InvalidImageFormatException(String message) {
        super(message);
    }
}
