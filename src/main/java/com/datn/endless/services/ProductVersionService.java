package com.datn.endless.services;

import com.datn.endless.dtos.ProductForProcVersionDTO;
import com.datn.endless.dtos.ProductVersionDTO;
import com.datn.endless.dtos.VersionAttributeDTO;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.AttributeValueNotFoundException;
import com.datn.endless.exceptions.ProductNotFoundException;
import com.datn.endless.exceptions.ProductVersionNotFoundException;
import com.datn.endless.models.ProductVersionModel;
import com.datn.endless.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductVersionService {

    @Autowired
    private ProductversionRepository productVersionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AttributevalueRepository attributeValueRepository;

    @Autowired
    private  VersionattributeRepository versionAttributeRepository;

    @Autowired
    private PromotionproductRepository promotionproductRepository;

    // Lấy danh sách tất cả các ProductVersions
    public List<ProductVersionDTO> getAllProductVersions() {
        List<Productversion> productVersions = productVersionRepository.findAll();
        return productVersions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy ProductVersion theo ID
    public ProductVersionDTO getProductVersionById(String productVersionID) {
        Productversion productVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found"));
        return convertToDTO(productVersion);
    }

    // Tạo mới ProductVersion
    public ProductVersionDTO createProductVersion(ProductVersionModel productVersionModel) {
        Product product = productRepository.findById(productVersionModel.getProductID())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        Productversion productVersion = new Productversion();
        productVersion.setProductVersionID(UUID.randomUUID().toString());
        productVersion.setProductID(product);
        productVersion.setVersionName(productVersionModel.getVersionName());
        productVersion.setPurchasePrice(productVersionModel.getPurchasePrice());
        productVersion.setPrice(productVersionModel.getPrice());
        productVersion.setImage(productVersionModel.getImage());
        productVersion.setStatus("Active");

        // Lưu phiên bản sản phẩm
        Productversion savedVersion = productVersionRepository.save(productVersion);

        // Lưu các Attribute cho phiên bản
        List<String> attributeValueIDs = productVersionModel.getAttributeValueID();
        for (String attributeValueID : attributeValueIDs) {
            Attributevalue attributeValue = attributeValueRepository.findById(attributeValueID)
                    .orElseThrow(() -> new AttributeValueNotFoundException("Attribute Value not found"));

            Versionattribute versionAttribute = new Versionattribute();
            versionAttribute.setVersionAttributeID(UUID.randomUUID().toString());
            versionAttribute.setProductVersionID(savedVersion);
            versionAttribute.setAttributeValueID(attributeValue);
            versionAttributeRepository.save(versionAttribute);
        }

        return convertToDTO(savedVersion);
    }

    // Cập nhật ProductVersion
    public ProductVersionDTO updateProductVersion(String productVersionID, ProductVersionModel productVersionModel) {
        Productversion existingProductVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found"));

        existingProductVersion.setVersionName(productVersionModel.getVersionName());
        existingProductVersion.setPurchasePrice(productVersionModel.getPurchasePrice());
        existingProductVersion.setPrice(productVersionModel.getPrice());
        existingProductVersion.setImage(productVersionModel.getImage());

        // Cập nhật thông tin
        Productversion updatedVersion = productVersionRepository.save(existingProductVersion);

        // Xóa các VersionAttribute cũ và thêm mới
        versionAttributeRepository.deleteByProductVersionID(productVersionID);
        for (String attributeValueID : productVersionModel.getAttributeValueID()) {
            Attributevalue attributeValue = attributeValueRepository.findById(attributeValueID)
                    .orElseThrow(() -> new AttributeValueNotFoundException("Attribute Value not found"));

            Versionattribute versionAttribute = new Versionattribute();
            versionAttribute.setProductVersionID(updatedVersion);
            versionAttribute.setAttributeValueID(attributeValue);
            versionAttributeRepository.save(versionAttribute);
        }

        return convertToDTO(updatedVersion);
    }

    // Xóa ProductVersion
    public void deleteProductVersion(String productVersionID) {
        Productversion productVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found"));
        productVersionRepository.delete(productVersion);
    }

    private ProductVersionDTO convertToDTO(Productversion productVersion) {
        ProductForProcVersionDTO productDTO = new ProductForProcVersionDTO();
        productDTO.setProductID(productVersion.getProductID().getProductID());
        productDTO.setCategoryName(productVersion.getProductID().getCategoryID().getName());
        productDTO.setBrandName(productVersion.getProductID().getBrandID().getName());
        productDTO.setName(productVersion.getProductID().getName());
        productDTO.setNameEn(productVersion.getProductID().getNameEn());

        ProductVersionDTO dto = new ProductVersionDTO();
        dto.setProductVersionID(productVersion.getProductVersionID());
        dto.setProduct(productDTO);
        dto.setVersionName(productVersion.getVersionName());
        dto.setPurchasePrice(productVersion.getPurchasePrice());
        dto.setPrice(productVersion.getPrice());
        dto.setStatus(productVersion.getStatus());
        dto.setImage(productVersion.getImage());

        // Tính toán và thêm giá khuyến mãi vào DTO
        BigDecimal discountPrice = calculateDiscountPrice(productVersion.getProductVersionID());
        dto.setDiscountPrice(discountPrice);

        List<VersionAttributeDTO> versionAttributes = productVersion.getVersionattributes().stream()
                .map(va -> {
                    VersionAttributeDTO vaDTO = new VersionAttributeDTO();
                    vaDTO.setVersionAttributeID(va.getVersionAttributeID());
                    vaDTO.setAttributeName(va.getAttributeValueID().getAttribute().getAttributeName());
                    vaDTO.setAttributeValue(va.getAttributeValueID().getValue());
                    return vaDTO;
                })
                .collect(Collectors.toList());

        dto.setVersionAttributes(versionAttributes);
        return dto;
    }



    private BigDecimal calculateDiscountPrice(String productVersionID) {
        // Lấy giá gốc của sản phẩm
        BigDecimal price = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found"))
                .getPrice();

        // Khởi tạo giá giảm
        BigDecimal discountPricePerUnit = price; // Giá gốc là giá khuyến mãi nếu không có khuyến mãi nào áp dụng
        LocalDate now = LocalDate.now();

        try {
            // Lấy danh sách khuyến mãi áp dụng cho sản phẩm
            List<Promotionproduct> promotionProducts = promotionproductRepository.findByProductVersionID(productVersionID);

            boolean hasValidPromotion = false; // Biến đánh dấu xem có khuyến mãi hợp lệ không

            for (Promotionproduct promotionProduct : promotionProducts) {
                Promotiondetail promotionDetail = promotionProduct.getPromotionDetailID();
                Promotion promotion = promotionDetail.getPromotionID();

                // Kiểm tra thời gian khuyến mãi
                LocalDate startDate = promotion.getStartDate();
                LocalDate endDate = promotion.getEndDate();
                if (!now.isBefore(startDate) && !now.isAfter(endDate)) {
                    // Áp dụng khuyến mãi chỉ khi thời gian hiện tại nằm trong khoảng thời gian khuyến mãi
                    BigDecimal percentDiscount = BigDecimal.valueOf(promotionDetail.getPercentDiscount()).divide(BigDecimal.valueOf(100)); // e.g., 10 for 10%

                    // Tính toán giảm giá cho một đơn vị sản phẩm
                    BigDecimal discountAmountPerUnit = percentDiscount.multiply(price);
                    if (discountAmountPerUnit.compareTo(BigDecimal.ZERO) < 0) {
                        discountAmountPerUnit = BigDecimal.ZERO;
                    }
                    discountPricePerUnit = discountPricePerUnit.subtract(discountAmountPerUnit);

                    hasValidPromotion = true; // Đánh dấu đã có khuyến mãi hợp lệ
                }
            }

            // Nếu không có khuyến mãi hợp lệ, giá khuyến mãi bằng giá gốc
            if (!hasValidPromotion) {
                discountPricePerUnit = price;
            }

        } catch (Exception e) {
            // Log lỗi và trả về thông điệp lỗi chi tiết
            e.printStackTrace();
            throw new RuntimeException("Error calculating discount price: " + e.getMessage(), e);
        }

        // Đảm bảo giá không âm
        if (discountPricePerUnit.compareTo(BigDecimal.ZERO) < 0) {
            discountPricePerUnit = BigDecimal.ZERO;
        }

        return discountPricePerUnit;
    }

    // Lấy danh sách ProductVersions với phân trang, lọc và sắp xếp
    public Page<ProductVersionDTO> getProductVersions(int page, int size, String sortBy, String direction, String versionName) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Productversion> pageResult;
        if (versionName != null && !versionName.isEmpty()) {
            pageResult = productVersionRepository.findByVersionNameContaining(versionName, pageable);
        } else {
            pageResult = productVersionRepository.findAll(pageable);
        }
        return pageResult.map(this::convertToDTO);
    }
}

