package com.datn.endless.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "provinces")
public class Province {
    private String code;

    private String name;

    private String nameEn;

    private String fullName;

    private String fullNameEn;

    private String codeName;

    private Integer administrativeUnitId;

    private Integer administrativeRegionId;

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

    @Column(name = "Administrative_Unit_ID")
    public Integer getAdministrativeUnitId() {
        return administrativeUnitId;
    }

    @Column(name = "Administrative_Region_ID")
    public Integer getAdministrativeRegionId() {
        return administrativeRegionId;
    }

}