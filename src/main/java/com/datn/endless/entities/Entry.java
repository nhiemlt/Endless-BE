package com.datn.endless.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "entries")
public class Entry {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "EntryID", nullable = false, length = 36)
    private String entryID;

    @NotNull
    @Column(name = "OrderDate", nullable = false)
    private LocalDate orderDate;

    @NotNull
    @Column(name = "TotalMoney", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalMoney;

}