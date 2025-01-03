package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    @Column(name = "EntryDate", nullable = false)
    private LocalDateTime entryDate;

    @NotNull
    @Column(name = "TotalMoney", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalMoney;

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entrydetail> details;
}
