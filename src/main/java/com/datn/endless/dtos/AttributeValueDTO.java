package com.datn.endless.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class AttributeValueDTO {
    @JsonIgnore
    private String attributeId;
    @JsonIgnore
    private String attributeName;
    private String attributeValueID;
    private String attributeValue;
}
