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

    @NotNull
    @Column(name = "ProvinceID", nullable = false)
    private Integer provinceID;

    @Size(max = 50)
    @NotNull
    @Column(name = "ProvinceName", nullable = false, length = 50)
    private String provinceName;

    @NotNull
    @Column(name = "DistrictID", nullable = false)
    private Integer districtID;

    @Size(max = 50)
    @NotNull
    @Column(name = "DistrictName", nullable = false, length = 50)
    private String districtName;

    @Size(max = 20)
    @NotNull
    @Column(name = "WardCode", nullable = false, length = 20)
    private String wardCode;

    @Size(max = 50)
    @NotNull
    @Column(name = "WardName", nullable = false, length = 50)
    private String wardName;

    @NotNull
    @Lob
    @Column(name = "DetailAddress", nullable = false)
    private String detailAddress;

}