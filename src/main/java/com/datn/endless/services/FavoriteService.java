package com.datn.endless.services;

import com.datn.endless.dtos.FavoriteDTO;
import com.datn.endless.entities.Favorite;
import com.datn.endless.entities.Product;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.User;
import com.datn.endless.exceptions.ProductVersionNotFoundException;
import com.datn.endless.exceptions.UserNotFoundException;
import com.datn.endless.repositories.FavoriteRepository;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    @Autowired
    private UserRepository userRepository;

    private final UserLoginInfomation userLoginInformation;

    // Helper method để lấy người dùng hiện tại
    private User getCurrentUser() {
        String username = userLoginInformation.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    public List<FavoriteDTO> getFavorites() {
        User user = getCurrentUser();
        List<Favorite> favorites = favoriteRepository.findByUserID(user);

        return favorites.stream()
                .map(favorite -> {
                    Product product = favorite.getProductID();
                    if (product != null) {
                        Productversion productVersion = productversionRepository.findFirstByProductID_ProductID(product.getProductID());
                        if (productVersion != null) {
                            return new FavoriteDTO(
                                    favorite.getFavoriteID(),
                                    product.getProductID(),
                                    productVersion.getImage(),
                                    productVersion.getVersionName(),
                                    productVersion.getPrice()
                            );
                        }
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public String toggleFavorite(String productVersionId) {
        User user = getCurrentUser();
        Productversion productVersion = productversionRepository.findById(productVersionId)
                .orElseThrow(() -> new ProductVersionNotFoundException("Product version not found"));

        Product product = productVersion.getProductID();
        Favorite favorite = favoriteRepository.findByUserIDAndProductID(user, product).orElse(null);

        if (favorite != null) {
            favoriteRepository.delete(favorite);
            return "Removed from favorites";
        } else {
            Favorite newFavorite = new Favorite();
            newFavorite.setUserID(user);
            newFavorite.setProductID(product);
            favoriteRepository.save(newFavorite);
            return "Added to favorites";
        }
    }
}
