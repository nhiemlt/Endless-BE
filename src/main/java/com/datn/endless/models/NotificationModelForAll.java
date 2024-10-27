package com.datn.endless.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NotificationModelForAll {
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    @NotEmpty(message = "Tiêu đề là bắt buộc")
    private String title;

    @NotEmpty(message = "Nội dung là bắt buộc")
    private String content;
}

