package com.datn.endless.controllers;

import com.datn.endless.dtos.*;
import com.datn.endless.exceptions.*;
import com.datn.endless.models.OrderModel;
import com.datn.endless.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody OrderModel orderModel) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrderDTO savedOrderDTO = orderService.createOrder(orderModel);
            response.put("success", true);
            response.put("data", savedOrderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (AddressNotFoundException e) {
            response.put("success", false);
            response.put("error", "Địa chỉ không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (VoucherNotFoundException e) {
            response.put("success", false);
            response.put("error", "Mã giảm giá không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (ProductVersionNotFoundException e) {
            response.put("success", false);
            response.put("error", "Phiên bản sản phẩm không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/create-order-online")
    public ResponseEntity<Map<String, Object>> createOrderVNPay(@RequestBody OrderModel orderModel) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrderDTO savedOrderDTO = orderService.createOrderOnline(orderModel);
            response.put("success", true);
            response.put("data", savedOrderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (AddressNotFoundException e) {
            response.put("success", false);
            response.put("error", "Địa chỉ không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (VoucherNotFoundException e) {
            response.put("success", false);
            response.put("error", "Mã giảm giá không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (ProductVersionNotFoundException e) {
            response.put("success", false);
            response.put("error", "Phiên bản sản phẩm không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Lấy đơn hàng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrderDTO orderDTO = orderService.getOrderDTOById(id);
            response.put("success", true);
            response.put("data", orderDTO);
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            response.put("success", false);
            response.put("error", "Đơn hàng không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Lấy tất cả đơn hàng
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderDTO> orders = orderService.getAllOrderDTOs(keywords, startDate, endDate, pageable);

            response.put("success", true);
            response.put("data", orders.getContent());
            response.put("currentPage", orders.getNumber());
            response.put("totalItems", orders.getTotalElements());
            response.put("totalPages", orders.getTotalPages());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Lấy tất cả đơn hàng của người dùng đăng nhập
    @GetMapping("/user")
    public ResponseEntity<?> getAllUserOrders(@RequestParam(name = "keyword", required = false) String keyword) {
        try {
            List<OrderDTO> orders = orderService.getAllUserLogin(keyword);
            if (orders.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Không tìm thấy đơn hàng của người dùng", "data", orders), HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(Map.of("message", "Lấy đơn hàng thành công", "data", orders), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Đã xảy ra lỗi khi lấy đơn hàng", "data", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Lấy chi tiết đơn hàng theo ID
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getOrderDetailsByOrderId(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<OrderDetailDTO> orderDetails = orderService.getOrderDetailsDTOByOrderId(id);
            response.put("success", true);
            response.put("data", orderDetails);
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            response.put("success", false);
            response.put("error", "Đơn hàng không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getOrderStatusByOrderId(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<OrderStatusDTO> orderStatuses = orderService.getOrderStatusDTOByOrderId(id);
            response.put("success", true);
            response.put("data", orderStatuses);
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            response.put("success", false);
            response.put("error", "Không tìm thấy đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Hủy đơn hàng
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrderStatusDTO cancelledStatus = orderService.cancelOrder(id);
            response.put("success", true);
            response.put("data", cancelledStatus);
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            response.put("success", false);
            response.put("error", "Không tìm thấy đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (OrderCannotBeCancelledException e) {
            response.put("success", false);
            response.put("error", "Không thể hủy đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        String orderId = requestBody.get("orderId");

        try {
            OrderStatusDTO cancelledStatus = orderService.cancelOrder(orderId);
            response.put("success", true);
            response.put("data", cancelledStatus);
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            response.put("success", false);
            response.put("error", "Đơn hàng không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (OrderCancellationNotAllowedException e) {
            response.put("success", false);
            response.put("error", "Không thể hủy đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/mark-as-paid")
    public ResponseEntity<Map<String, Object>> markOrderAsPaid(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        String orderId = requestBody.get("orderId");

        try {
            OrderStatusDTO paidStatus = orderService.markOrderAsPaid(orderId);
            response.put("success", true);
            response.put("data", paidStatus);
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            response.put("success", false);
            response.put("error", "Đơn hàng không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (InvalidOrderStatusException e) {
            response.put("success", false);
            response.put("error", "Không thể đánh dấu đơn hàng là đã thanh toán: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/mark-as-shipping")
    public ResponseEntity<Map<String, Object>> markOrderAsShipping(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        String orderId = requestBody.get("orderId");

        try {
            OrderStatusDTO shippingStatus = orderService.markOrderAsShipping(orderId);
            response.put("success", true);
            response.put("data", shippingStatus);
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            response.put("success", false);
            response.put("error", "Đơn hàng không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (InvalidOrderStatusException e) {
            response.put("success", false);
            response.put("error", "Không thể đánh dấu đơn hàng là đang vận chuyển: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/mark-as-delivered")
    public ResponseEntity<Map<String, Object>> markOrderAsDelivered(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        String orderId = requestBody.get("orderId");

        try {
            OrderStatusDTO deliveredStatus = orderService.markOrderAsDelivered(orderId);
            response.put("success", true);
            response.put("data", deliveredStatus);
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            response.put("success", false);
            response.put("error", "Đơn hàng không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (InvalidOrderStatusException e) {
            response.put("success", false);
            response.put("error", "Không thể đánh dấu đơn hàng là đã giao: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/mark-as-confirmed")
    public ResponseEntity<Map<String, Object>> markOrderAsConfirmed(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        String orderId = requestBody.get("orderId");

        try {
            OrderStatusDTO confirmedStatus = orderService.markOrderAsConfirmed(orderId);
            response.put("success", true);
            response.put("data", confirmedStatus);
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            response.put("success", false);
            response.put("error", "Đơn hàng không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (InvalidOrderStatusException e) {
            response.put("success", false);
            response.put("error", "Không thể xác nhận đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/mark-as-pending")
    public ResponseEntity<Map<String, Object>> markOrderAsPending(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new HashMap<>();
        String orderId = requestBody.get("orderId");

        try {
            OrderStatusDTO pendingStatus = orderService.markOrderAsPending(orderId);
            response.put("success", true);
            response.put("data", pendingStatus);
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException e) {
            response.put("success", false);
            response.put("error", "Đơn hàng không tồn tại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (InvalidOrderStatusException e) {
            response.put("success", false);
            response.put("error", "Không thể đánh dấu đơn hàng là đang chờ xử lý: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
