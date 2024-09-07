package com.datn.endless.controllers;

import com.datn.endless.entities.Attributevalue;
import com.datn.endless.repositories.AttributevalueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/attributevalues")
public class AttributeValueController {

    @Autowired
    private AttributevalueRepository attributevalueRepository;

    @GetMapping
    public List<Attributevalue> getAllAttributeValues() {
        return attributevalueRepository.findAll();
    }

    @GetMapping("/{id}")
    public Attributevalue getAttributeValueById(@PathVariable String id) {
        Optional<Attributevalue> attributevalue = attributevalueRepository.findById(id);
        return attributevalue.orElse(null);
    }

    @PostMapping
    public Attributevalue createAttributeValue(@RequestBody Attributevalue attributevalue) {
        return attributevalueRepository.save(attributevalue);
    }

    @PutMapping("/{id}")
    public Attributevalue updateAttributeValue(@PathVariable String id, @RequestBody Attributevalue attributevalue) {
        if (attributevalueRepository.existsById(id)) {
            attributevalue.setAttributeValueID(id);
            return attributevalueRepository.save(attributevalue);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteAttributeValue(@PathVariable String id) {
        attributevalueRepository.deleteById(id);
    }
}
