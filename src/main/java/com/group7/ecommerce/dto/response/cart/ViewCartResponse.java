package com.group7.ecommerce.dto.response.cart;

import com.group7.ecommerce.dto.request.cart.CartSummary;
import lombok.*;

import java.util.List;

@Builder
public record ViewCartResponse (
        boolean success,
        String message,
        List<CartItemResponse> cartItems,
        CartSummary summary
) {}
