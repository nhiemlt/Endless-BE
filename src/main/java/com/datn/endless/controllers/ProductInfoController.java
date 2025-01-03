package com.datn.endless.controllers;

import com.datn.endless.dtos.ProductInfoDTO;
import com.datn.endless.exceptions.ResourceNotFoundException;
import com.datn.endless.services.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/product-info")
public class ProductInfoController {

    @Autowired
    private ProductInfoService productInfoService;

    @GetMapping()
    public ResponseEntity<Page<ProductInfoDTO>> getAllProductInfos(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // Lấy dữ liệu từ dịch vụ
        Page<ProductInfoDTO> productInfos = productInfoService.findAllProductInfos(page, size);

        // Trả về ResponseEntity chứa trang sản phẩm
        return ResponseEntity.ok(productInfos);
    }

    // API lấy thông tin sản phẩm theo ProductID
    @GetMapping("/{productID}")
    public ResponseEntity<?> getProductByID(@PathVariable String productID) {
        try {
            ProductInfoDTO productInfoDTO = productInfoService.getByID(productID);
            return ResponseEntity.ok(productInfoDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy sản phẩm với ID: " + productID);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi không mong muốn.");
        }
    }

    // API lấy thông tin sản phẩm theo ProductVersionID
    @GetMapping("/by-version/{productVersionID}")
    public ResponseEntity<?> getProductByProductVersionID(@PathVariable String productVersionID) {
        try {
            ProductInfoDTO productInfoDTO = productInfoService.getByProductVersionID(productVersionID);
            return ResponseEntity.ok(productInfoDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy sản phẩm với ProductVersionID: " + productVersionID);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi không mong muốn.");
        }
    }


    // API để lọc danh sách sản phẩm theo tiêu chí
    @GetMapping("/filter")
    public ResponseEntity<Page<ProductInfoDTO>> filterProductInfos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "quantitySold") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> categoryIDs,
            @RequestParam(required = false) List<String> brandIDs,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        // Danh sách các thuộc tính hỗ trợ sắp xếp
        List<String> sortableFields = List.of("numberOfReviews", "discountPrice", "quantitySold");

        // Kiểm tra sortBy hợp lệ
        if (!sortableFields.contains(sortBy) || sortBy.isEmpty()) {
            sortBy = "quantitySold";
        }

        // Kiểm tra sortBy hợp lệ
        if (!sortableFields.contains(sortBy)) {
            return ResponseEntity.badRequest().body(null);
        }

        Page<ProductInfoDTO> productInfoPage = productInfoService.filterProductInfos(
                page, size, sortBy, direction, keyword, categoryIDs, brandIDs, minPrice, maxPrice
        );
        return ResponseEntity.ok(productInfoPage);
    }
}
