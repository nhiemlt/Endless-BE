package com.datn.endless.controllers;

import com.datn.endless.dtos.RatingDTO;
import com.datn.endless.dtos.RatingDTO2;
import com.datn.endless.entities.Rating;
import com.datn.endless.exceptions.DuplicateResourceException;
import com.datn.endless.exceptions.EntityNotFoundException;
import com.datn.endless.exceptions.ForbidenException;
import com.datn.endless.models.RatingModel;
import com.datn.endless.services.RatingService;
import com.datn.endless.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class RatingAndPictureController {

    @Autowired
    private RatingService ratingService;

    // Lấy tất cả đánh giá
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRatings(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int ratingValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        try {
            PageRequest pageable = PageRequest.of(page, size, Sort.by("ratingDate").ascending()); // Sắp xếp theo ratingDate
            Page<RatingDTO2> ratings = ratingService.getRatingsByKeyWord(keyword, ratingValue, pageable);

            response.put("success", true);
            response.put("data", ratings);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Lấy đánh giá theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRatingById(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            RatingDTO rating = ratingService.getRatingById(id);
            response.put("success", true);
            response.put("data", rating);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Lấy đánh giá theo ID (phiên bản 2)
    @GetMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> getRatingById2(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            RatingDTO2 rating = ratingService.getRatingById2(id); // Gọi phương thức mới từ service
            response.put("success", true);
            response.put("data", rating);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Lấy danh sách đánh giá theo productVersionID
    @GetMapping("/productVersion/{productVersionID}")
    public List<RatingDTO> getRatingsByProductVersionId(@PathVariable String productVersionID) {
        return ratingService.getRatingsByProductVersionId(productVersionID);
    }


    // API xóa đánh giá theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable String id) {
        ratingService.deleteRatingById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Thêm đánh giá mới
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addRating(
            @RequestBody @Valid RatingModel ratingModel,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            response.put("success", "false");
            response.put("message", "Dữ liệu đầu vào không hợp lệ");
            // Gửi danh sách lỗi chi tiết
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            response.put("errors", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // Thêm đánh giá thông qua service
            RatingDTO2 newRating = ratingService.addRating(ratingModel);

            // Trả về phản hồi thành công
            response.put("success", "true");
            response.put("message", "Đánh giá đã được thêm thành công");
            response.put("ratingID", newRating.getRatingID());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserNotFoundException e) {
            response.put("success", "false");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (ForbidenException e) {
            response.put("success", "false");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (DuplicateResourceException e) {
            response.put("success", "false");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", "false");
            response.put("message", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
