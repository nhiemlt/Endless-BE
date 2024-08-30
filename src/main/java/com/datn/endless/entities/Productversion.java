package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "productversions")
public class Productversion {
    private String productVersionID;

    private Product productID;

    private String versionName;

    private BigDecimal purchasePrice;

    private BigDecimal price;

    private String status;

    private String image;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "ProductVersionID", nullable = false, length = 36)
    public String getProductVersionID() {
        return productVersionID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProductID", nullable = false)
    public Product getProductID() {
        return productID;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "VersionName", nullable = false)
    public String getVersionName() {
        return versionName;
    }

    @NotNull
    @Column(name = "PurchasePrice", nullable = false, precision = 18, scale = 2)
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    @NotNull
    @Column(name = "Price", nullable = false, precision = 18, scale = 2)
    public BigDecimal getPrice() {
        return price;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "Status", nullable = false, length = 50)
    public String getStatus() {
        return status;
    }

    @Lob
    @Column(name = "Image")
    public String getImage() {
        return image;
    }

}