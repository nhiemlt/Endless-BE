package com.datn.endless.services;

import com.datn.endless.dtos.*;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.*;
import com.datn.endless.models.ProductVersionModel;
import com.datn.endless.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private OrderdetailRepository orderdetailRepository;
    @Autowired
    private ProductversionRepository productversionRepository;


    // Tìm kiếm ProductVersion theo ID
    public ProductVersionDTO searchProductVersionById(String productVersionID) {
        // Tìm phiên bản sản phẩm dựa trên productVersionID
        Productversion productVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm với ID: " + productVersionID));

        // Chuyển đổi Productversion thành ProductVersionDTO
        return convertToDTO(productVersion);
    }


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
        // Tạo đối tượng Pageable với thông tin phân trang và sắp xếp
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Lọc danh sách ProductVersion theo từ khóa nếu có
        Page<Productversion> pageResult = (keyword != null && !keyword.isEmpty()) ?
                productversionRepository.findByVersionNameContaining(keyword, pageable) :
                productversionRepository.findByStatusActive(pageable);

        // Chuyển đổi danh sách ProductVersion thành DTOs
        return pageResult.map(this::convertToDTO);
    }


    public List<ProductVersionDTO> filterProductVersionsByCategoriesAndBrands(
            List<String> categoryNames,
            List<String> brandNames,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        List<Productversion> productVersions = new ArrayList<>();

        // Kiểm tra nếu không có categoryNames và brandNames, chỉ lọc theo giá
        if ((categoryNames == null || categoryNames.isEmpty()) && (brandNames == null || brandNames.isEmpty())) {
            // Lọc các phiên bản sản phẩm theo giá
            List<Productversion> activeVersions = productVersionRepository.findAll()
                    .stream()
                    .filter(version -> version.getStatus().equalsIgnoreCase("Active") &&
                            (minPrice == null || version.getPrice().compareTo(minPrice) >= 0) &&
                            (maxPrice == null || version.getPrice().compareTo(maxPrice) <= 0))
                    .collect(Collectors.toList());
            productVersions.addAll(activeVersions);
        }

        // Nếu có categoryNames hoặc brandNames, lọc theo các tiêu chí này
        else {
            // Lọc theo danh sách category names
            if (categoryNames != null && !categoryNames.isEmpty()) {
                for (String categoryName : categoryNames) {
                    List<Product> productsByCategory = productRepository.findByCategoryNameContaining(categoryName);
                    if (brandNames != null && !brandNames.isEmpty()) {
                        for (String brandName : brandNames) {
                            List<Product> productsByBrand = productRepository.findByBrandNameContaining(brandName);
                            for (Product product : productsByCategory) {
                                if (productsByBrand.contains(product)) {
                                    List<Productversion> activeVersions = productVersionRepository.findByProductID(product)
                                            .stream()
                                            .filter(version -> version.getStatus().equalsIgnoreCase("Active") &&
                                                    (minPrice == null || version.getPrice().compareTo(minPrice) >= 0) &&
                                                    (maxPrice == null || version.getPrice().compareTo(maxPrice) <= 0))
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
                                            (minPrice == null || version.getPrice().compareTo(minPrice) >= 0) &&
                                            (maxPrice == null || version.getPrice().compareTo(maxPrice) <= 0))
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
                                        (minPrice == null || version.getPrice().compareTo(minPrice) >= 0) &&
                                        (maxPrice == null || version.getPrice().compareTo(maxPrice) <= 0))
                                .collect(Collectors.toList());
                        productVersions.addAll(activeVersions);
                    }
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

    public ProductVersionDTO createProductVersion(ProductVersionModel productVersionModel) {
        // Kiểm tra sự tồn tại của sản phẩm
        Product product = productRepository.findById(productVersionModel.getProductID())
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy sản phẩm với ID: " + productVersionModel.getProductID()));

        // Kiểm tra trùng tên phiên bản sản phẩm cho cùng sản phẩm
        boolean isVersionNameExists = productVersionRepository.existsByProductIDAndVersionName(product, productVersionModel.getVersionName());
        if (isVersionNameExists) {
            throw new ProductVersionConflictException("Phiên bản sản phẩm với tên này đã tồn tại cho sản phẩm: " + productVersionModel.getVersionName());
        }

        // Tạo mới đối tượng phiên bản sản phẩm
        Productversion productVersion = new Productversion();
        productVersion.setProductVersionID(UUID.randomUUID().toString()); // Tạo ID phiên bản sản phẩm mới
        productVersion.setProductID(product); // Liên kết với sản phẩm
        productVersion.setVersionName(productVersionModel.getVersionName());
        productVersion.setPurchasePrice(productVersionModel.getPurchasePrice());
        productVersion.setPrice(productVersionModel.getPrice());
        productVersion.setWeight(productVersionModel.getWeight());
        productVersion.setHeight(productVersionModel.getHeight());
        productVersion.setLength(productVersionModel.getLength());
        productVersion.setWidth(productVersionModel.getWidth());
        productVersion.setImage(productVersionModel.getImage());
        productVersion.setStatus("Active"); // Mặc định trạng thái là Active

        // Lưu phiên bản sản phẩm vào cơ sở dữ liệu
        Productversion savedVersion = productVersionRepository.save(productVersion);

        // Lưu thông tin thuộc tính cho phiên bản sản phẩm
        saveVersionAttributes(productVersionModel.getAttributeValueID(), savedVersion);

        // Chuyển đổi thành DTO và trả về
        return convertToDTO(savedVersion);
    }


    public ProductVersionDTO updateProductVersion(String productVersionID, ProductVersionModel productVersionModel) {
        // Tìm kiếm phiên bản sản phẩm hiện tại
        Productversion existingProductVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm"));

        // Kiểm tra trùng versionName cho sản phẩm
        boolean isVersionNameExists = productVersionRepository.existsByProductIDAndVersionNameAndNotId(existingProductVersion.getProductID(), productVersionModel.getVersionName(), productVersionID);
        if (isVersionNameExists) {
            throw new ProductVersionConflictException("Phiên bản sản phẩm với tên này đã tồn tại");
        }

        // Cập nhật thông tin phiên bản sản phẩm
        existingProductVersion.setVersionName(productVersionModel.getVersionName());
        existingProductVersion.setPurchasePrice(productVersionModel.getPurchasePrice());
        existingProductVersion.setPrice(productVersionModel.getPrice());
        existingProductVersion.setWeight(productVersionModel.getWeight());
        existingProductVersion.setHeight(productVersionModel.getHeight());
        existingProductVersion.setLength(productVersionModel.getLength());
        existingProductVersion.setWidth(productVersionModel.getWidth());
        existingProductVersion.setImage(productVersionModel.getImage());

        // Lưu lại thông tin phiên bản sản phẩm đã cập nhật
        Productversion updatedVersion = productVersionRepository.save(existingProductVersion);

        // Xử lý VersionAttributes (Xoá những thuộc tính không còn liên quan và cập nhật các thuộc tính mới)
        versionAttributeRepository.deleteByProductVersionID(productVersionID);

        // Lưu lại các thuộc tính mới cho phiên bản sản phẩm
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

    // Tính toán giá khuyến mãi
    private BigDecimal calculateDiscountPrice(String productVersionID) {
        // Bước 1: Lấy thông tin của phiên bản sản phẩm từ database
        Productversion productVersion = productVersionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm"));

        // Bước 2: Lấy thông tin khuyến mãi áp dụng cho sản phẩm này trong thời gian hiện tại
        List<Promotionproduct> promotionproducts = promotionproductRepository.findByProductVersionIDAndPromotionStartDateBeforeAndPromotionEndDateAfter(
                productVersion.getProductVersionID(), Instant.now());

        if (promotionproducts.isEmpty()) {
            return productVersion.getPrice();  // Không có khuyến mãi, trả về giá gốc
        }

        // Bước 3: Lấy khuyến mãi hợp lệ đầu tiên
        Promotion validPromotion = null;
        for (Promotionproduct promotionproduct : promotionproducts) {
            Promotion promotion = promotionproduct.getPromotionID();

            // Kiểm tra xem khuyến mãi có đang trong thời gian hiệu lực hay không
            if (isPromotionActive(promotion)) {
                validPromotion = promotion;
                break;  // Dừng lại khi tìm thấy khuyến mãi hợp lệ đầu tiên
            }
        }

        // Nếu không có khuyến mãi hợp lệ, trả về giá gốc
        if (validPromotion == null) {
            return productVersion.getPrice();
        }

        // Bước 4: Tính toán giá sau khi giảm
        BigDecimal originalPrice = productVersion.getPrice();  // Lấy giá gốc của sản phẩm
        BigDecimal discountPercent = BigDecimal.valueOf(validPromotion.getPercentDiscount());
        BigDecimal discountAmount = originalPrice.multiply(discountPercent).divide(BigDecimal.valueOf(100));
        BigDecimal discountedPrice = originalPrice.subtract(discountAmount);

        // Bước 5: Trả về giá sau giảm
        return discountedPrice.setScale(2, RoundingMode.HALF_UP);  // Làm tròn đến 2 chữ số thập phân
    }

    // Hàm kiểm tra xem khuyến mãi có đang trong thời gian hiệu lực hay không
    private boolean isPromotionActive(Promotion promotion) {
        Instant now = Instant.now();
        return !promotion.getStartDate().isAfter(now) && !promotion.getEndDate().isBefore(now);
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

    // Chuyển đổi Productversion thành ProductVersionDTO
    ProductVersionDTO convertToDTO(Productversion productVersion) {
        ProductForProcVersionDTO productDTO = new ProductForProcVersionDTO();
        productDTO.setProductID(productVersion.getProductID().getProductID());
        productDTO.setName(productVersion.getProductID().getName());
        productDTO.setCategoryName(productVersion.getProductID().getCategoryID().getName());
        productDTO.setBrandName(productVersion.getProductID().getBrandID().getName());
        productDTO.setDescription(productVersion.getProductID().getDescription());

        ProductVersionDTO dto = new ProductVersionDTO();
        dto.setProductVersionID(productVersion.getProductVersionID());
        dto.setProduct(productDTO);
        dto.setVersionName(productVersion.getVersionName());
        dto.setPurchasePrice(productVersion.getPurchasePrice()); // giá nhap || giá góc
        dto.setPrice(productVersion.getPrice()); //gia ban
        dto.setWeight(productVersion.getWeight()); //gia ban
        dto.setHeight(productVersion.getHeight());
        dto.setLength(productVersion.getLength());
        dto.setWidth(productVersion.getWidth());
        dto.setStatus(productVersion.getStatus());

        dto.setDiscountPercentage(calculateDiscountPercentage(productVersion.getProductVersionID())); // Tỷ lệ phần trăm giảm giá
        dto.setQuantitySold(purchaseOrderService.getProductVersionOrderQuantity(productVersion.getProductVersionID())); // Số lượng đã bán
        dto.setQuantityAvailable(purchaseOrderService.getProductVersionQuantity(productVersion.getProductVersionID())); // Số lượng có sẵn
        List<RatingDTO> ratings = ratingService.getRatingsByProductVersionId(productVersion.getProductVersionID());
        dto.setNumberOfReviews(ratingService.getRatingCountByProductVersionId(productVersion.getProductVersionID())); // So luong danh gia cua sp
        dto.setAverageRating(ratings.stream().mapToDouble(RatingDTO::getRatingValue).average().orElse(0)); // Đánh giá trung bình
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
                    Promotion promotion = promotionProduct.getPromotionID();
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

    // Lưu các VersionAttribute cho ProductVersion
    private void saveVersionAttributes(List<String> attributeValueIDs, Productversion savedVersion) {
        for (String attributeValueID : attributeValueIDs) {
            Attributevalue attributeValue = attributeValueRepository.findById(attributeValueID)
                    .orElseThrow(() -> new AttributeValueNotFoundException("Giá trị thuộc tính không tồn tại."));

            Versionattribute versionAttribute = new Versionattribute();
            versionAttribute.setVersionAttributeID(UUID.randomUUID().toString());
            versionAttribute.setProductVersionID(savedVersion);
            versionAttribute.setAttributeValueID(attributeValue);
            versionAttributeRepository.save(versionAttribute);
        }
    }

    public List<ProductVersionDTO> getTop5BestSellingProductsByCategory(String categoryID) {
        Pageable pageable = PageRequest.of(0, 5); // Lấy top 5 sản phẩm
        Page<Object[]> results = orderDetailRepository.findTopSellingProductVersionsByCategory(categoryID, pageable);

        // Chuyển đổi các kết quả từ `Productversion` thành `ProductVersionDTO`
        return results.stream().map(result -> {
            Productversion productVersion = (Productversion) result[0]; // `result[0]` là `Productversion`
            return convertToDTO(productVersion);
        }).collect(Collectors.toList());
    }

    public List<ProductVersionDTO> getTop5BestSellingProductsByBrand(String brandID) {
        Pageable pageable = PageRequest.of(0, 5); // Lấy top 5 sản phẩm
        Page<Object[]> results = orderDetailRepository.findTopSellingProductVersionsByBrand(brandID, pageable);

        // Chuyển đổi các kết quả từ `Productversion` thành `ProductVersionDTO`
        return results.stream().map(result -> {
            Productversion productVersion = (Productversion) result[0]; // `result[0]` là `Productversion`
            return convertToDTO(productVersion);
        }).collect(Collectors.toList());
    }


    public List<ProductVersionDTO> getProductVersionsByBrand(String brandName) {
        // Lấy tất cả các sản phẩm theo tên thương hiệu
        List<Productversion> productVersions = productVersionRepository.findByProductID_BrandID_Name(brandName);

        if (productVersions.isEmpty()) {
            throw new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm cho thương hiệu: " + brandName);
        }

        // Chuyển đổi tất cả các Productversion thành ProductVersionDTO
        return productVersions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public long countProducts() {
        return productRepository.countProducts();
    }

    public long countBrands() {
        return productRepository.countBrands();
    }


    // Đếm số lượng đánh giá
    public long getRatingCountByProductVersionId(String productVersionID) {
        return ratingRepository.countByOrderDetailID_ProductVersionID_ProductVersionID(productVersionID);
    }

    // Số lượng đã bán
    public Integer getProductVersionOrderQuantity(String productVersionID) {
        Integer quantity = orderdetailRepository.findTotalSoldQuantityByProductVersion(productVersionID);
        return quantity == null ? 0 : quantity;
    }


    // Lấy danh sách sản phẩm có sắp xếp theo tiêu chí và chiều, chỉ lấy sản phẩm có trạng thái active
    public List<ProductVersionDTO> getSortedProductVersions(String sortBy, String direction) {
        // Lọc các sản phẩm có trạng thái active
        List<Productversion> productVersions = productversionRepository.findByStatus("Active");

        // Ánh xạ danh sách ProductVersion thành DTO
        List<ProductVersionDTO> productVersionDTOs = productVersions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Sắp xếp danh sách dựa trên tiêu chí
        Comparator<ProductVersionDTO> comparator;
        switch (sortBy) {
            case "numberOfReviews":
                comparator = Comparator.comparing(ProductVersionDTO::getNumberOfReviews);
                break;
            case "discountPrice":
                comparator = Comparator.comparing(ProductVersionDTO::getDiscountPrice);
                break;
            case "quantitySold":
                comparator = Comparator.comparing(ProductVersionDTO::getQuantitySold);
                break;
            default:
                throw new IllegalArgumentException("Invalid sortBy parameter: " + sortBy);
        }

        // Áp dụng chiều sắp xếp
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        // Sắp xếp danh sách
        return productVersionDTOs.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }




}
