package com.datn.endless.repositories;

import com.datn.endless.entities.Favorite;
import com.datn.endless.entities.Product;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, String> {
    List<Favorite> findByUserID(User user);
    Optional<Favorite> findByUserIDAndProductID(User user, Product product);
    boolean existsByUserIDAndProductID(User user, Product product);
}