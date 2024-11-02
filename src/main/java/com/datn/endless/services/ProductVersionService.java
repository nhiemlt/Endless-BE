package com.datn.endless.services;

import com.datn.endless.dtos.*;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.*;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
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


    @Autowired
    private OrderdetailRepository orderDetailRepository;

    // Lấy danh sách ProductVersions với phân trang, lọc và sắp xếp
    public Page<ProductVersionDTO> getProductVersions(int page, int size, String sortBy, String direction, String keyword) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Productversion> pageResult = (keyword != null && !keyword.isEmpty())
                ? productVersionRepository.findByVersionNameContaining2(keyword, pageable)
                : productVersionRepository.findAll(pageable);

        return pageResult.map(this::convertToDTO);
    }

    public Page<ProductVersionDTO> getProductVersionsByKeyword(int page, int size, String sortBy, String direction, String keyword) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Tìm kiếm ProductVersion theo từ khóa
        Page<Productversion> pageResult = (keyword != null && !keyword.isEmpty())
                ? productVersionRepository.findByVersionNameContaining(keyword, pageable)
                : productVersionRepository.findByStatusActive(pageable);

        // Chuyển đổi các kết quả tìm kiếm thành DTOs
        return pageResult.map(this::convertToDTO);
    }



    // Lấy tất cả danh sách phiên bản sản phẩm theo người dùng.
    public Page<ProductVersionDTO> getActiveProductVersions(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Productversion> pageResult = productVersionRepository.findByStatusActive(pageable);

        return pageResult.map(this::convertToDTO);
    }

    // Lấy danh sách ProductVersions ở trạng thái Active theo tên sản phẩm
    public List<ProductVersionDTO> getActiveProductVersionsByProductName(String productName) {
        // Tìm sản phẩm theo tên
        List<Product> products = productRepository.findByNameContaining(productName);

        if (products.isEmpty()) {
            throw new ProductNotFoundException("Không tìm thấy sản phẩm với tên: " + productName);
        }

        // Lấy danh sách phiên bản sản phẩm ở trạng thái Active cho tất cả sản phẩm tìm thấy
        List<Productversion> activeProductVersions = new ArrayList<>();
        for (Product product : products) {
            // Lấy các phiên bản sản phẩm active theo sản phẩm
            List<Productversion> versions = productVersionRepository.findByProductID(product);
            activeProductVersions.addAll(versions.stream()
                    .filter(version -> version.getStatus().equals("Active")) // Lọc các phiên bản ở trạng thái Active
                    .collect(Collectors.toList()));
        }

        return activeProductVersions.stream()
                .map(this::convertToDTO) // Chuyển đổi từng Productversion sang ProductVersionDTO
                .collect(Collectors.toList());
    }


    // Lấy danh sách ProductVersions theo tên category hoặc tên brand
    public List<ProductVersionDTO> getProductVersionsByCategoryOrBrandName(String categoryName, String brandName) {
        List<Productversion> productVersions = new ArrayList<>();

        // Tìm kiếm sản phẩm theo category name
        if (categoryName != null && !categoryName.isEmpty()) {
            List<Product> productsByCategory = productRepository.findByCategoryNameContaining(categoryName);
            for (Product product : productsByCategory) {
                // Lấy các phiên bản sản phẩm theo sản phẩm và kiểm tra trạng thái 'Active'
                List<Productversion> activeVersions = productVersionRepository.findByProductID(product)
                        .stream()
                        .filter(version -> version.getStatus().equalsIgnoreCase("Active"))
                        .collect(Collectors.toList());
                productVersions.addAll(activeVersions);
            }
        }

        // Tìm kiếm sản phẩm theo brand name
        if (brandName != null && !brandName.isEmpty()) {
            List<Product> productsByBrand = productRepository.findByBrandNameContaining(brandName);
            for (Product product : productsByBrand) {
                // Lấy các phiên bản sản phẩm theo sản phẩm và kiểm tra trạng thái 'Active'
                List<Productversion> activeVersions = productVersionRepository.findByProductID(product)
                        .stream()
                        .filter(version -> version.getStatus().equalsIgnoreCase("Active"))
                        .collect(Collectors.toList());
                productVersions.addAll(activeVersions);
            }
        }

        // Kiểm tra xem có phiên bản nào có trạng thái Active không
        if (productVersions.isEmpty()) {
            throw new ProductVersionInactiveException("Tất cả phiên bản sản phẩm đã ngừng hoạt động.");
        }

        return productVersions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<ProductVersionDTO> filterProductVersionsByCategoriesAndBrands(
            List<String> categoryNames,
            List<String> brandNames,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        List<Productversion> productVersions = new ArrayList<>();

        // Lọc theo danh sách category names
        if (categoryNames != null && !categoryNames.isEmpty()) {
            for (String categoryName : categoryNames) {
                List<Product> productsByCategory = productRepository.findByCategoryNameContaining(categoryName);
                if (brandNames != null && !brandNames.isEmpty()) {
                    for (String brandName : brandNames) {
                        List<Product> productsByBrand = productRepository.findByBrandNameContaining(brandName);
                        for (Product product : productsByCategory) {
                            if (productsByBrand.contains(product)) {
                                // Lọc các phiên bản có trạng thái 'Active' và nằm trong khoảng giá
                                List<Productversion> activeVersions = productVersionRepository.findByProductID(product)
                                        .stream()
                                        .filter(version -> version.getStatus().equalsIgnoreCase("Active") &&
                                                version.getPrice().compareTo(minPrice) >= 0 &&
                                                version.getPrice().compareTo(maxPrice) <= 0)
                                        .collect(Collectors.toList());
                                productVersions.addAll(activeVersions);
                            }
                        }
                    }
                } else {
                    // Nếu không có brand names, lọc theo category và giá
                    for (Product product : productsByCategory) {
                        List<Productversion> activeVersions = productVersionRepository.findByProductID(product)
                                .stream()
                                .filter(version -> version.getStatus().equalsIgnoreCase("Active") &&
                                        version.getPrice().compareTo(minPrice) >= 0 &&
                                        version.getPrice().compareTo(maxPrice) <= 0)
                                .collect(Collectors.toList());
                        productVersions.addAll(activeVersions);
                    }
                }
            }
        }

        // Lọc theo danh sách brand names nếu không có category names
        if (brandNames != null && !brandNames.isEmpty() && (categoryNames == null || categoryNames.isEmpty())) {
            for (String brandName : brandNames) {
                List<Product> productsByBrand = productRepository.findByBrandNameContaining(brandName);
                for (Product product : productsByBrand) {
                    List<Productversion> activeVersions = productVersionRepository.findByProductID(product)
                            .stream()
                            .filter(version -> version.getStatus().equalsIgnoreCase("Active") &&
                                    version.getPrice().compareTo(minPrice) >= 0 &&
                                    version.getPrice().compareTo(maxPrice) <= 0)
                            .collect(Collectors.toList());
                    productVersions.addAll(activeVersions);
                }
            }
        }

        // Kiểm tra và thông báo nếu không có phiên bản sản phẩm nào thỏa mãn tiêu chí lọc
        if (productVersions.isEmpty()) {
            throw new ProductVersionInactiveException("Không có phiên bản sản phẩm nào hoạt động hoặc trong khoảng giá được chọn.");
        }

        return productVersions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<ProductVersionDTO> getTop5BestSellingProductVersionsThisMonth(Pageable pageable) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59);

        Page<Object[]> results = orderDetailRepository.findTopSellingProductVersionsInMonth(startOfMonth, endOfMonth, pageable);

        // Chuyển đổi sang ProductVersionDTO
        return results.map(result -> {
            String productVersionID = (String) result[0];
            Productversion productVersion = productVersionRepository.findById(productVersionID)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiên bản sản phẩm với ID: " + productVersionID));
            return convertToDTO(productVersion);
        });
    }


    public Page<ProductVersionDTO> getTop5BestSellingProductVersionsAllTime(Pageable pageable) {
        Page<Object[]> results = orderDetailRepository.findTopSellingProductVersionsAllTime(pageable);

        // Chuyển đổi sang ProductVersionDTO
        return results.map(result -> {
            String productVersionID = (String) result[0];
            Productversion productVersion = productVersionRepository.findById(productVersionID)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiên bản sản phẩm với ID: " + productVersionID));
            return convertToDTO(productVersion);
        });
    }



    // Lấy ProductVersion theo ID
    public ProductVersionDTO getProductVersionById(String productVersionID) {
        Productversion productVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm"));
        return convertToDTO(productVersion);
    }


    // Tạo mới ProductVersion
    public ProductVersionDTO createProductVersion(ProductVersionModel productVersionModel) {
        Product product = productRepository.findById(productVersionModel.getProductID())
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy sản phẩm"));


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
        productVersion.setImage(productVersionModel.getImage());
        productVersion.setStatus("Active");

        // Lưu phiên bản sản phẩm
        Productversion savedVersion = productVersionRepository.save(productVersion);
        saveVersionAttributes(productVersionModel.getAttributeValueID(), savedVersion);

        return convertToDTO(savedVersion);
    }


    // Cập nhật ProductVersion
    public ProductVersionDTO updateProductVersion(String productVersionID, ProductVersionModel productVersionModel) {

        Productversion existingProductVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm"));


        existingProductVersion.setVersionName(productVersionModel.getVersionName());
        existingProductVersion.setPurchasePrice(productVersionModel.getPurchasePrice());
        existingProductVersion.setPrice(productVersionModel.getPrice());
        existingProductVersion.setWeight(productVersionModel.getWeight());
        existingProductVersion.setHeight(productVersionModel.getHeight());
        existingProductVersion.setLength(productVersionModel.getLength());
        existingProductVersion.setWidth(productVersionModel.getWidth());
        existingProductVersion.setImage(productVersionModel.getImage());


        // Cập nhật thông tin
        Productversion updatedVersion = productVersionRepository.save(existingProductVersion);

        // Xóa các VersionAttribute cũ và thêm mới
        versionAttributeRepository.deleteByProductVersionID(productVersionID);
        saveVersionAttributes(productVersionModel.getAttributeValueID(), updatedVersion);

        return convertToDTO(updatedVersion);
    }

    // Cập nhật Status
    public ProductVersionDTO updateProductVersionStatus(String productVersionID, String status) {
        Productversion existingProductVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm"));

        // Cập nhật trạng thái
        existingProductVersion.setStatus(status); // Giả sử status là một thuộc tính trong Productversion
        Productversion updatedVersion = productVersionRepository.save(existingProductVersion);

        return convertToDTO(updatedVersion);
    }

    // Xóa ProductVersion
    public void deleteProductVersion(String productVersionID) {
        Productversion productVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm"));
        productVersionRepository.delete(productVersion);
    }

    // Chuyển đổi Productversion thành ProductVersionDTO
    private ProductVersionDTO convertToDTO(Productversion productVersion) {
        ProductForProcVersionDTO productDTO = new ProductForProcVersionDTO();
        productDTO.setProductID(productVersion.getProductID().getProductID());
        productDTO.setName(productVersion.getProductID().getName());
        productDTO.setCategoryName(productVersion.getProductID().getCategoryID().getName());
        productDTO.setBrandName(productVersion.getProductID().getBrandID().getName());

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
//        dto.setStatus(productVersion.getStatus());
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
                    promotionDTO.setStartDate(promotion.getStartDate());
                    promotionDTO.setEndDate(promotion.getEndDate());
                    promotionDTO.setPoster(promotion.getPoster());
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
