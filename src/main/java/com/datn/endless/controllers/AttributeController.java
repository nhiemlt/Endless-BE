package com.datn.endless.controllers;

import com.datn.endless.dtos.AttributeDTO;

import com.datn.endless.exceptions.AttributeNotFoundException;
import com.datn.endless.repositories.AttributeRepository;
import com.datn.endless.services.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attributes")
public class AttributeController {

    @Autowired
    private AttributeService attributeService;
    @Autowired
    private AttributeRepository attributeRepository;

    // Get all attributes with filter, pagination, search
    @GetMapping
    public ResponseEntity<List<AttributeDTO>> getAllAttributes(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<AttributeDTO> attributes = attributeService.getAllAttributes(id, name, page, size);
        return ResponseEntity.ok(attributes);
    }

    // Create new attribute
    @PostMapping
    public ResponseEntity<AttributeDTO> createAttribute(@RequestBody AttributeDTO attributeDTO) {
        AttributeDTO createdAttribute = attributeService.createAttribute(attributeDTO);
        return ResponseEntity.ok(createdAttribute);
    }

    // Update attribute
    @PutMapping("/{id}")
    public ResponseEntity<AttributeDTO> updateAttribute(@PathVariable String id, @RequestBody AttributeDTO attributeDTO) {
        AttributeDTO updatedAttribute = attributeService.updateAttribute(id, attributeDTO);
        return ResponseEntity.ok(updatedAttribute);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttribute(@PathVariable String id) {
        try {
            attributeService.deleteAttribute(id);
            return ResponseEntity.ok("Thuộc tính xóa thành công.");
        } catch (AttributeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }


}
