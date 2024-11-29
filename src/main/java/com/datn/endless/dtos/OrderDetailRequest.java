package com.datn.endless.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequest {
	  private int detailId;  // Mã chi tiết sản phẩm
	   private BigDecimal productPrice;
	    private int quantity;
}
