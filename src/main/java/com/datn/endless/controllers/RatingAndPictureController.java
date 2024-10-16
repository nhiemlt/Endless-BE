package com.datn.endless.controllers;

import com.datn.endless.dtos.RatingDTO;
import com.datn.endless.exceptions.EntityNotFoundException;
import com.datn.endless.models.RatingModel;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.services.RatingService;
import com.datn.endless.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class RatingAndPictureController {

    @Autowired
    private RatingService ratingService;

    // Lấy tất cả đánh giá với lọc và phân trang
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRatings(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        try {
            PageRequest pageable = PageRequest.of(page, size);
            Page<RatingDTO> ratings = ratingService.getAllRatings(userId, pageable);

            response.put("success", true);
            response.put("data", ratings.getContent());
            response.put("totalPages", ratings.getTotalPages());
            response.put("totalElements", ratings.getTotalElements());
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

    // Lấy danh sách đánh giá theo productVersionID
    @GetMapping("/productVersion/{productVersionID}")
    public List<RatingDTO> getRatingsByProductVersionId(@PathVariable String productVersionID) {
        return ratingService.getRatingsByProductVersionId(productVersionID);
    }

    // Thêm đánh giá
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addRating(
            @RequestParam("orderDetailId") String orderDetailId,
            @RequestParam("ratingValue") int ratingValue,
            @RequestParam("comment") String comment,
            @RequestParam(value = "pictures", required = false) MultipartFile[] pictures) {

        Map<String, String> response = new HashMap<>();
        try {
            RatingModel ratingModel = new RatingModel();
            ratingModel.setOrderDetailId(orderDetailId);
            ratingModel.setRatingValue(ratingValue);
            ratingModel.setComment(comment);
            ratingModel.setPictures(pictures);

            RatingDTO newRating = ratingService.addRating(ratingModel);
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
