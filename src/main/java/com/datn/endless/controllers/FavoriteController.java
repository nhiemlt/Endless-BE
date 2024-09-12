package com.datn.endless.controllers;

import com.datn.endless.dtos.FavoriteDTO;
import com.datn.endless.entities.*;
import com.datn.endless.repositories.FavoriteRepository;
import com.datn.endless.repositories.ProductRepository;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {


    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<?> getFavorites() {
        // Lấy thông tin người dùng từ SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Kiểm tra kiểu của principal
        if (!(principal instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated. Please log in.");
        }

        UserDetails userDetails = (UserDetails) principal;
        String username = userDetails.getUsername();

        // Xác thực người dùng
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated. Please log in.");
        }

        // Tìm tất cả các sản phẩm yêu thích của người dùng
        List<Favorite> favorites = favoriteRepository.findByUserID(user);
        if (favorites.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No favorites found.");
        }

        // Chuyển đổi danh sách Favorite thành danh sách FavoriteDTO
        List<FavoriteDTO> favoriteDTOs = favorites.stream()
                .map(favorite -> {
                    Productversion productVersion = productversionRepository.findById(favorite.getProductID().getProductID()).orElse(null);
                    if (productVersion != null) {
                        String nameVersionName = productVersion.getProductID().getName() + " " + productVersion.getVersionName();
                        return new FavoriteDTO(
                                productVersion.getImage(),
                                nameVersionName,
                                productVersion.getPurchasePrice(),
                                productVersion.getPrice()
                        );
                    }
                    return null;
                })
                .filter(favoriteDTO -> favoriteDTO != null)
                .collect(Collectors.toList());

        return ResponseEntity.ok(favoriteDTOs);
    }


    @PostMapping("/add")
    public ResponseEntity<String> addFavorite(
            @RequestParam("productVersionID") String productVersionID,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Xác thực người dùng
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated. Please log in.");
        }

        // Kiểm tra nếu sản phẩm đã tồn tại trong danh sách yêu thích
        Productversion productVersion = productversionRepository.findById(productVersionID).orElse(null);
        if (productVersion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product version not found.");
        }

        Favorite existingFavorite = favoriteRepository.findByUserIDAndProductID(user, productVersion.getProductID());
        if (existingFavorite != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product already in favorites.");
        }

        // Tạo và lưu mới Favorite
        Favorite favorite = new Favorite();
        favorite.setUserID(user);
        favorite.setProductID(productVersion.getProductID());
        favoriteRepository.save(favorite);

        return ResponseEntity.ok("Product added to favorites.");
    }

    @DeleteMapping("/delete/{favoriteID}")
    public ResponseEntity<String> removeFavorite(
            @PathVariable("favoriteID") String favoriteID,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Xác thực người dùng
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated. Please log in.");
        }

        // Tìm Favorite để xóa
        Favorite favorite = favoriteRepository.findById(favoriteID).orElse(null);
        if (favorite == null || !favorite.getUserID().equals(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorite not found.");
        }

        // Xóa Favorite
        favoriteRepository.delete(favorite);

        return ResponseEntity.ok("Product removed from favorites.");
    }

}