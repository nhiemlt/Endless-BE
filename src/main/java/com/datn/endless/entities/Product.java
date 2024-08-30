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
@Table(name = "products")
public class Product {
    private String productID;

    private Category categoryID;

    private Brand brandID;

    private String name;

    private String nameEn;

    private String description;

    private String enDescription;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "ProductID", nullable = false, length = 36)
    public String getProductID() {
        return productID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CategoryID", nullable = false)
    public Category getCategoryID() {
        return categoryID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BrandID", nullable = false)
    public Brand getBrandID() {
        return brandID;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    public String getName() {
        return name;
    }

    @Size(max = 255)
    @Column(name = "Name_EN")
    public String getNameEn() {
        return nameEn;
    }

    @Lob
    @Column(name = "Description")
    public String getDescription() {
        return description;
    }

    @Lob
    @Column(name = "EN_description")
    public String getEnDescription() {
        return enDescription;
    }

}