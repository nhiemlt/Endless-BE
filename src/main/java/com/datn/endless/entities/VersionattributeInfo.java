package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
@Table(name = "versionattributes")
public class VersionattributeInfo {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "VersionAttributeID", nullable = false, length = 36)
    private String versionAttributeID;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "ProductVersionID", nullable = false)
    private ProductversionInfo productVersionID;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "AttributeValueID", nullable = false)
    private Attributevalue attributeValueID;
}
