package com.datn.endless.services;

import com.datn.endless.dtos.BrandDTO;
import com.datn.endless.entities.Brand;
import com.datn.endless.models.BrandModel;
import com.datn.endless.repositories.BrandRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Transactional
    public BrandDTO createBrand(BrandModel brandModel) {
        validateBrandModel(brandModel);

        if (brandRepository.findByName(brandModel.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên thương hiệu đã tồn tại: " + brandModel.getName());
        }

        Brand newBrand = new Brand();
        newBrand.setBrandID(UUID.randomUUID().toString());
        newBrand.setName(brandModel.getName());
        newBrand.setLogo(brandModel.getLogo());
        return convertToDTO(brandRepository.save(newBrand));
    }

    @Transactional
    public BrandDTO updateBrand(String id, BrandModel brandModel) {
        validateBrandModel(brandModel);

        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thương hiệu có ID: " + id));

        if (!existingBrand.getName().equals(brandModel.getName()) &&
                brandRepository.findByName(brandModel.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên thương hiệu đã tồn tại: " + brandModel.getName());
        }

        existingBrand.setName(brandModel.getName());
        existingBrand.setLogo(brandModel.getLogo());

        return convertToDTO(brandRepository.save(existingBrand));
    }

    public Page<BrandDTO> getBrandsWithPaginationAndSearch(String keyword, Pageable pageable) {
        Page<Brand> brands;
        if (keyword != null && !keyword.isEmpty()) {
            brands = brandRepository.searchByName(keyword, pageable);
        } else {
            brands = brandRepository.findAll(pageable);
        }
        return brands.map(this::convertToDTO);
    }

    public Optional<BrandDTO> getBrandById(String id) {
        return brandRepository.findById(id).map(this::convertToDTO);
    }



    @Transactional
    public void deleteBrand(String id) {
        if (!brandRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy thương hiệu có ID: " + id);
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

    private void validateBrandModel(BrandModel brandModel) {
        if (!StringUtils.hasText(brandModel.getName())) {
            throw new IllegalArgumentException("Tên thương hiệu không được để trống.");
        }
        if (!StringUtils.hasText(brandModel.getLogo())) {
            throw new IllegalArgumentException("Logo thương hiệu không được để trống.");
        }

        // Kiểm tra định dạng ảnh cho logo
        String logo = brandModel.getLogo();
        String fileExtension = logo.substring(logo.lastIndexOf(".") + 1).toLowerCase();

        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif");
        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException("Định dạng ảnh không hợp lệ. Chỉ chấp nhận các định dạng: .jpg, .jpeg, .png, .gif");
        }
    }

}
