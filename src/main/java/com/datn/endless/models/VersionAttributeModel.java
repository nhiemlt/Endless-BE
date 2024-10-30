package com.datn.endless.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class VersionAttributeModel {

    @NotNull(message = "ProductVersionID không được để trống.")
    private String productVersionID;

    @NotNull(message = "AttributeValueID không được để trống.")
    private List<String> attributeValueIDs; // Thay đổi đây để cho phép danh sách ID
}
