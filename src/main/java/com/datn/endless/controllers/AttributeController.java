package com.datn.endless.controllers;

import com.datn.endless.entities.Attribute;
import com.datn.endless.repositories.AttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/attributes")
public class AttributeController {

    @Autowired
    private AttributeRepository attributeRepository;

    @GetMapping
    public List<Attribute> getAllAttributes() {
        return attributeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Attribute getAttributeById(@PathVariable String id) {
        Optional<Attribute> attribute = attributeRepository.findById(id);
        return attribute.orElse(null);
    }

    @PostMapping
    public Attribute createAttribute(@RequestBody Attribute attribute) {
        return attributeRepository.save(attribute);
    }

    @PutMapping("/{id}")
    public Attribute updateAttribute(@PathVariable String id, @RequestBody Attribute attribute) {
        if (attributeRepository.existsById(id)) {
            attribute.setAttributeID(id);
            return attributeRepository.save(attribute);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteAttribute(@PathVariable String id) {
        attributeRepository.deleteById(id);
    }
}
