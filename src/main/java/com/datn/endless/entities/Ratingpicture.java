package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
@Table(name = "ratingpictures")
public class Ratingpicture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "PictureID", nullable = false, length = 36)
    private String pictureID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RatingID", nullable = false)
    private Rating ratingID;

    @Lob
    @Column(name = "Picture")
    private String picture;

}