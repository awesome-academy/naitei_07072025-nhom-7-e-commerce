package com.group7.ecommerce.dto.request.cart;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeleteMultipleCartItemsRequest(
        @NotEmpty(message = "Cart item IDs cannot be empty")
        List<Integer> cartItemIds
) {}
