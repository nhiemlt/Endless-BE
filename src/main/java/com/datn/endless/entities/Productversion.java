package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "productversions")
public class Productversion {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "ProductVersionID", nullable = false, length = 36)
    private String productVersionID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProductID", nullable = false)
    private Product productID;

    @Size(max = 255)
    @NotNull
    @Column(name = "VersionName", nullable = false)
    private String versionName;

    @NotNull
    @Column(name = "PurchasePrice", nullable = false, precision = 18, scale = 2)
    private BigDecimal purchasePrice;

    @NotNull
    @Column(name = "Price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Size(max = 50)
    @NotNull
    @Column(name = "Status", nullable = false, length = 50)
    private String status;

    @Lob
    @Column(name = "Image")
    private String image;

    @OneToMany(mappedBy = "productVersionID")
    private Set<Versionattribute> versionattributes = new LinkedHashSet<>();

}