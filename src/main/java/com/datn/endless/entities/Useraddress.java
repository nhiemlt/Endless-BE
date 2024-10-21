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
    @Column(name = "ProvinceName", nullable = false, length = 100)
    private String provinceName;

    @Size(max = 100)
    @NotNull
    @Column(name = "DistrictName", nullable = false, length = 100)
    private String districtName;

    @Size(max = 100)
    @NotNull
    @Column(name = "WardStreet", nullable = false, length = 100)
    private String wardStreet;

    @NotNull
    @Lob
    @Column(name = "DetailAddress", nullable = false)
    private String detailAddress;

    @Size(max = 255)
    @Column(name = "AddressLevel4")
    private String addressLevel4;

}