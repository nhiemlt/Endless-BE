package com.datn.endless.controllers;

import com.datn.endless.dtos.OrderDTO;
import com.datn.endless.dtos.OrderDetailDTO;
import com.datn.endless.dtos.OrderStatusDTO;
import com.datn.endless.exceptions.*;
import com.datn.endless.models.ErrorResponse;
import com.datn.endless.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Create Order
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            // Gọi service để tạo đơn hàng
            OrderDTO savedOrderDTO = orderService.createOrder(orderDTO);
            // Trả về đơn hàng đã lưu với mã trạng thái CREATED (201)
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrderDTO);
        } catch (UserNotFoundException e) {
            // Xử lý lỗi khi người dùng không tồn tại
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found: " + e.getMessage()) {
            });
        } catch (VoucherNotFoundException e) {
            // Xử lý lỗi khi voucher không tồn tại
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Voucher not found: " + e.getMessage()));
        } catch (AddressNotFoundException e) {
            // Xử lý lỗi khi địa chỉ không tồn tại
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Address not found: " + e.getMessage()));
        } catch (ProductVersionNotFoundException e) {
            // Xử lý lỗi khi phiên bản sản phẩm không tồn tại
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Product version not found: " + e.getMessage()));
        } catch (StatusTypeNotFoundException e) {
            // Xử lý lỗi khi loại trạng thái đơn hàng không tồn tại
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Order status type not found: " + e.getMessage()));
        } catch (Exception e) {
            // Xử lý các lỗi không dự đoán được khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    // Get Order by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String id) {
        try {
            OrderDTO orderDTO = orderService.getOrderDTOById(id);
            return ResponseEntity.ok(orderDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Customize error handling as needed
        }
    }

    // Get All Orders
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        try {
            List<OrderDTO> orders = orderService.getAllOrderDTOs();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Customize error handling as needed
        }
    }

    // Get Order Details by Order ID
    @GetMapping("/{id}/details")
    public ResponseEntity<List<OrderDetailDTO>> getOrderDetailsByOrderId(@PathVariable String id) {
        try {
            List<OrderDetailDTO> orderDetails = orderService.getOrderDetailsDTOByOrderId(id);
            return ResponseEntity.ok(orderDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Customize error handling as needed
        }
    }

    // Get Order Status by Order ID
    @GetMapping("/{id}/status")
    public ResponseEntity<List<OrderStatusDTO>> getOrderStatusByOrderId(@PathVariable String id) {
        try {
            List<OrderStatusDTO> orderStatuses = orderService.getOrderStatusDTOByOrderId(id);
            return ResponseEntity.ok(orderStatuses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Customize error handling as needed
        }
    }

    // Cancel Order
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable String id) {
        try {
            // Giả định orderService.cancelOrder sẽ trả về trạng thái đơn hàng sau khi hủy thành công
            OrderStatusDTO cancelledStatus = orderService.cancelOrder(id);
            return ResponseEntity.ok(cancelledStatus);
        } catch (OrderNotFoundException e) {
            // Xử lý trường hợp không tìm thấy đơn hàng
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found with ID: " + id);
        } catch (OrderCannotBeCancelledException e) {
            // Xử lý trường hợp đơn hàng đã bị hủy
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Order can not be Cancel");
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}