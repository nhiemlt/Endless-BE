package com.datn.endless.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NotificationModelForUser {

    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Content is required")
    private String content;

    @Size(max = 50, message = "Type cannot exceed 50 characters")
    @NotEmpty(message = "Type is required")
    private String type;

    @NotNull(message = "=user ID is required")
    private String userID;
}

