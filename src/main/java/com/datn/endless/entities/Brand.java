package com.datn.endless.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "brands")
public class Brand {
    @Id
    @Size(max = 36)
    @Column(name = "BrandID", nullable = false, length = 36)
    private String brandID = java.util.UUID.randomUUID().toString();

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    private String name;

    @Lob
    @Column(name = "Logo")
    private String logo;
}
