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
@Table(name = "brands")
public class Brand {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "BrandID", nullable = false, length = 36)
    private String brandID;

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    private String name;

    @Lob
    @Column(name = "Logo")
    private String logo;

}