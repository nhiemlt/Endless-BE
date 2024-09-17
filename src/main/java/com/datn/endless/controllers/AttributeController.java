package com.datn.endless.controllers;

import com.datn.endless.dtos.AttributeWithValueDTO;
import com.datn.endless.entities.Attribute;
import com.datn.endless.entities.Attributevalue;
import com.datn.endless.repositories.AttributeRepository;
import com.datn.endless.repositories.AttributevalueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attributes")
public class AttributeController {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributevalueRepository attributevalueRepository;

    // Create a new attribute
    @PostMapping
    public ResponseEntity<String> createAttribute(@RequestBody AttributeWithValueDTO attributeDTO) {
        if (attributeDTO.getName() == null || attributeDTO.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Attribute name cannot be empty.");
        }

        if (attributeRepository.existsByAttributeName(attributeDTO.getName())) {
            return ResponseEntity.badRequest().body("Attribute with the given name already exists.");
        }

        try {
            // Validate and create attribute
            Attribute attribute = new Attribute();
            attribute.setAttributeName(attributeDTO.getName());
            attribute.setEnAtributename(attributeDTO.getEnName());
            if (attribute.getAttributeID() == null || attribute.getAttributeID().isEmpty()) {
                attribute.setAttributeID(java.util.UUID.randomUUID().toString());
            }
            attributeRepository.save(attribute);

            // Create attribute value if provided
            if (attributeDTO.getValue() != null) {
                if (attributevalueRepository.existsByValue(attributeDTO.getValue())) {
                    return ResponseEntity.badRequest().body("Attribute value with the given value already exists.");
                }
                Attributevalue attributeValue = new Attributevalue();
                attributeValue.setAttributeID(attribute);
                attributeValue.setValue(attributeDTO.getValue());
                attributeValue.setEnValue(attributeDTO.getEnValue());
                attributeValue.setAttributeValueID(java.util.UUID.randomUUID().toString()); // Assign ID
                attributevalueRepository.save(attributeValue);
            }

            return ResponseEntity.ok("Attribute and Attribute Value created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating attribute or attribute value: " + e.getMessage());
        }
    }

    // Get all attributes
    @GetMapping
    public ResponseEntity<List<Attribute>> getAllAttributes() {
        try {
            List<Attribute> attributes = attributeRepository.findAll();
            return ResponseEntity.ok(attributes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get an attribute by ID
    @GetMapping("/{id}")
    public ResponseEntity<Attribute> getAttributeById(@PathVariable String id) {
        Optional<Attribute> attribute = attributeRepository.findById(id);
        if (attribute.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(attribute.get());
    }

    // Get attributes by name
    @GetMapping("/name")
    public ResponseEntity<List<Attribute>> searchAttributesByName(@RequestParam String name) {
        List<Attribute> attributes = attributeRepository.findByAttributeNameContaining(name);
        if (attributes.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(attributes);
    }





    // Update an attribute
    @PutMapping("/{id}")
    public ResponseEntity<String> updateAttribute(@PathVariable String id, @RequestBody Attribute attribute) {
        if (attribute.getAttributeName() == null || attribute.getAttributeName().isEmpty()) {
            return ResponseEntity.badRequest().body("Attribute name cannot be empty.");
        }

        Optional<Attribute> existingAttribute = attributeRepository.findById(id);
        if (existingAttribute.isEmpty()) {
            return ResponseEntity.status(404).body("Attribute not found with ID: " + id);
        }

        if (attributeRepository.existsByAttributeName(attribute.getAttributeName())) {
            return ResponseEntity.badRequest().body("Attribute with the given name already exists.");
        }

        try {
            attribute.setAttributeID(id);
            attributeRepository.save(attribute);
            return ResponseEntity.ok("Attribute updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating attribute: " + e.getMessage());
        }
    }

    // Delete an attribute
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttribute(@PathVariable String id) {
        if (!attributeRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Attribute not found with ID: " + id);
        }

        try {
            attributeRepository.deleteById(id);
            return ResponseEntity.ok("Attribute delete successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting attribute: " + e.getMessage());
        }
    }
}
