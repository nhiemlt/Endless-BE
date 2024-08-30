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
@Table(name = "favorite")
public class Favorite {
    private String favoriteID;

    private User userID;

    private Product productID;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "FavoriteID", nullable = false, length = 36)
    public String getFavoriteID() {
        return favoriteID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    public User getUserID() {
        return userID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProductID", nullable = false)
    public Product getProductID() {
        return productID;
    }

}