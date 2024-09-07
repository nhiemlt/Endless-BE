package com.datn.endless.services;

import com.datn.endless.dtos.OrderDTO;
import com.datn.endless.dtos.OrderDetailDTO;
import com.datn.endless.entities.Order;
import com.datn.endless.entities.Orderdetail;
import com.datn.endless.repositories.OrderRepository;
import com.datn.endless.repositories.OrderdetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderdetailRepository orderDetailRepository;

    // Create or Update Order
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    // Get Order by ID
    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    // Get All Orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Delete Order
    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }

    // Get Order Details by Order ID
    public List<Orderdetail> getOrderDetailsByOrderId(String orderId) {
        return orderDetailRepository.findAll().stream()
                .filter(od -> od.getOrderID().getOrderID().equals(orderId))
                .toList();
    }

    public OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderID(order.getOrderID());
        dto.setUserID(order.getUserID().getUserID());
        dto.setVoucherID(order.getVoucherID() != null ? order.getVoucherID().getVoucherID() : null);
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalMoney(order.getTotalMoney());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setOrderAddress(order.getOrderAddress());
        dto.setOrderPhone(order.getOrderPhone());
        dto.setOrderName(order.getOrderName());
        return dto;
    }

    private OrderDetailDTO convertToOrderDetailDTO(Orderdetail orderDetail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderDetailID(orderDetail.getOrderDetailID());
        dto.setOrderID(orderDetail.getOrderID().getOrderID());
        dto.setProductVersionID(orderDetail.getProductVersionID().getProductVersionID());
        dto.setQuantity(orderDetail.getQuantity());
        dto.setPrice(orderDetail.getPrice());
        dto.setDiscountPrice(orderDetail.getDiscountPrice());
        return dto;
    }

    // Convert DTO to Entity
    public Order convertToOrderEntity(OrderDTO dto) {
        Order order = new Order();
        order.setOrderID(dto.getOrderID());
        // Set other fields as needed...
        return order;
    }

    // Add methods to get DTOs
    public List<OrderDTO> getAllOrderDTOs() {
        return orderRepository.findAll().stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderDTOById(String id) {
        return orderRepository.findById(id)
                .map(this::convertToOrderDTO)
                .orElse(null);
    }

    public List<OrderDetailDTO> getOrderDetailsDTOByOrderId(String orderId) {
        return orderDetailRepository.findAll().stream()
                .filter(od -> od.getOrderID().getOrderID().equals(orderId))
                .map(this::convertToOrderDetailDTO)
                .collect(Collectors.toList());
    }
}
