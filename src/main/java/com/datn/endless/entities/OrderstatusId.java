package com.datn.endless.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class OrderstatusId implements Serializable {
    @Column(name = "OrderID")
    private String orderID;

    @Column(name = "StatusID")
    private Integer statusID;

    public OrderstatusId() {}

    public OrderstatusId(String orderID, Integer statusID) {
        this.orderID = orderID;
        this.statusID = statusID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setStatusID(Integer statusID) {
        this.statusID = statusID;
    }

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