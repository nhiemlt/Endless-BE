package com.datn.endless.controllers;

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

import java.util.*;

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

    private User getCurrentUser() {
        //Lấy thông tin xác thực của người dùng
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //Kiểm tra kiểu dữ liệu của principa
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            //Truy xuất thông tin người dùng từ cơ sở dữ liệu
            return userRepository.findByUsername(username);
        }
        return null;

    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getFavorites() {
        // Lấy thông tin người dùng hiện tại
        User user = getCurrentUser();
        if (user == null) {
            System.out.println("User not found or unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        System.out.println("User found: " + user.getUsername());

        // Tìm tất cả các sản phẩm yêu thích của người dùng
        List<Favorite> favorites = favoriteRepository.findByUserID(user);

        if (favorites.isEmpty()) {
            System.out.println("No favorites found for user: " + user.getUsername());
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Tạo một danh sách để lưu trữ thông tin của Favorite
        List<Map<String, Object>> favoriteInfoList = new ArrayList<>();

        // Duyệt qua từng Favorite và trích xuất các trường cần thiết
        for (Favorite favorite : favorites) {
            Map<String, Object> favoriteInfo = new HashMap<>();
            favoriteInfo.put("favorite", favorite.getFavoriteID());
            favoriteInfo.put("userID", favorite.getUserID().getUserID());
            favoriteInfo.put("productID", favorite.getProductID().getProductID());

            // Thêm vào danh sách kết quả
            favoriteInfoList.add(favoriteInfo);
        }

        return ResponseEntity.ok(favoriteInfoList);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addFavorite(@RequestBody Map<String, Object> requestBody) {
        // Lấy thông tin người dùng hiện tại đã đăng nhập
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated. Please log in.");
        }

        // Lấy productID từ requestBody
        String productID = (String) requestBody.get("productID");
        if (productID == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product ID is missing.");
        }

        // Tìm sản phẩm theo productID
        Product product = productRepository.findById(productID).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product not found.");
        }

        // Kiểm tra xem sản phẩm đã có trong danh sách yêu thích chưa
        Optional<Favorite> existingFavoriteOptional = favoriteRepository.findByUserIDAndProductID(user, product);
        if (existingFavoriteOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product is already in the favorites list.");
        }

        // Tạo đối tượng Favorite mới và lưu vào cơ sở dữ liệu
        Favorite favorite = new Favorite();
        favorite.setUserID(user);
        favorite.setProductID(product);
        favoriteRepository.save(favorite);

        return ResponseEntity.status(HttpStatus.CREATED).body("Product added to favorites successfully.");
    }

    @DeleteMapping("/delete/{productID}")
    public ResponseEntity<String> removeFavorite(@PathVariable String productID) {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated. Please log in.");
        }

        Product product = productRepository.findById(productID).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product not found.");
        }

        Optional<Favorite> existingFavoriteOptional = favoriteRepository.findByUserIDAndProductID(user, product);
        if (!existingFavoriteOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product is not in the favorites list.");
        }

        Favorite favorite = existingFavoriteOptional.get();
        favoriteRepository.delete(favorite);

        return ResponseEntity.ok("Product removed from favorites successfully.");
    }
}