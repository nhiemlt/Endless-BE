package com.datn.endless.controllers;

import com.datn.endless.dtos.ErrorResponse;
import com.datn.endless.dtos.EntryDTO;
import com.datn.endless.dtos.EntryDetailDTO;
import com.datn.endless.models.EntryModel;
import com.datn.endless.services.EntryService;
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
@RequestMapping("/entry-orders")
public class EntryController {

    @Autowired
    private EntryService entryService;

    @PostMapping
    public ResponseEntity<Object> createEntry(@RequestBody EntryModel purchaseOrderModel) {
        try {
            EntryDTO createdOrder = entryService.createEntry(purchaseOrderModel);
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
    public ResponseEntity<Object> getEntryById(@PathVariable("id") String id) {
        EntryDTO purchaseOrder = entryService.getEntryById(id);
        if (purchaseOrder != null) {
            return ResponseEntity.ok(purchaseOrder);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Purchase order not found", "Purchase order not found with id: " + id));
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllEntries(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Pageable pageable) {
        try {
            Page<EntryDTO> purchaseOrders = entryService.getAllEntries(startDate, endDate, pageable);
            return ResponseEntity.ok(purchaseOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", e.getMessage()));
        }
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Object> getEntryDetails(@PathVariable("id") String id) {
        List<EntryDetailDTO> details = entryService.getEntryDetails(id);
        if (details != null) {
            return ResponseEntity.ok(details);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Purchase order not found", "Purchase order not found with id: " + id));
        }
    }
}
