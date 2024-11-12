package com.datn.endless.services;

import com.datn.endless.dtos.CartDTO;
import com.datn.endless.dtos.ProductVersionDTO;
import com.datn.endless.exceptions.QuantityException;
import com.datn.endless.models.CartModel;
import com.datn.endless.entities.Cart;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.User;
import com.datn.endless.exceptions.CartItemNotFoundException;
import com.datn.endless.exceptions.ProductVersionNotFoundException;
import com.datn.endless.exceptions.UserNotFoundException;
import com.datn.endless.repositories.CartRepository;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    @Autowired
    private final CartRepository cartRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ProductversionRepository productversionRepository;

    @Autowired
    private final UserLoginInfomation userLoginInformation;

    @Autowired
    private EntryService entryService;

    @Autowired
    private ProductVersionService productVersionService;

    // Lấy danh sách giỏ hàng của người dùng hiện tại
    public List<CartDTO> getCarts() {
        User currentUser = getCurrentUser();
        List<Cart> carts = cartRepository.findByUserID(currentUser);
        return carts.stream().map(this::convertToCartDTO).collect(Collectors.toList());
    }

    // Thêm sản phẩm vào giỏ hàng
    @Transactional
    public void addToCart(CartModel cartModel) {
        User currentUser = getCurrentUser();
        Productversion productVersion = productversionRepository.findById(cartModel.getProductVersionID())
                .orElseThrow(() -> new ProductVersionNotFoundException("Phiên bản sản phẩm không tìm thấy"));

        int prodQuantity = entryService.getProductVersionQuantity(cartModel.getProductVersionID());

        Cart cart = cartRepository.findByUserIDAndProductVersionID(currentUser, productVersion)
                .orElse(new Cart());

        if (cart.getCartID() == null) { // Cart mới
            cart.setUserID(currentUser);
            cart.setProductVersionID(productVersion);
            cart.setQuantity(cartModel.getQuantity());
        } else { // Sản phẩm đã có trong giỏ, cập nhật số lượng
            cart.setQuantity(cart.getQuantity() + cartModel.getQuantity());
        }
        if (cart.getQuantity() > prodQuantity) {
            throw new QuantityException("Số lượng vượt quá sản phẩm tồn kho!");
        }
        else{
            cartRepository.save(cart);
        }

    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @Transactional
    public void updateCartQuantity(CartModel cartModel) {
        User currentUser = getCurrentUser();
        Productversion productVersion = productversionRepository.findById(cartModel.getProductVersionID())
                .orElseThrow(() -> new ProductVersionNotFoundException("Phiên bản sản phẩm không tìm thấy"));
        int prodQuantity = entryService.getProductVersionQuantity(cartModel.getProductVersionID());

        Cart cart = cartRepository.findByUserIDAndProductVersionID(currentUser, productVersion)
                .orElseThrow(() -> new CartItemNotFoundException("Mặt hàng trong giỏ không tìm thấy"));
        if (cartModel.getQuantity() > prodQuantity) {
            throw new QuantityException("Số lượng vượt quá sản phẩm tồn kho!");
        } else {
            cart.setQuantity(cartModel.getQuantity());
            cartRepository.save(cart);
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @Transactional
    public void deleteCartItem(String productVersionID) {
        User currentUser = getCurrentUser();
        Productversion productVersion = productversionRepository.findById(productVersionID)
                .orElseThrow(() -> new ProductVersionNotFoundException("Phiên bản sản phẩm không tìm thấy"));

        Cart cart = cartRepository.findByUserIDAndProductVersionID(currentUser, productVersion)
                .orElseThrow(() -> new CartItemNotFoundException("Mặt hàng trong giỏ không tìm thấy"));

        cartRepository.delete(cart);
    }

    // Helper method để lấy người dùng hiện tại
    private User getCurrentUser() {
        String username = userLoginInformation.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            throw new UserNotFoundException("Người dùng không tìm thấy");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("Người dùng không tìm thấy với tên đăng nhập: " + username);
        }
        return user;
    }

    // Chuyển đổi từ Cart entity sang CartDTO
    private CartDTO convertToCartDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartID(cart.getCartID());
        Productversion productVersion = productversionRepository.findById(cart.getProductVersionID().getProductVersionID())
                .orElseThrow(() -> new ProductVersionNotFoundException("Phiên bản sản phẩm không tìm thấy"));
        ProductVersionDTO prodvDto = productVersionService.convertToDTO(productVersion);
        dto.setProductVersionID(prodvDto.getProductVersionID());
        dto.setProductName(prodvDto.getProduct().getName());
        dto.setVersionName(prodvDto.getVersionName());
        dto.setImage(prodvDto.getImage());
        dto.setPrice(prodvDto.getPrice());
        dto.setDiscountPrice(prodvDto.getDiscountPrice());

        dto.setQuantity(cart.getQuantity());
        dto.setWeight(prodvDto.getWeight().intValue());
        dto.setHeight(prodvDto.getHeight().intValue());
        dto.setLength(prodvDto.getLength().intValue());
        dto.setWidth(prodvDto.getWidth().intValue());
        return dto;
    }

    // Lấy tổng số lượng sản phẩm trong giỏ hàng của người dùng hiện tại
    public int getTotalCartQuantity() {
        User currentUser = getCurrentUser();
        List<Cart> carts = cartRepository.findByUserID(currentUser);

        return carts.stream().mapToInt(Cart::getQuantity).sum();
    }


}
