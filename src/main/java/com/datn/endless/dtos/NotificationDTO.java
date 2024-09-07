package com.datn.endless.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
public class NotificationDTO {
    @Size(max = 255)
    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @Size(max = 50)
    @NotEmpty
    private String type;

    @NotEmpty
    private List<String> userIds;
}

