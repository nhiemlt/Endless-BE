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
    private String addressID;

    private User userID;

    private Province provinceCode;

    private District districtCode;

    private Ward wardCode;

    private String houseNumberStreet;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "AddressID", nullable = false, length = 36)
    public String getAddressID() {
        return addressID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    public User getUserID() {
        return userID;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProvinceCode")
    public Province getProvinceCode() {
        return provinceCode;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DistrictCode")
    public District getDistrictCode() {
        return districtCode;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WardCode")
    public Ward getWardCode() {
        return wardCode;
    }

    @NotNull
    @Lob
    @Column(name = "HouseNumberStreet", nullable = false)
    public String getHouseNumberStreet() {
        return houseNumberStreet;
    }

}