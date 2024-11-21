package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO{
    String promotionID;
    String name;
    Instant startDate;
    Instant endDate;
    Integer percentDiscount;
    String poster;
    Boolean active;
    Instant createDate;
    Set<PromotionproductDTO> promotionproducts;
}