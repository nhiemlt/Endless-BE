package com.datn.endless.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "attributes")
public class Attribute {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "AttributeID", nullable = false, length = 36)

    private String attributeID;

    @Size(max = 255)
    @NotNull
    @Column(name = "AttributeName", nullable = false)
    private String attributeName;

    @Size(max = 255)
    @Column(name = "EN_atributeName")
    private String enAtributename;

}