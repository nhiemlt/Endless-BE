package com.datn.endless.controllers;

import com.datn.endless.entities.Productversion;
import com.datn.endless.repositories.ProductversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/product-version")
public class ProductVersionController {

    @Autowired
    private ProductversionRepository ProductversionRepository;

    @GetMapping
    public List<Productversion> getAllProductversions() {
        return ProductversionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Productversion getProductversionById(@PathVariable String id) {
        return ProductversionRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Productversion createProductversion(@RequestBody Productversion Productversion) {
        return ProductversionRepository.save(Productversion);
    }

    @PutMapping("/{id}")
    public Productversion updateProductversion(@PathVariable String id, @RequestBody Productversion Productversion) {
        if (ProductversionRepository.existsById(id)) {
            Productversion.setProductVersionID(id);
            return ProductversionRepository.save(Productversion);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteProductversion(@PathVariable String id) {
        ProductversionRepository.deleteById(id);
    }
}
