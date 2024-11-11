    package com.datn.endless.dtos;

    import lombok.Getter;
    import lombok.Setter;

    import java.util.List;

    @Getter
    @Setter
    public class PromotionproductDTO {
        private String promotionProductID;
        private String promotionDetailID;
        private int  PercentDiscount;
//        private String productVersionID;
private List<ProductVersionDTO1> productVersionIDs;
    }
