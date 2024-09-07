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
@Table(name = "attributevalues")
public class Attributevalue {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "AttributeValueID", nullable = false, length = 36)
    private String attributeValueID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AttributeID", nullable = false)
    private Attribute attributeID;

    @Size(max = 255)
    @NotNull
    @Column(name = "Value", nullable = false)
    private String value;

    @Size(max = 255)
    @Column(name = "EN_value")
    private String enValue;

}