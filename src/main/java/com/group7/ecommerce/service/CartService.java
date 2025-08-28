package com.group7.ecommerce.service;

import com.group7.ecommerce.dto.request.cart.AddToCartRequest;
import com.group7.ecommerce.dto.request.cart.DeleteCartItemRequest;
import com.group7.ecommerce.dto.request.cart.DeleteMultipleCartItemsRequest;
import com.group7.ecommerce.dto.request.cart.UpdateCartRequest;
import com.group7.ecommerce.dto.response.cart.AddToCartResponse;
import com.group7.ecommerce.dto.response.cart.DeleteCartItemResponse;
import com.group7.ecommerce.dto.response.cart.UpdateCartResponse;
import com.group7.ecommerce.dto.response.cart.ViewCartResponse;
import org.springframework.security.core.Authentication;

public interface CartService {

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    AddToCartResponse addToCart(Authentication authentication, AddToCartRequest request);

    /**
     * Cập nhật sản phẩm trong giỏ hàng
     */
    UpdateCartResponse updateCartItem(Authentication authentication, UpdateCartRequest request);

    /**
     * Xem danh sách sản phẩm trong giỏ hàng
     */
    ViewCartResponse viewCart(Authentication authentication);

    // Delete methods - Soft Delete
    DeleteCartItemResponse softDeleteCartItem(Authentication authentication, DeleteCartItemRequest request);
    DeleteCartItemResponse softDeleteMultipleCartItems(Authentication authentication, DeleteMultipleCartItemsRequest request);

    // Delete methods - Hard Delete
    DeleteCartItemResponse hardDeleteCartItem(Authentication authentication, DeleteCartItemRequest request);
    DeleteCartItemResponse hardDeleteMultipleCartItems(Authentication authentication, DeleteMultipleCartItemsRequest request);
}
