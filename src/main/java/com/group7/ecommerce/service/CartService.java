package com.group7.ecommerce.service;

import com.group7.ecommerce.dto.request.cart.AddToCartRequest;
import com.group7.ecommerce.dto.response.cart.AddToCartResponse;
import org.springframework.security.core.Authentication;

public interface CartService {

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    AddToCartResponse addToCart(Authentication authentication, AddToCartRequest request);

}
