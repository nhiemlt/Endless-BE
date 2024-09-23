package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class FavoriteDTO {
    private String favoriteID;
    private String productID;
    private String image;
    private String productName;
    private BigDecimal price;

}
