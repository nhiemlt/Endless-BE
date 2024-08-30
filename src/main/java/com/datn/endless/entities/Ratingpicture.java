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
@Table(name = "ratingpictures")
public class Ratingpicture {
    private String pictureID;

    private Rating ratingID;

    private String picture;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "PictureID", nullable = false, length = 36)
    public String getPictureID() {
        return pictureID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RatingID", nullable = false)
    public Rating getRatingID() {
        return ratingID;
    }

    @Lob
    @Column(name = "Picture")
    public String getPicture() {
        return picture;
    }

}