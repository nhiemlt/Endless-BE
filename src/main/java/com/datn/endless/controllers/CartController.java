package com.datn.endless.controllers;

import com.datn.endless.entities.Cart;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.User;
import com.datn.endless.repositories.CartRepository;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductversionRepository productversionRepository;

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
    public ResponseEntity<List<Map<String, Object>>> getCarts() {
        User user = getCurrentUser();
        if (user == null) {
            System.out.println("User not found or unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        System.out.println("User found: " + user.getUsername());

        // Lấy danh sách Cart từ cơ sở dữ liệu dựa trên User
        List<Cart> carts = cartRepository.findByUserID(user);

        if (carts.isEmpty()) {
            System.out.println("No carts found for user: " + user.getUsername());
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Tạo một danh sách để lưu trữ thông tin của Cart
        List<Map<String, Object>> cartInfoList = new ArrayList<>();

        // Duyệt qua từng Cart và trích xuất các trường cần thiết
        for (Cart cart : carts) {
            Map<String, Object> cartInfo = new HashMap<>();
            cartInfo.put("userID", cart.getUserID().getUserID());
            cartInfo.put("productVersionID", cart.getProductVersionID().getProductVersionID());
            cartInfo.put("quantity", cart.getQuantity());

            // Thêm vào danh sách kết quả
            cartInfoList.add(cartInfo);
        }

        return ResponseEntity.ok(cartInfoList);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<String> addToCart(@RequestBody Map<String, Object> requestBody) {
        String productVersionID = (String) requestBody.get("productVersionID");
        Integer quantity = (Integer) requestBody.get("quantity");
        // Lấy thông tin người dùng hiện tại đã đăng nhập
        User user = getCurrentUser();
        if (user == null) {
            System.out.println("User not found or unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or unauthorized");
        }

        // Kiểm tra productVersionID có hợp lệ không
        Productversion productVersion = productversionRepository.findById(productVersionID).orElse(null);
        if (productVersion == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product version not found");
        }

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
        Optional<Cart> existingCartOptional = cartRepository.findByUserIDAndProductVersionID(user, productVersion);

        if (existingCartOptional.isPresent()) {
            // Nếu sản phẩm đã có trong giỏ hàng, tăng thêm số lượng
            Cart existingCart = existingCartOptional.get();
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            cartRepository.save(existingCart);
            return ResponseEntity.ok("Quantity updated for existing product in cart");
        } else {
            // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới sản phẩm trong giỏ hàng
            Cart newCart = new Cart();
            newCart.setUserID(user);
            newCart.setProductVersionID(productVersion);
            newCart.setQuantity(quantity);
            cartRepository.save(newCart);
            return ResponseEntity.ok("Product added to cart successfully");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCartQuantity(@RequestBody Map<String, Object> payload) {
        // Lấy giá trị từ payload
        String productVersionID = (String) payload.get("productVersionID");
        Integer quantity = (Integer) payload.get("quantity");

        // Xác thực dữ liệu đầu vào
        if (productVersionID == null || quantity == null || quantity < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input data.");
        }

        // Lấy thông tin người dùng hiện tại
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or unauthorized");
        }

        // Tìm Productversion từ ID
        Productversion productVersion = productversionRepository.findById(productVersionID).orElse(null);
        if (productVersion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product version not found");
        }

        // Tìm sản phẩm trong giỏ hàng
        Optional<Cart> optionalCart = cartRepository.findByUserIDAndProductVersionID(user, productVersion);
        if (optionalCart.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart item not found");
        }

        Cart cart = optionalCart.get();

        // Cập nhật số lượng
        cart.setQuantity(quantity);
        cartRepository.save(cart);

        return ResponseEntity.ok("Cart quantity updated successfully");
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/{productVersionID}")
    public ResponseEntity<String> deleteCart(@PathVariable String productVersionID) {
        // Lấy thông tin người dùng hiện tại
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or unauthorized");
        }

        // Tìm Productversion từ ID
        Productversion productVersion = productversionRepository.findById(productVersionID).orElse(null);
        if (productVersion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product version not found");
        }

        // Tìm sản phẩm trong giỏ hàng của người dùng
        Optional<Cart> optionalCart = cartRepository.findByUserIDAndProductVersionID(user, productVersion);

        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cartRepository.delete(cart);
            return ResponseEntity.ok("Cart item deleted successfully");
        } else {
            // Nếu không tìm thấy sản phẩm trong giỏ hàng
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart item not found");
        }
    }

}

