package com.datn.endless.controllers;

import com.datn.endless.dtos.ErrorResponse;
import com.datn.endless.dtos.PurchaseOrderDTO;
import com.datn.endless.dtos.PurchaseOrderDetailDTO;
import com.datn.endless.models.PurchaseOrderModel;
import com.datn.endless.services.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<Object> createPurchaseOrder(@RequestBody PurchaseOrderModel purchaseOrderModel) {
        try {
            PurchaseOrderDTO createdOrder = purchaseOrderService.createPurchaseOrder(purchaseOrderModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Product version not found", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid input data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPurchaseOrderById(@PathVariable("id") String id) {
        PurchaseOrderDTO purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
        if (purchaseOrder != null) {
            return ResponseEntity.ok(purchaseOrder);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Purchase order not found", "Purchase order not found with id: " + id));
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllPurchaseOrders(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Pageable pageable) {
        try {
            Page<PurchaseOrderDTO> purchaseOrders = purchaseOrderService.getAllPurchaseOrders(startDate, endDate, pageable);
            return ResponseEntity.ok(purchaseOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", e.getMessage()));
        }
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Object> getPurchaseOrderDetails(@PathVariable("id") String id) {
        List<PurchaseOrderDetailDTO> details = purchaseOrderService.getPurchaseOrderDetails(id);
        if (details != null) {
            return ResponseEntity.ok(details);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Purchase order not found", "Purchase order not found with id: " + id));
        }
    }
}
