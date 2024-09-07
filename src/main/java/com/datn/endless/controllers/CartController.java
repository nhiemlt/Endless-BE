package com.datn.endless.controllers;

import com.datn.endless.entities.Cart;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.User;
import com.datn.endless.repositories.CartRepository;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.UserRepository;
import jakarta.annotation.security.PermitAll;
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
@RequestMapping("/api/carts")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    HttpSession session;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductversionRepository productVersionRepository;

    @Autowired
    HttpServletRequest req;

    @GetMapping
    public ResponseEntity<List<Cart>> getCarts() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<Cart> carts = cartRepository.findByUserID(user);
        return ResponseEntity.ok(carts);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Cart cart) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Productversion productVersion = productVersionRepository.findById(cart.getProductVersionID().getProductVersionID()).orElse(null);
        if (productVersion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product version not found");
        }

        Cart existingCart = cartRepository.findByUserIDAndProductVersionID(user, productVersion);
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + cart.getQuantity());
            cartRepository.save(existingCart);
        } else {
            cart.setUserID(user);
            cart.setProductVersionID(productVersion);
            cartRepository.save(cart);
        }

        return ResponseEntity.ok("Item added to cart for user: " + username);
    }

    @PutMapping("/update")
    public ResponseEntity<Cart> updateCart(@RequestParam String id, @RequestParam int quantityChange) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Cart cart = cartRepository.findById(id).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        int newQuantity = cart.getQuantity() + quantityChange;
        if (newQuantity < 1) {
            newQuantity = 1;
        }
        cart.setQuantity(newQuantity);
        cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Cart cart = cartRepository.findById(id).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        cartRepository.delete(cart);
        return ResponseEntity.noContent().build();
    }
}
