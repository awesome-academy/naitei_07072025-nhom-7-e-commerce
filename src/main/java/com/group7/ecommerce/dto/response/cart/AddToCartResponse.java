package com.group7.ecommerce.dto.response.cart;

import lombok.Builder;

@Builder
public record AddToCartResponse(
        boolean success,
        String message,
        Integer cartItemId,
        Integer totalItemsInCart
) {}
