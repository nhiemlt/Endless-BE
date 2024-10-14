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
@Table(name = "entrydetails")
public class Entrydetail {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "EntryDetailID", nullable = false, length = 36)
    private String entryDetailID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EntryID", nullable = false)
    private Entry entryID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProductVersionID", nullable = false)
    private Productversion productVersionID;

    @NotNull
    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "Price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

}