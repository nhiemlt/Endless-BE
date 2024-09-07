package com.datn.endless.controllers;

import com.datn.endless.entities.Favorite;
import com.datn.endless.entities.Product;
import com.datn.endless.entities.User;
import com.datn.endless.repositories.FavoriteRepository;
import com.datn.endless.repositories.ProductRepository;
import com.datn.endless.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    HttpSession session;

    @Autowired
    FavoriteRepository favoriteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    HttpServletRequest req;

    @GetMapping
    public ResponseEntity<List<Favorite>> getFavorites() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<Favorite> favorites = favoriteRepository.findByUserID(user);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToFavorites(@RequestBody Favorite favorite) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Product product = productRepository.findById(favorite.getProductID().getProductID()).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        Favorite existingFavorite = favoriteRepository.findByUserIDAndProductID(user, product);
        if (existingFavorite != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Product already in favorites");
        } else {
            favorite.setUserID(user);
            favorite.setProductID(product);
            favoriteRepository.save(favorite);
        }

        return ResponseEntity.ok("Product added to favorites for user: " + username);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Tìm sản phẩm yêu thích theo ID
        Favorite favorite = favoriteRepository.findById(id).orElse(null);
        if (favorite == null || !favorite.getUserID().equals(user)) {
            return ResponseEntity.notFound().build();
        }

        // Xóa sản phẩm yêu thích khỏi danh sách yêu thích của người dùng
        favoriteRepository.delete(favorite);
        return ResponseEntity.noContent().build();
    }
}