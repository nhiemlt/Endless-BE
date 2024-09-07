package com.datn.endless.controllers;

import com.datn.endless.dtos.OrderDTO;
import com.datn.endless.dtos.OrderDetailDTO;
import com.datn.endless.entities.Order;
import com.datn.endless.entities.Orderdetail;
import com.datn.endless.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    // Create or Update Order
    @PostMapping
    public ResponseEntity<OrderDTO> createOrUpdateOrder(@RequestBody OrderDTO orderDTO) {
        Order order = orderService.convertToOrderEntity(orderDTO);
        Order savedOrder = orderService.saveOrder(order);
        return ResponseEntity.ok(orderService.convertToOrderDTO(savedOrder));
    }

    // Get Order by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String id) {
        OrderDTO orderDTO = orderService.getOrderDTOById(id);
        return orderDTO != null ? ResponseEntity.ok(orderDTO) : ResponseEntity.notFound().build();
    }

    // Get All Orders
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrderDTOs());
    }

    // Delete Order
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // Get Order Details by Order ID
    @GetMapping("/{orderId}/details")
    public ResponseEntity<List<OrderDetailDTO>> getOrderDetailsByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderDetailsDTOByOrderId(orderId));
    }
}
