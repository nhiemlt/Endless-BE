package com.datn.endless.services;

import com.datn.endless.entities.Brand;
import com.datn.endless.repositories.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public Brand createBrand(Brand brand) {
        // Set default UUID if it's not provided
        if (brand.getBrandID() == null || brand.getBrandID().isEmpty()) {
            brand.setBrandID(java.util.UUID.randomUUID().toString());
        }
        return brandRepository.save(brand);
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Optional<Brand> getBrandById(String id) {
        return brandRepository.findById(id);
    }

    public Brand updateBrand(String id, Brand brand) {
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand not found with ID: " + id);
        }
        brand.setBrandID(id);
        return brandRepository.save(brand);
    }

    public void deleteBrand(String id) {
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand not found with ID: " + id);
        }
        brandRepository.deleteById(id);
    }
}
