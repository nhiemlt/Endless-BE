package com.datn.endless.controllers;

import com.datn.endless.dtos.RatingDTO;
import com.datn.endless.dtos.RatingDTO2;
import com.datn.endless.entities.Rating;
import com.datn.endless.exceptions.EntityNotFoundException;
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
            response.put("error", "An unexpected error occurred: " + e.getMessage());
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
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

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
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Lấy danh sách đánh giá theo productVersionID
    @GetMapping("/productVersion/{productVersionID}")
    public List<RatingDTO> getRatingsByProductVersionId(@PathVariable String productVersionID) {
        return ratingService.getRatingsByProductVersionId(productVersionID);
    } 

    // Thêm đánh giá mới
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addRating(
            @RequestParam("orderDetailId") String orderDetailId,
            @RequestParam("ratingValue") int ratingValue,
            @RequestParam("comment") String comment,
            @RequestParam(value = "pictures", required = false) String[] pictures) {

        Map<String, String> response = new HashMap<>();
        try {
            // Tạo đối tượng RatingModel từ dữ liệu đầu vào
            RatingModel ratingModel = new RatingModel();
            ratingModel.setOrderDetailId(orderDetailId);
            ratingModel.setRatingValue(ratingValue);
            ratingModel.setComment(comment);
            // Xử lý hình ảnh nếu có
            if (pictures != null && pictures.length > 0) {
                ratingModel.setPictures(Arrays.asList(pictures)); // Chuyển mảng chuỗi thành danh sách
            }
            // Thêm đánh giá thông qua service
            RatingDTO2 newRating = ratingService.addRating(ratingModel);

            // Trả về phản hồi thành công
            response.put("success", "true");
            response.put("message", "Rating added successfully");
            response.put("ratingID", newRating.getRatingID());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserNotFoundException e) {
            response.put("success", "false");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", "false");
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
