package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "purchaseorders")
public class Entryorder {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "PurchaseOrderID", nullable = false, length = 36)
    private String purchaseOrderID;

    @NotNull
    @Column(name = "PurchaseDate", nullable = false)
    private LocalDate purchaseDate;

    @NotNull
    @Column(name = "TotalMoney", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalMoney;

    @OneToMany(mappedBy = "purchaseOrderID", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entryorderdetail> details;

}