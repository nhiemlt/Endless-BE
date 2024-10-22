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
@Table(name = "useraddresses")
public class Useraddress {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "AddressID", nullable = false, length = 36)
    private String addressID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    private User userID;

    @Size(max = 100)
    @NotNull
    @Column(name = "ProvinceID", nullable = false, length = 100)
    private String provinceID;

    @Size(max = 100)
    @NotNull
    @Column(name = "DistrictID", nullable = false, length = 100)
    private String districtID;

    @Size(max = 100)
    @NotNull
    @Column(name = "WardCode", nullable = false, length = 100)
    private String wardCode;

    @NotNull
    @Lob
    @Column(name = "DetailAddress", nullable = false)
    private String detailAddress;

}