package com.datn.endless.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "attributevalues")
public class Attributevalue {
    @Id
    @Size(max = 36)
    @Column(name = "AttributeValueID", nullable = false, length = 36)
    private String attributeValueID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AttributeID", nullable = false)
    private Attribute attribute;

    @Size(max = 255)
    @NotNull
    @Column(name = "Value", nullable = false)
    private String value;



}
