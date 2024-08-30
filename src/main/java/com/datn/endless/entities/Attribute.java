package com.datn.endless.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "attributes")
public class Attribute {
    private String attributeID;

    private String attributeName;

    private String enAtributename;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "AttributeID", nullable = false, length = 36)
    public String getAttributeID() {
        return attributeID;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "AttributeName", nullable = false)
    public String getAttributeName() {
        return attributeName;
    }

    @Size(max = 255)
    @Column(name = "EN_atributeName")
    public String getEnAtributename() {
        return enAtributename;
    }

}