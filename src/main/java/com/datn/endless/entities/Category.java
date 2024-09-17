package com.datn.endless.entities;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "categories")
public class Category {
    @Id
    @Column(name = "CategoryID", nullable = false, length = 36)
    private String categoryID = UUID.randomUUID().toString(); // Sử dụng UUID

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    @Size(max = 255)
    @Column(name = "EN_name")
    private String enName;

}

