package com.datn.endless.controllers;

import com.datn.endless.dtos.*;
import com.datn.endless.exceptions.*;
import com.datn.endless.models.OrderModel;
import com.datn.endless.models.OrderModelForUser;
import com.datn.endless.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Create Order
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody OrderModel orderModel) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrderDTO savedOrderDTO = orderService.createOrder(orderModel);
            response.put("success", true);
            response.put("data", savedOrderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserNotFoundException e) {
            response.put("success", false);
            response.put("error", "User not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (VoucherNotFoundException e) {
            response.put("success", false);
            response.put("error", "Voucher not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (AddressNotFoundException e) {
            response.put("success", false);
            response.put("error", "Address not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (ProductVersionNotFoundException e) {
            response.put("success", false);
            response.put("error", "Product version not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (StatusTypeNotFoundException e) {
            response.put("success", false);
            response.put("error", "Order status type not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrderForUser(@RequestBody OrderModelForUser orderModel) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrderDTO savedOrderDTO = orderService.createOrderForUser(orderModel);
            response.put("success", true);
            response.put("data", savedOrderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (AddressNotFoundException e) {
            response.put("success", false);
            response.put("error", "Address not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (VoucherNotFoundException e) {
            response.put("success", false);
            response.put("error", "Voucher not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (ProductVersionNotFoundException e) {
            response.put("success", false);
            response.put("error", "Product version not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get Order by ID
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
            response.put("error", "Order not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get All Orders
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(required = false) String userID,
            @RequestParam(required = false) String orderAddress,
            @RequestParam(required = false) String orderPhone,
            @RequestParam(required = false) String orderName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        try {
            // Tạo đối tượng Pageable cho phân trang
            Pageable pageable = PageRequest.of(page, size);

            // Gọi service để lấy dữ liệu đơn hàng với lọc và phân trang
            Page<OrderDTO> orders = orderService.getAllOrderDTOs(userID, orderAddress, orderPhone, orderName, pageable);

            response.put("success", true);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getAllOrderByUserLogin(
            @RequestParam(required = false) String orderAddress,
            @RequestParam(required = false) String orderPhone,
            @RequestParam(required = false) String orderName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        try {
            // Tạo đối tượng Pageable cho phân trang
            Pageable pageable = PageRequest.of(page, size);

            // Gọi service để lấy dữ liệu đơn hàng với lọc và phân trang
            Page<OrderDTO> orders = orderService.getAllOrderDTOsByUserLogin( orderAddress, orderPhone, orderName, pageable);

            response.put("success", true);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Get Order Details by Order ID
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
            response.put("error", "Order not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get Order Status by Order ID
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
            response.put("error", "Order not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Cancel Order
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
            response.put("error", "Order not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (OrderCannotBeCancelledException e) {
            response.put("success", false);
            response.put("error", "Order cannot be cancelled: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
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
            response.put("error", "Order not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (OrderCancellationNotAllowedException e) {
            response.put("success", false);
            response.put("error", "Order cannot be cancelled: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
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
            response.put("error", "Order not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (InvalidOrderStatusException e) {
            response.put("success", false);
            response.put("error", "Order cannot be marked as paid: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
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
            response.put("error", "Order not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (InvalidOrderStatusException e) {
            response.put("success", false);
            response.put("error", "Order cannot be marked as shipping: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
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
            response.put("error", "Order not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (InvalidOrderStatusException e) {
            response.put("success", false);
            response.put("error", "Order cannot be marked as delivered: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
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
            response.put("error", "Order not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (InvalidOrderStatusException e) {
            response.put("success", false);
            response.put("error", "Order cannot be marked as confirmed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
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
            response.put("error", "Order not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (InvalidOrderStatusException e) {
            response.put("success", false);
            response.put("error", "Order cannot be set to pending: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
