package com.datn.endless.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "modules")
public class Module {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "ModuleID", nullable = false, length = 36)
    private String moduleID;

    @Size(max = 255)
    @NotNull
    @Column(name = "ModuleName", nullable = false)
    private String moduleName;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "EN_ModuleName")
    private String enModulename;

    @Size(max = 255)
    @Column(name = "EN_description")
    private String enDescription;

}