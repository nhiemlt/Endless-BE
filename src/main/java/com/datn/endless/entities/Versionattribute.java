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
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "VersionAttributeID", nullable = false, length = 36)
    private String versionAttributeID;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "ProductVersionID", nullable = false)
    private Productversion productVersionID;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "AttributeValueID", nullable = false)
    private Attributevalue attributeValueID;

}