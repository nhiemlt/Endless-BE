package com.datn.endless.utils;

import lombok.Data;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ErrorResponse {
    private List<String> errors;

    // Constructor cho chuỗi đơn
    public ErrorResponse(String error) {
        this.errors = List.of(error); // Tạo danh sách từ một chuỗi
    }

    // Constructor cho danh sách chuỗi
    public ErrorResponse(List<String> errors) {
        this.errors = errors;
    }

    // Constructor cho danh sách ObjectError
    public ErrorResponse(String message, List<ObjectError> allErrors) {
        this.errors = allErrors.stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
        this.errors.add(0, message); // Thêm thông báo chính ở đầu danh sách
    }
}
