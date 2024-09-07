package com.datn.endless.repositories;

import com.datn.endless.entities.Cart;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, String> {
    List<Cart> findByUserID(User userID);
    Cart findByUserIDAndProductVersionID(User user, Productversion productVersion);
}