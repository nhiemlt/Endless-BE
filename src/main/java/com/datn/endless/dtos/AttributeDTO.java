package com.datn.endless.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AttributeDTO {
    private String attributeID;
    private String attributeName;

    // Khởi tạo danh sách rỗng
    private List<AttributeValueDTO> attributeValues = new ArrayList<>();

//    private List<AttributeValueDTO> attributeValues;
}