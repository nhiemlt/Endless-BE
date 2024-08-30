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
@Table(name = "categories")
public class Category {
    private String categoryID;

    private String name;

    private String enName;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "CategoryID", nullable = false, length = 36)
    public String getCategoryID() {
        return categoryID;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    public String getName() {
        return name;
    }

    @Size(max = 255)
    @Column(name = "EN_name")
    public String getEnName() {
        return enName;
    }

}