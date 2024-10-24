package com.datn.endless.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NotificationModel {

    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    @NotEmpty(message = "Tiêu đề là bắt buộc")
    private String title;

    @NotEmpty(message = "Nội dung là bắt buộc")
    private String content;

    @Size(max = 50, message = "Loại không được vượt quá 50 ký tự")
    private String type = "Tạo thủ công";

    @NotEmpty(message = "Yêu cầu ít nhất một ID người dùng")
    private List<String> userIds;
}

