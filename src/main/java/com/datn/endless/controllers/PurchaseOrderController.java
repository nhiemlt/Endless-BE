package com.datn.endless.controllers;

import com.datn.endless.dtos.PurchaseOrderDTO;
import com.datn.endless.dtos.PurchaseOrderDetailDTO;
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
            PurchaseOrderDTO createdOrder = purchaseOrderService.createPurchaseOrder(purchaseOrderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPurchaseOrderById(@PathVariable("id") String id) {
        try {
            PurchaseOrderDTO purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
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
    public ResponseEntity<List<PurchaseOrderDTO>> getAllPurchaseOrders() {
        try {
            List<PurchaseOrderDTO> purchaseOrders = purchaseOrderService.getAllPurchaseOrders();
            return ResponseEntity.ok(purchaseOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchPurchaseOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            List<PurchaseOrderDTO> purchaseOrders = purchaseOrderService.searchPurchaseOrders(status, startDate, endDate);
            return ResponseEntity.ok(purchaseOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Object> getPurchaseOrderDetails(@PathVariable("id") String id) {
        try {
            List<PurchaseOrderDetailDTO> details = purchaseOrderService.getPurchaseOrderDetails(id);
            if (details != null) {
                return ResponseEntity.ok(details);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Purchase order not found with id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
