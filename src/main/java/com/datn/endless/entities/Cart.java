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
@Table(name = "carts")
public class Cart {
    private String cartID;

    private User userID;

    private Productversion productVersionID;

    private Integer quantity;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "CartID", nullable = false, length = 36)
    public String getCartID() {
        return cartID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    public User getUserID() {
        return userID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProductVersionID", nullable = false)
    public Productversion getProductVersionID() {
        return productVersionID;
    }

    @NotNull
    @Column(name = "Quantity", nullable = false)
    public Integer getQuantity() {
        return quantity;
    }

}