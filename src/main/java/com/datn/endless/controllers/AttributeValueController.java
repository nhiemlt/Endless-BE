package com.datn.endless.controllers;

import com.datn.endless.entities.Attributevalue;
import com.datn.endless.repositories.AttributeRepository;
import com.datn.endless.repositories.AttributevalueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/attributevalues")
public class AttributeValueController {

    @Autowired
    private AttributevalueRepository attributevalueRepository;

    @Autowired
    private AttributeRepository attributeRepository;

    // Create a new attribute value
    @PostMapping
    public ResponseEntity<String> createAttributevalue(@RequestBody Attributevalue attributevalue) {
        // Check for empty or null attribute value ID
        if (attributevalue.getValue() == null || attributevalue.getValue().isEmpty()) {
            return ResponseEntity.badRequest().body("Attribute value cannot be empty.");
        }

        // Check if attribute ID exists
        if (!attributeRepository.existsById(attributevalue.getAttributeID().getAttributeID())) {
            return ResponseEntity.badRequest().body("Attribute not found with ID: " + attributevalue.getAttributeID().getAttributeID());
        }

        // Check for duplicate attribute value
        if (attributevalueRepository.existsByValue(attributevalue.getValue())) {
            return ResponseEntity.badRequest().body("Attribute value already exists.");
        }

        // Set default UUID if it's not provided
        if (attributevalue.getAttributeValueID() == null || attributevalue.getAttributeValueID().isEmpty()) {
            attributevalue.setAttributeValueID(UUID.randomUUID().toString());
        }

        try {
            attributevalueRepository.save(attributevalue);
            return ResponseEntity.ok("Attribute value created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating attribute value: " + e.getMessage());
        }
    }

    // Get all attribute values
    @GetMapping
    public ResponseEntity<List<Attributevalue>> getAllAttributevalues() {
        try {
            List<Attributevalue> attributevalues = attributevalueRepository.findAll();
            return ResponseEntity.ok(attributevalues);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get an attribute value by ID
    // Get an attribute value by ID
    @GetMapping("/{id}")
    public ResponseEntity<Attributevalue> getAttributevalueById(@PathVariable String id) {
        Optional<Attributevalue> attributevalue = attributevalueRepository.findById(id);
        if (attributevalue.isEmpty()) {
            return ResponseEntity.status(404).body(null);  // Trả về lỗi 404 nếu không tìm thấy
        }
        return ResponseEntity.ok(attributevalue.get());  // Trả về dữ liệu nếu tìm thấy
    }


    // Get attribute values by Value
    @GetMapping("/value")
    public ResponseEntity<List<Attributevalue>> getAttributevaluesByValue(@RequestParam String value) {
        List<Attributevalue> attributevalues = attributevalueRepository.findByValue(value);
        if (attributevalues.isEmpty()) {
            return ResponseEntity.status(404).body(null);  // Trả về lỗi 404 nếu không có kết quả
        }
        return ResponseEntity.ok(attributevalues);  // Trả về danh sách kết quả nếu tìm thấy
    }


    // Update an attribute value
    @PutMapping("/{id}")
    public ResponseEntity<String> updateAttributevalue(@PathVariable String id, @RequestBody Attributevalue attributevalue) {
        if (attributevalue.getValue() == null || attributevalue.getValue().isEmpty()) {
            return ResponseEntity.badRequest().body("Attribute value cannot be empty.");
        }

        if (!attributevalueRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Attribute value not found with ID: " + id);
        }

        if (!attributeRepository.existsById(attributevalue.getAttributeID().getAttributeID())) {
            return ResponseEntity.badRequest().body("Attribute not found with ID: " + attributevalue.getAttributeID().getAttributeID());
        }

        try {
            attributevalue.setAttributeValueID(id);
            attributevalueRepository.save(attributevalue);
            return ResponseEntity.ok("Attribute value updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating attribute value: " + e.getMessage());
        }
    }

    // Delete an attribute value
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttributevalue(@PathVariable String id) {
        if (!attributevalueRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Attribute value not found with ID: " + id);
        }

        try {
            attributevalueRepository.deleteById(id);
            return ResponseEntity.ok("Attribute value delete successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting attribute value: " + e.getMessage());
        }


    }
}
