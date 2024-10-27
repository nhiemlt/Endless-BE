package com.datn.endless.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AttributeModel {
    @NotNull
    @Size(max = 255)
    private String attributeName;

//    @Size(max = 255)
//    private String enAttributeName;
}
