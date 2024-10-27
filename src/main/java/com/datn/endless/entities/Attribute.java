package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "attributes")
public class Attribute {
    @Id
    @Size(max = 36)
    @Column(name = "AttributeID", nullable = false, length = 36)
    private String attributeID;

    @NotNull
    @Size(max = 255)
    @Column(name = "AttributeName", nullable = false)
    private String attributeName;

//    @Size(max = 255)
//    @Column(name = "EN_atributeName") // Chỉnh sửa tên cột
//    private String enAtributename;

}
