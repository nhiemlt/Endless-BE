package com.datn.endless.controllers;

import com.datn.endless.dtos.CartDTO;
import com.datn.endless.dtos.ErrorResponse;
import com.datn.endless.models.CartModel;
import com.datn.endless.services.CartService;
import com.datn.endless.exceptions.CartItemNotFoundException;
import com.datn.endless.exceptions.ProductVersionNotFoundException;
import com.datn.endless.exceptions.UserNotFoundException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    // Lấy danh sách giỏ hàng
    @GetMapping
    public ResponseEntity<Object> getCarts() {
        try {
            List<CartDTO> carts = cartService.getCarts();
            return ResponseEntity.ok(carts);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Người dùng không tìm thấy", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Đã xảy ra lỗi không mong muốn", e.getMessage()));
        }
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add-to-cart")
    public ResponseEntity<Object> addToCart(@Valid @RequestBody CartModel cartModel) {
        try {
            cartService.addToCart(cartModel);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sản phẩm đã được thêm vào giỏ hàng thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Người dùng không tìm thấy", e.getMessage()));
        } catch (ProductVersionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Phiên bản sản phẩm không tìm thấy", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Đã xảy ra lỗi không mong muốn", e.getMessage()));
        }
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/update")
    public ResponseEntity<Object> updateCartQuantity(@Valid @RequestBody CartModel cartModel) {
        try {
            cartService.updateCartQuantity(cartModel);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Số lượng giỏ hàng đã được cập nhật thành công");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Người dùng không tìm thấy", e.getMessage()));
        } catch (ProductVersionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Phiên bản sản phẩm không tìm thấy", e.getMessage()));
        } catch (CartItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Sản phẩm trong giỏ hàng không tìm thấy", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Đã xảy ra lỗi không mong muốn", e.getMessage()));
        }
    }

    // Phương thức xử lý ngoại lệ để trả về thông báo lỗi xác thực
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/{productVersionID}")
    public ResponseEntity<Object> deleteCartItem(@PathVariable String productVersionID) {
        try {
            cartService.deleteCartItem(productVersionID);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sản phẩm trong giỏ hàng đã được xóa thành công");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Người dùng không tìm thấy", e.getMessage()));
        } catch (ProductVersionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Phiên bản sản phẩm không tìm thấy", e.getMessage()));
        } catch (CartItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Sản phẩm trong giỏ hàng không tìm thấy", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Đã xảy ra lỗi không mong muốn", e.getMessage()));
        }
    }

}
