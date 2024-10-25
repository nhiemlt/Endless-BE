package com.datn.endless.services;

import com.datn.endless.dtos.BrandDTO;
import com.datn.endless.entities.Brand;
import com.datn.endless.exceptions.ConvertImageException;
import com.datn.endless.models.BrandModel;
import com.datn.endless.repositories.BrandRepository;
import com.datn.endless.utils.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;



    public BrandDTO createBrand(BrandModel brandModel) {
        Brand newBrand = new Brand();
        newBrand.setBrandID(UUID.randomUUID().toString());
        newBrand.setName(brandModel.getName());
        newBrand.setLogo(brandModel.getLogo());
        return convertToDTO(brandRepository.save(newBrand));
    }

    public BrandDTO updateBrand(String id, BrandModel brandModel) {
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with ID: " + id));

        existingBrand.setName(brandModel.getName());
        existingBrand.setLogo(brandModel.getLogo());



        return convertToDTO(brandRepository.save(existingBrand));
    }

    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<BrandDTO> getBrandById(String id) {
        return brandRepository.findById(id).map(this::convertToDTO);
    }

    public void deleteBrand(String id) {
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand not found with ID: " + id);
        }
        brandRepository.deleteById(id);
    }

    private BrandDTO convertToDTO(Brand brand) {
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setBrandID(brand.getBrandID());
        brandDTO.setBrandName(brand.getName());
        brandDTO.setLogo(brand.getLogo());
        // Thêm các thuộc tính khác nếu cần
        return brandDTO;
    }
}
