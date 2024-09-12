package com.datn.endless.controllers;

import com.datn.endless.dtos.OrderDetailDTO;
import com.datn.endless.entities.Order;
import com.datn.endless.entities.Orderdetail;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Productversion;
import com.datn.endless.repositories.OrderRepository;
import com.datn.endless.repositories.OrderdetailRepository;
import com.datn.endless.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderdetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getOrders(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Xác thực người dùng
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        // Lấy tất cả các đơn hàng của người dùng
        List<Order> orders = orderRepository.findByUserID(user);
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No orders found for the user");
        }

        // Lấy tất cả các chi tiết đơn hàng cho các đơn hàng của người dùng
        List<Orderdetail> orderDetails = orderDetailRepository.findByOrderIDIn(orders);

        // Chuyển đổi danh sách OrderDetail thành danh sách OrderDetailDTO
        List<OrderDetailDTO> orderDetailDTOs = orderDetails.stream()
                .map(orderDetail -> {
                    Productversion productVersion = orderDetail.getProductVersionID();
                    String productNameVersionName = productVersion.getProductID().getName() + " " + productVersion.getVersionName();
                    return new OrderDetailDTO(
                            productNameVersionName,
                            orderDetail.getQuantity(),
                            orderDetail.getPrice(),
                            orderDetail.getDiscountPrice(),
                            orderDetail.getOrderID().getOrderStatus(),
                            orderDetail.getOrderID().getOrderDate()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDetailDTOs);
    }
}
