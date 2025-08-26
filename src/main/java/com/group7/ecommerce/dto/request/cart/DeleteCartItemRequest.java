package com.group7.ecommerce.dto.request.cart;

import jakarta.validation.constraints.NotNull;

public record DeleteCartItemRequest(
        @NotNull(message = "Cart item ID is required")
        Integer cartItemId
) {}
