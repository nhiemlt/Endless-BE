package com.datn.endless.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;


@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "products")
public class Product {
    @Id
    @Size(max = 36)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @ColumnDefault("(uuid())")
    @Column(name = "ProductID", nullable = false, length = 36)
    private String productID;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category categoryID;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "BrandID", nullable = false)
    private Brand brandID;

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    private String name;

    @Lob
    @Column(name = "Description")
    private String description;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CreateDate")
    private Instant createDate;

}
