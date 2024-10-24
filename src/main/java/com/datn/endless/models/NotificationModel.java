package com.datn.endless.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NotificationModel {

    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Content is required")
    private String content;

    @Size(max = 50, message = "Type cannot exceed 50 characters")
    private String type = "Manual creation";

    @NotEmpty(message = "At least one user ID is required")
    private List<String> userIds;
}

