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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProvinceCode")
    private Province provinceCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DistrictCode")
    private District districtCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WardCode")
    private Ward wardCode;

    @NotNull
    @Lob
    @Column(name = "HouseNumberStreet", nullable = false)
    private String houseNumberStreet;

}