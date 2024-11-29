package com.datn.endless.dtos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
	  private int userId;  // Mã người dùng
	    private Integer addressId; // Thêm addressId để nhận từ phản hồi

	    private double totalAmount;  // Tổng tiền
	    private int paymentMethodId; // Mã phương thức thanh toán
	    private int paymentStatusId; // Mã trạng thái thanh toán
	    private int orderStatusId;   // Mã trạng thái đơn hàng
	    private Date orderDate;  // Ngày đặt hàng
	    private String note;  // Ghi chú
	    private String buyerName;  // Tên người mua
	    private String phoneNumber;  // Số điện thoại
	    private List<OrderDetailRequest> orderDetails;  // Chi tiết đơn hàng
}
