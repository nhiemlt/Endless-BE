package com.datn.endless.repositories;

import com.datn.endless.entities.Cart;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    // Phương thức để tìm giỏ hàng dựa trên userID và productVersionID

    Optional<Cart> findByUserIDAndProductVersionID(User user, Productversion productVersion);
    List<Cart> findByUserID(User user);
}