package com.datn.endless.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orderstatustype")
public class Orderstatustype {
    @Id
    @Column(name = "StatusID", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    private String name;
}