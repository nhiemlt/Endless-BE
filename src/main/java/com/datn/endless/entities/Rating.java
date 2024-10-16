package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "RatingID", nullable = false, length = 36)
    private String ratingID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    private User userID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OrderDetailID", nullable = false)
    private Orderdetail orderDetailID;

    @Column(name = "RatingValue")
    private Integer ratingValue;

    @Lob
    @Column(name = "Comment")
    private String comment;

    @NotNull
    @Column(name = "RatingDate", nullable = false)
    private Instant ratingDate;

    @OneToMany(mappedBy = "ratingID", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Ratingpicture> ratingpictures = new LinkedHashSet<>();

}