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
    private String moduleID;

    private String moduleName;

    private String description;

    private String enModulename;

    private String enDescription;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "ModuleID", nullable = false, length = 36)
    public String getModuleID() {
        return moduleID;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "ModuleName", nullable = false)
    public String getModuleName() {
        return moduleName;
    }

    @Size(max = 255)
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Size(max = 255)
    @Column(name = "EN_ModuleName")
    public String getEnModulename() {
        return enModulename;
    }

    @Size(max = 255)
    @Column(name = "EN_description")
    public String getEnDescription() {
        return enDescription;
    }

}