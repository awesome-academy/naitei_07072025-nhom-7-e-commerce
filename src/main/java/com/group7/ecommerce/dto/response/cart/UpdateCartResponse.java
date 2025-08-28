package com.group7.ecommerce.dto.response.cart;

import lombok.*;

@Builder
public record UpdateCartResponse (
        boolean success,
        String message,
        Integer cartItemId,
        Integer totalItemsInCart
) {}
