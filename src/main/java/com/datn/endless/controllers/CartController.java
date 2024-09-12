package com.datn.endless.controllers;

import com.datn.endless.dtos.CartDTO;
import com.datn.endless.entities.Cart;
import com.datn.endless.entities.User;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

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
    public ResponseEntity<List<CartDTO>> getCarts() {
        User user = getCurrentUser();
        if (user == null) {
            System.out.println("User not found or unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        System.out.println("User found: " + user.getUsername());

        List<CartDTO> carts = cartService.getCartsByUser(user);
        if (carts.isEmpty()) {
            System.out.println("No carts found for user: " + user.getUsername());
        }
        return ResponseEntity.ok(carts);
    }

//    @PostMapping("/add-to-cart")
//    public ResponseEntity<?> addToCart(@RequestBody CartDTO cartDTO) {
//        User user = getCurrentUser();
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
//        }
//
//        // Validate the input data (optional)
//        if (cartDTO.getQuantity() <= 0 || cartDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid quantity or price");
//        }
//
//        try {
//            CartDTO cart = new CartDTO();
//            cart.setUser(user); // Đảm bảo dùng đúng phương thức setUser
//            cart.setImage(cartDTO.getImage());
//            cart.setVersionName(cartDTO.getVersionName());
//            cart.setPurchasePrice(cartDTO.getPurchasePrice());
//            cart.setPrice(cartDTO.getPrice());
//            cart.setQuantity(cartDTO.getQuantity());
//
//            // Gọi service để thêm sản phẩm vào giỏ hàng
//            cartService.addToCart(user, cart);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//
//        return ResponseEntity.ok("Item added to cart");
//    }

    @PutMapping("/update")
    public ResponseEntity<CartDTO> updateCart(@RequestParam String id, @RequestParam int quantityChange) {
        try {
            Cart updatedCart = cartService.updateCartQuantity(id, quantityChange);

            // Convert updatedCart to CartDTO
            CartDTO updatedCartDTO = new CartDTO(
                    updatedCart.getProductVersionID().getImage(),
                    updatedCart.getProductVersionID().getVersionName(),
                    updatedCart.getProductVersionID().getPurchasePrice(),
                    updatedCart.getProductVersionID().getPrice(),
                    updatedCart.getQuantity()
            );

            return ResponseEntity.ok(updatedCartDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable String id) {
        try {
            cartService.deleteCart(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
