package com.datn.endless.repositories;

import com.datn.endless.entities.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, String> {
}