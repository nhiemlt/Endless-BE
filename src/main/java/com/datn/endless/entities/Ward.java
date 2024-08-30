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
    private String code;

    private String name;

    private String nameEn;

    private String fullName;

    private String fullNameEn;

    private String codeName;

    private District districtCode;

    private Integer administrativeUnitId;

    @Id
    @Size(max = 10)
    @Column(name = "Code", nullable = false, length = 10)
    public String getCode() {
        return code;
    }

    @Size(max = 512)
    @Column(name = "Name", length = 512)
    public String getName() {
        return name;
    }

    @Size(max = 512)
    @Column(name = "Name_EN", length = 512)
    public String getNameEn() {
        return nameEn;
    }

    @Size(max = 512)
    @Column(name = "Full_Name", length = 512)
    public String getFullName() {
        return fullName;
    }

    @Size(max = 512)
    @Column(name = "Full_Name_EN", length = 512)
    public String getFullNameEn() {
        return fullNameEn;
    }

    @Size(max = 512)
    @Column(name = "Code_Name", length = 512)
    public String getCodeName() {
        return codeName;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "District_Code")
    public District getDistrictCode() {
        return districtCode;
    }

    @Column(name = "Administrative_Unit_ID")
    public Integer getAdministrativeUnitId() {
        return administrativeUnitId;
    }

}