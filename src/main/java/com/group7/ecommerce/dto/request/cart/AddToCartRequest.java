package com.group7.ecommerce.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(
        @NotNull(message = "{validation.cart.productId.required}")
        Integer productId,

        @NotNull(message = "{validation.cart.quantity.required}")
        @Min(value = 1, message = "{validation.cart.quantity.min}")
        Integer quantity
) {}
