package com.datn.endless.controllers;

import com.datn.endless.dtos.ErrorResponse;
import com.datn.endless.dtos.FavoriteDTO;
import com.datn.endless.exceptions.ProductNotFoundException;
import com.datn.endless.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getFavorites() {
        List<FavoriteDTO> favorites = favoriteService.getFavorites();
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/toggle/{productVersionId}")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@PathVariable String productVersionId) {
        Map<String, Object> response = new HashMap<>();
        try {
            String message = favoriteService.toggleFavorite(productVersionId);
            response.put("success", true);
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            response.put("success", false);
            response.put("error", "Product not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
