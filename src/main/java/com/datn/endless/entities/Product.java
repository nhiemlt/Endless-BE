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
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "ProductID", nullable = false, length = 36)
    private String productID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category categoryID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BrandID", nullable = false)
    private Brand brandID;

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "Name_EN")
    private String nameEn;

    @Lob
    @Column(name = "Description")
    private String description;

    @Lob
    @Column(name = "EN_description")
    private String enDescription;

}