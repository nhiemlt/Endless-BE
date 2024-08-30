package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "attributevalues")
public class Attributevalue {
    private String attributeValueID;

    private Attribute attributeID;

    private String value;

    private String enValue;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "AttributeValueID", nullable = false, length = 36)
    public String getAttributeValueID() {
        return attributeValueID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AttributeID", nullable = false)
    public Attribute getAttributeID() {
        return attributeID;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "Value", nullable = false)
    public String getValue() {
        return value;
    }

    @Size(max = 255)
    @Column(name = "EN_value")
    public String getEnValue() {
        return enValue;
    }

}