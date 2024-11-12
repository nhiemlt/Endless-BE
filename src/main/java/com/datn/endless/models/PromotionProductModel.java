package com.datn.endless.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PromotionProductModel {
    private String promotionProductID;
    private String promotionDetailID;
//    private String productVersionID;
    private List<String> productVersionIDs;
}
