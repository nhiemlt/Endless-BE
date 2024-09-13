package com.datn.endless.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "RatingID", nullable = false, length = 36)
    private String ratingID;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    private User userID;

    @JsonIgnore
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

}