package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wards")
public class Ward {
    @Id
    @Size(max = 10)
    @Column(name = "Code", nullable = false, length = 10)
    private String code;

    @Size(max = 512)
    @Column(name = "Name", length = 512)
    private String name;

    @Size(max = 512)
    @Column(name = "Name_EN", length = 512)
    private String nameEn;

    @Size(max = 512)
    @Column(name = "Full_Name", length = 512)
    private String fullName;

    @Size(max = 512)
    @Column(name = "Full_Name_EN", length = 512)
    private String fullNameEn;

    @Size(max = 512)
    @Column(name = "Code_Name", length = 512)
    private String codeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "District_Code")
    private District districtCode;

    @Column(name = "Administrative_Unit_ID")
    private Integer administrativeUnitId;

}