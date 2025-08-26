package com.group7.ecommerce.dto.response.cart;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record CartItemResponse(
        int cartItemId,
        Long productId,
        String productName,
        String primaryImageUrl,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        Integer availableStock
) {}
