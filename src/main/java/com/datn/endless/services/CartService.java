package com.datn.endless.services;

import com.datn.endless.dtos.CartDTO;
import com.datn.endless.entities.Cart;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.User;
import com.datn.endless.repositories.CartRepository;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductversionRepository productVersionRepository;

    public List<CartDTO> getCartsByUser(User user) {
        List<Cart> carts = cartRepository.findByUserID(user);
        return carts.stream().map(cart -> new CartDTO(
                cart.getProductVersionID().getImage(),
                cart.getProductVersionID().getVersionName(),
                cart.getProductVersionID().getPurchasePrice(),
                cart.getProductVersionID().getPrice(),
                cart.getQuantity()
        )).collect(Collectors.toList());
    }

//    public void addToCart(User user, CartDTO cart) {
//        Productversion productVersion = productVersionRepository.findById(cart.).orElse(null);
//        if (productVersion == null) {
//            throw new IllegalArgumentException("Product version not found");
//        }
//
//        Cart existingCart = cartRepository.findByUserIDAndProductVersionID(user, productVersion);
//        if (existingCart != null) {
//            existingCart.setQuantity(existingCart.getQuantity() + cart.getQuantity());
//            cartRepository.save(existingCart);
//        } else {
//            cart.setUserID(user);
//            cart.setProductVersionID(productVersion);
//            cartRepository.save(cart);
//        }
//    }

    public Cart updateCartQuantity(String cartID, int quantityChange) {
        Cart cart = cartRepository.findById(cartID).orElse(null);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        int newQuantity = cart.getQuantity() + quantityChange;
        if (newQuantity < 1) {
            newQuantity = 1;
        }
        cart.setQuantity(newQuantity);
        return cartRepository.save(cart);
    }

    public void deleteCart(String cartID) {
        Cart cart = cartRepository.findById(cartID).orElse(null);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }
        cartRepository.delete(cart);
    }
}
