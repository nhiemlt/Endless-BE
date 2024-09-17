package com.datn.endless.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderstatusId implements Serializable {
    @Column(name = "OrderID")
    private String orderID;

    @Column(name = "StatusID")
    private Integer statusID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderstatusId that = (OrderstatusId) o;
        return Objects.equals(orderID, that.orderID) && Objects.equals(statusID, that.statusID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderID, statusID);
    }
}