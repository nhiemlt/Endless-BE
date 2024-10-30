package com.datn.endless.dtos;

import lombok.Data;

import java.util.List;

@Data
public class VersionAttributeDTO2 {
    private String versionAttributeID;
    private String productVersionID;
    private List<String> attributeValueIDs; // Cập nhật để chứa danh sách ID


}
