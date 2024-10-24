package com.datn.endless.services;

import com.datn.endless.dtos.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
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
    private VersionattributeRepository versionAttributeRepository;

    @Autowired
    private PromotionproductRepository promotionproductRepository;

    @Autowired
    private EntryService purchaseOrderService;

    @Autowired
    private RatingService ratingService;

    // Lấy danh sách ProductVersions với phân trang, lọc và sắp xếp
    public Page<ProductVersionDTO> getProductVersions(int page, int size, String sortBy, String direction, String versionName) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Productversion> pageResult = (versionName != null && !versionName.isEmpty())
                ? productVersionRepository.findByVersionNameContaining(versionName, pageable)
                : productVersionRepository.findAll(pageable);

        return pageResult.map(this::convertToDTO);
    }

    // Lấy danh sách ProductVersions theo ProductID
    public List<ProductVersionDTO> getProductVersionsByProductId(String productID) {
        Product product = productRepository.findById(productID)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        List<Productversion> productVersions = productVersionRepository.findByProductID(product);

        return productVersions.stream()
                .map(this::convertToDTO) // Chuyển đổi từng Productversion sang ProductVersionDTO
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
        productVersion.setWeight(productVersionModel.getWeight());
        productVersion.setHeight(productVersionModel.getHeight());
        productVersion.setLength(productVersionModel.getLength());
        productVersion.setWidth(productVersionModel.getWidth());

        productVersion.setImage(convertImageToBase64(productVersionModel.getImage())); // Chuyển đổi hình ảnh

        productVersion.setStatus("Active");

        // Lưu phiên bản sản phẩm
        Productversion savedVersion = productVersionRepository.save(productVersion);
        saveVersionAttributes(productVersionModel.getAttributeValueID(), savedVersion);

        return convertToDTO(savedVersion);
    }

    // Hàm chuyển đổi MultipartFile thành String (base64)
    private String convertImageToBase64(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null; // Hoặc xử lý theo nhu cầu
        }

        try {
            byte[] bytes = file.getBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Error converting image to base64: " + e.getMessage(), e);
        }
    }
    // Cập nhật ProductVersion
    public ProductVersionDTO updateProductVersion(String productVersionID, ProductVersionModel productVersionModel) {
        Productversion existingProductVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found"));

        existingProductVersion.setVersionName(productVersionModel.getVersionName());
        existingProductVersion.setPurchasePrice(productVersionModel.getPurchasePrice());
        existingProductVersion.setPrice(productVersionModel.getPrice());
        existingProductVersion.setWeight(productVersionModel.getWeight());
        existingProductVersion.setHeight(productVersionModel.getHeight());
        existingProductVersion.setLength(productVersionModel.getLength());
        existingProductVersion.setWidth(productVersionModel.getWidth());
        existingProductVersion.setImage(convertImageToBase64(productVersionModel.getImage())); // Chuyển đổi hình ảnh

        // Cập nhật thông tin
        Productversion updatedVersion = productVersionRepository.save(existingProductVersion);

        // Xóa các VersionAttribute cũ và thêm mới
        versionAttributeRepository.deleteByProductVersionID(productVersionID);
        saveVersionAttributes(productVersionModel.getAttributeValueID(), updatedVersion);

        return convertToDTO(updatedVersion);
    }



    // Xóa ProductVersion
    public void deleteProductVersion(String productVersionID) {
        Productversion productVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found"));
        productVersionRepository.delete(productVersion);
    }

    // Chuyển đổi Productversion thành ProductVersionDTO
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
        dto.setPurchasePrice(productVersion.getPurchasePrice()); // giá nhap || giá góc
        dto.setPrice(productVersion.getPrice()); //gia ban
        dto.setShipFee(productVersion.getWeight()); //gia ban
        dto.setHeight(productVersion.getHeight());
        dto.setLength(productVersion.getLength());
        dto.setWidth(productVersion.getWidth());

        dto.setDiscountPercentage(calculateDiscountPercentage(productVersion.getProductVersionID())); // Tỷ lệ phần trăm giảm giá

        dto.setQuantitySold(purchaseOrderService.getProductVersionOrderQuantity(productVersion.getProductVersionID())); // Số lượng đã bán
        dto.setQuantityAvailable(purchaseOrderService.getProductVersionQuantity(productVersion.getProductVersionID())); // Số lượng có sẵn




        List<RatingDTO> ratings = ratingService.getRatingsByProductVersionId(productVersion.getProductVersionID());
        dto.setAverageRating(ratings.stream().mapToDouble(RatingDTO::getRatingValue).average().orElse(0)); // Đánh giá trung bình
        dto.setNumberOfReviews(ratingService.getRatingCountByProductVersionId(productVersion.getProductVersionID())); // So luong danh gia cua sp


        dto.setStatus(productVersion.getStatus());
        dto.setImage(productVersion.getImage());

        // Tính toán và thêm giá khuyến mãi vào DTO
        dto.setDiscountPrice(calculateDiscountPrice(productVersion.getProductVersionID()));

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


        List<PromotionDTO> promotions = promotionproductRepository.findByProductVersionID(productVersion.getProductVersionID())
                .stream()
                .map(promotionProduct -> {
                    Promotion promotion = promotionProduct.getPromotionDetailID().getPromotionID();
                    PromotionDTO promotionDTO = new PromotionDTO();
                    promotionDTO.setPromotionID(promotion.getPromotionID());
                    promotionDTO.setName(promotion.getName());
                    promotionDTO.setEnName(promotion.getEnName());
                    promotionDTO.setStartDate(promotion.getStartDate());
                    promotionDTO.setEndDate(promotion.getEndDate());
                    promotionDTO.setPoster(promotion.getPoster());
                    promotionDTO.setEnDescription(promotion.getEnDescription());
                    return promotionDTO;
                })
                .collect(Collectors.toList());
        dto.setPromotions(promotions); // Đưa danh sách khuyến mãi vào DTO


        return dto;
    }

    // Tính toán giá khuyến mãi
    private BigDecimal calculateDiscountPrice(String productVersionID) {
        BigDecimal price = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found"))
                .getPrice();

        BigDecimal discountPricePerUnit = price;
        LocalDate now = LocalDate.now();

        List<Promotionproduct> promotionProducts = promotionproductRepository.findByProductVersionID(productVersionID);
        boolean hasValidPromotion = false;

        for (Promotionproduct promotionProduct : promotionProducts) {
            Promotiondetail promotionDetail = promotionProduct.getPromotionDetailID();
            Promotion promotion = promotionDetail.getPromotionID();

            LocalDate startDate = promotion.getStartDate();
            LocalDate endDate = promotion.getEndDate();
            if (!now.isBefore(startDate) && !now.isAfter(endDate)) {
                BigDecimal percentDiscount = BigDecimal.valueOf(promotionDetail.getPercentDiscount()).divide(BigDecimal.valueOf(100));
                discountPricePerUnit = discountPricePerUnit.subtract(percentDiscount.multiply(price));
                hasValidPromotion = true;
            }
        }

        return hasValidPromotion ? discountPricePerUnit.max(BigDecimal.ZERO) : price;
    }
    // Tính toán tỷ lệ phần trăm giảm giá cho ProductVersion
    private double calculateDiscountPercentage(String productVersionID) {
        Productversion productVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found"));

        BigDecimal originalPrice = productVersion.getPrice();
        BigDecimal discountPrice = calculateDiscountPrice(productVersionID); // Sử dụng hàm đã có để lấy giá sau khi giảm

        // Tính tỷ lệ phần trăm giảm giá
        if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0; // Tránh chia cho 0
        }

        BigDecimal discountPercentage = (originalPrice.subtract(discountPrice))
                .divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP) // Làm tròn tới 2 chữ số thập phân
                .multiply(BigDecimal.valueOf(100)); // Chuyển thành phần trăm

        return discountPercentage.doubleValue(); // Trả về giá trị giảm giá dưới dạng double
    }




    // Lưu các VersionAttribute cho ProductVersion
    private void saveVersionAttributes(List<String> attributeValueIDs, Productversion savedVersion) {
        for (String attributeValueID : attributeValueIDs) {
            Attributevalue attributeValue = attributeValueRepository.findById(attributeValueID)
                    .orElseThrow(() -> new AttributeValueNotFoundException("Attribute Value not found"));

            Versionattribute versionAttribute = new Versionattribute();
            versionAttribute.setVersionAttributeID(UUID.randomUUID().toString());
            versionAttribute.setProductVersionID(savedVersion);
            versionAttribute.setAttributeValueID(attributeValue);
            versionAttributeRepository.save(versionAttribute);
        }
    }







}
