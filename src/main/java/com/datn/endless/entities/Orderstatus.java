package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Orderstatus")
public class Orderstatus {

    @EmbeddedId
    private OrderstatusId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("orderID")
    @JoinColumn(name = "OrderID", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("statusID")
    @JoinColumn(name = "StatusID", nullable = false)
    private Orderstatustype statusType;

    @NotNull
    @Column(name = "Time", nullable = false)
    private Instant time;
}