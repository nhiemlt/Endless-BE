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
@Table(name = "versionattributes")
public class Versionattribute {
    private String versionAttributeID;

    private Productversion productVersionID;

    private Attributevalue attributeValueID;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "VersionAttributeID", nullable = false, length = 36)
    public String getVersionAttributeID() {
        return versionAttributeID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProductVersionID", nullable = false)
    public Productversion getProductVersionID() {
        return productVersionID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AttributeValueID", nullable = false)
    public Attributevalue getAttributeValueID() {
        return attributeValueID;
    }

}