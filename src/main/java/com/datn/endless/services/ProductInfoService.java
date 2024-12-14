package com.datn.endless.services;

import com.datn.endless.dtos.*;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.ProductVersionNotFoundException;
import com.datn.endless.exceptions.ResourceNotFoundException;
import com.datn.endless.repositories.ProductInfoRepository;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.PromotionproductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ProductInfoService {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Autowired
    private EntryService entryService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private ProductversionRepository productVersionRepository;

    @Autowired
    private PromotionproductRepository promotionproductRepository;

    public ProductInfoDTO getByID(String productID){
        ProductInfo productInfo = productInfoRepository.findById(productID).orElseThrow(
                () -> new ResourceNotFoundException(productID)
        );
        return convertToDTO(productInfo);
    }

    public ProductInfoDTO getByProductVersionID(String productVersionID){
        ProductInfo productInfo = productInfoRepository.getProductInfoByProductVersionID(productVersionID);
        if(productInfo == null){
            throw new ResourceNotFoundException(productVersionID);
        }
        return convertToDTO(productInfo);
    }


    public Page<ProductInfoDTO> findAllProductInfos(int page, int size) {
        // Tạo đối tượng phân trang
        Pageable pageable = PageRequest.of(page, size);

        // Lấy tất cả các sản phẩm
        Page<ProductInfo> productInfos = productInfoRepository.findAll(pageable);

        // Chuyển đổi và trả về dữ liệu
        return productInfos.map(this::convertToDTO);
    }


    public Page<ProductInfoDTO> filterProductInfos(
            int page, int size, String sortBy, String direction,
            String keyword, List<String> categoryIDs, List<String> brandIDs,
            BigDecimal minPrice, BigDecimal maxPrice) {

        // Tạo đối tượng phân trang
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách sản phẩm dựa trên tiêu chí
        List<ProductInfo> productInfos = productInfoRepository.findProductInfoByCriteria(
                keyword, categoryIDs, brandIDs, minPrice, maxPrice);

        // Chuyển đổi và sắp xếp dữ liệu
        return getSortedProductInfos(sortBy, direction, pageable, productInfos);
    }

    private Page<ProductInfoDTO> getSortedProductInfos(
            String sortBy, String direction, Pageable pageable, List<ProductInfo> productInfos) {

        // Chuyển đổi danh sách ProductInfo sang ProductInfoDTO
        List<ProductInfoDTO> productInfoDTOs = productInfos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Xác định tiêu chí sắp xếp
        Comparator<ProductInfoDTO> comparator = getComparator(sortBy);

        // Áp dụng sắp xếp ngược nếu cần
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        // Sắp xếp danh sách
        List<ProductInfoDTO> sortedList = productInfoDTOs.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        // Áp dụng phân trang
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedList.size());
        return new PageImpl<>(sortedList.subList(start, end), pageable, sortedList.size());
    }

    private Comparator<ProductInfoDTO> getComparator(String sortBy) {
        switch (sortBy) {
            case "price":
                return Comparator.comparing(ProductInfoDTO::getPrice);
            case "name":
                return Comparator.comparing(ProductInfoDTO::getName);
            case "CategoryName":
                return Comparator.comparing(ProductInfoDTO::getCategoryName);
            case "BrandName":
                return Comparator.comparing(ProductInfoDTO::getBrandName);
            case "numberOfReviews":
                return Comparator.comparingDouble(dto -> dto.getProductVersionDTOs().stream()
                        .mapToLong(ProductDetailDTO::getNumberOfReviews)
                        .average()
                        .orElse(0));
            case "discountPrice":
                return Comparator.comparing(dto -> dto.getProductVersionDTOs().stream()
                        .map(ProductDetailDTO::getDiscountPrice)
                        .min(Comparator.naturalOrder())
                        .orElse(BigDecimal.ZERO));
            case "quantitySold":
                return Comparator.comparingDouble(dto -> dto.getProductVersionDTOs().stream()
                        .mapToDouble(ProductDetailDTO::getQuantitySold)
                        .sum());
            default:
                throw new IllegalArgumentException("Invalid sortBy parameter: " + sortBy);
        }
    }


    private ProductInfoDTO convertToDTO(ProductInfo productInfo) {
        // Kiểm tra xem có bất kỳ phiên bản sản phẩm nào không
        List<ProductDetailDTO> productDetailDTOs = productInfo.getProductversions().stream()
                .map(this::convertToProductDetailDTO)
                .collect(Collectors.toList());

        if (productDetailDTOs.isEmpty()) {
            // Xử lý khi không có phiên bản sản phẩm
            productDetailDTOs = null;
        }

        // Tạo ProductInfoDTO
        double totalQuantitySold = productDetailDTOs != null ?
                productDetailDTOs.stream().mapToDouble(ProductDetailDTO::getQuantitySold).sum() : 0;
        double totalQuantityAvailable = productDetailDTOs != null ?
                productDetailDTOs.stream().mapToDouble(ProductDetailDTO::getQuantityAvailable).sum() : 0;
        double totalNumberOfReviews = productDetailDTOs != null ?
                productDetailDTOs.stream().mapToDouble(ProductDetailDTO::getNumberOfReviews).sum() : 0;
        double averageRating = productDetailDTOs != null && totalNumberOfReviews > 0 ?
                productDetailDTOs.stream().mapToDouble(dto -> dto.getAverageRating() * dto.getNumberOfReviews())
                        .sum() / totalNumberOfReviews : 0;

        // Tính giá thấp nhất (min price)
        BigDecimal minPrice = productDetailDTOs != null ?
                productDetailDTOs.stream().map(ProductDetailDTO::getPrice)
                        .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO) : BigDecimal.ZERO;

        // Tính giá giảm thấp nhất (min discount price)
        BigDecimal minDiscountPrice = productDetailDTOs != null ?
                productDetailDTOs.stream().map(ProductDetailDTO::getDiscountPrice)
                        .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO) : BigDecimal.ZERO;

        // Tạo và trả về DTO
        return new ProductInfoDTO(
                productInfo.getProductID(),
                productInfo.getName(),
                minPrice,
                productInfo.getCategoryID().getName(),
                productInfo.getBrandID().getName(),
                productDetailDTOs != null ? calculateOverallDiscountPercentage(minPrice, minDiscountPrice) : 0,
                totalQuantityAvailable,
                totalQuantitySold,
                minDiscountPrice,
                averageRating,
                (long) totalNumberOfReviews,
                productDetailDTOs
        );
    }

    private ProductDetailDTO convertToProductDetailDTO(ProductversionInfo productVersion) {
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setProductVersionID(productVersion.getProductVersionID());
        dto.setVersionName(productVersion.getVersionName());
        dto.setPrice(productVersion.getPrice()); // Giá bán
        dto.setStatus(productVersion.getStatus());
        dto.setImage(productVersion.getImage());

        // Tính toán các thuộc tính khác
        dto.setDiscountPercentage(calculateDiscountPercentage(productVersion.getProductVersionID()));
        dto.setQuantitySold(entryService.getProductVersionOrderQuantity(productVersion.getProductVersionID()));
        dto.setQuantityAvailable(entryService.getProductVersionQuantity(productVersion.getProductVersionID()));
        dto.setNumberOfReviews(ratingService.getRatingCountByProductVersionId(productVersion.getProductVersionID()));

        // Tính trung bình đánh giá
        double averageRating = ratingService.getRatingsByProductVersionId(productVersion.getProductVersionID())
                .stream().mapToDouble(RatingDTO::getRatingValue).average().orElse(0);
        dto.setAverageRating(averageRating);

        // Tính giá giảm
        dto.setDiscountPrice(calculateDiscountPrice(productVersion.getProductVersionID()));

        // Chuyển đổi các thuộc tính phiên bản
        List<VersionAttributeInfoDTO> versionAttributes = productVersion.getVersionattributes().stream()
                .map(this::convertToVersionAttributeDTO)
                .collect(Collectors.toList());
        dto.setVersionAttributes(versionAttributes);

        return dto;
    }


    private VersionAttributeInfoDTO convertToVersionAttributeDTO(VersionattributeInfo versionAttribute) {
        VersionAttributeInfoDTO dto = new VersionAttributeInfoDTO();
        dto.setAttributeName(versionAttribute.getAttributeValueID().getAttribute().getAttributeName());
        dto.setAttributeValue(versionAttribute.getAttributeValueID().getValue());
        return dto;
    }


    private boolean isPromotionActive(Promotion promotion) {
        Instant now = Instant.now();
        return !promotion.getStartDate().isAfter(now) && !promotion.getEndDate().isBefore(now);
    }

    private double calculateOverallDiscountPercentage(BigDecimal originalPrice, BigDecimal discountPrice) {
        if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return originalPrice.subtract(discountPrice).divide(originalPrice, 2, RoundingMode.HALF_UP).doubleValue() * 100;
    }

    private BigDecimal calculateDiscountPrice(String productVersionID) {
        Productversion productVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm"));

        List<Promotionproduct> promotionProducts = promotionproductRepository
                .findByProductVersionIDAndPromotionStartDateBeforeAndPromotionEndDateAfter(
                        productVersionID, Instant.now());

        if (promotionProducts.isEmpty()) {
            return productVersion.getPrice();
        }

        Promotion validPromotion = promotionProducts.stream()
                .map(Promotionproduct::getPromotionID)
                .filter(this::isPromotionActive)
                .findFirst()
                .orElse(null);

        if (validPromotion == null) {
            return productVersion.getPrice();
        }

        BigDecimal discountPercent = BigDecimal.valueOf(validPromotion.getPercentDiscount());
        BigDecimal discountAmount = productVersion.getPrice().multiply(discountPercent).divide(BigDecimal.valueOf(100));
        return productVersion.getPrice().subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    private double calculateDiscountPercentage(String productVersionID) {
        Productversion productVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product Version not found"));

        BigDecimal originalPrice = productVersion.getPrice();
        BigDecimal discountPrice = calculateDiscountPrice(productVersionID);

        if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        BigDecimal discountPercentage = (originalPrice.subtract(discountPrice))
                .divide(originalPrice, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return discountPercentage.doubleValue();
    }
}
