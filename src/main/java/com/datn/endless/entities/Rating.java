package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "ratings")
public class Rating {
    private String ratingID;

    private User userID;

    private Orderdetail orderDetailID;

    private Integer ratingValue;

    private String comment;

    private Instant ratingDate;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "RatingID", nullable = false, length = 36)
    public String getRatingID() {
        return ratingID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    public User getUserID() {
        return userID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OrderDetailID", nullable = false)
    public Orderdetail getOrderDetailID() {
        return orderDetailID;
    }

    @Column(name = "RatingValue")
    public Integer getRatingValue() {
        return ratingValue;
    }

    @Lob
    @Column(name = "Comment")
    public String getComment() {
        return comment;
    }

    @NotNull
    @Column(name = "RatingDate", nullable = false)
    public Instant getRatingDate() {
        return ratingDate;
    }

}