package com.group7.ecommerce.dto.response.cart;

import com.group7.ecommerce.dto.request.cart.CartSummary;
import lombok.Builder;

import java.util.List;

@Builder
public record DeleteCartItemResponse(
        boolean success,
        String message,
        List<Integer> deletedCartItemIds,
        List<Integer> failedCartItemIds,
        Integer totalItemsInCart,
        CartSummary summary
) {}
