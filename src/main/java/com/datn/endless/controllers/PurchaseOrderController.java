package com.datn.endless.controllers;

import com.datn.endless.dtos.PurchaseOrderDTO;
import com.datn.endless.entities.Purchaseorder;
import com.datn.endless.services.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<Object> createPurchaseOrder(@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        try {
            Purchaseorder purchaseOrder = purchaseOrderService.createPurchaseOrder(purchaseOrderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrder);
        } catch (NoSuchElementException e) {
            // Trả về lỗi khi không tìm thấy đối tượng cần thiết
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Trả về lỗi khi có vấn đề với đầu vào
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            // Trả về lỗi chung khi có lỗi không xác định
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPurchaseOrderById(@PathVariable("id") String id) {
        try {
            Purchaseorder purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
            if (purchaseOrder != null) {
                return ResponseEntity.ok(purchaseOrder);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Purchase order not found with id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Purchaseorder>> getAllPurchaseOrders() {
        try {
            List<Purchaseorder> purchaseOrders = purchaseOrderService.getAllPurchaseOrders();
            return ResponseEntity.ok(purchaseOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}