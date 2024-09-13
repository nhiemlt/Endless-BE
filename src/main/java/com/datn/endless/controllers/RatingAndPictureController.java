package com.datn.endless.controllers;
import com.datn.endless.entities.Orderdetail;
import com.datn.endless.entities.Rating;
import com.datn.endless.entities.User;
import com.datn.endless.repositories.OrderRepository;
import com.datn.endless.repositories.OrderdetailRepository;
import com.datn.endless.repositories.RatingRepository;
import com.datn.endless.repositories.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
public class RatingAndPictureController {

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderdetailRepository orderdetailRepository;

    // Lấy tất cả các đánh giá
    @GetMapping
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    // Lấy đánh giá theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Rating> getRatingById(@PathVariable("id") String id) {
        Optional<Rating> rating = ratingRepository.findById(id);
        return rating.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

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

    @PostMapping("/add")
    public ResponseEntity<String> createRating(@RequestBody Rating rating) {
        // Kiểm tra thông tin người dùng hiện tại
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User is not logged in. Please log in to submit a rating.");
        }

        // Kiểm tra nếu ratingValue hợp lệ
        if (rating.getRatingValue() == null || rating.getRatingValue() < 1 || rating.getRatingValue() > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid rating value. The rating must be between 1 and 5.");
        }

        // Kiểm tra nếu orderDetailID hợp lệ
        if (rating.getOrderDetailID() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("OrderDetailID cannot be null.");
        }

        // Gán thông tin người dùng vào đánh giá
        rating.setUserID(currentUser);

        // Save rating to the database
        try {
            Rating savedRating = ratingRepository.save(rating);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Rating has been successfully added.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the rating: " + e.getMessage());
        }
    }

    // Lưu đánh giá vào cơ sở dữ liệu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable("id") String id) {
        if (!ratingRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ratingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
