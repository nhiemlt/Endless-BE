package com.datn.endless.dtos;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class AttributeValueDTO {
    private String attributeId;
    private String attributeName;
    private String attributeValue;
}
