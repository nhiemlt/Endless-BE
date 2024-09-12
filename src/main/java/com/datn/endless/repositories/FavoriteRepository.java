package com.datn.endless.repositories;

import com.datn.endless.entities.Favorite;
import com.datn.endless.entities.Product;
import com.datn.endless.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, String> {
    List<Favorite> findAll();
    List<Favorite> findByUserID(User user);
    Favorite findByUserIDAndProductID(User user, Product product);
}