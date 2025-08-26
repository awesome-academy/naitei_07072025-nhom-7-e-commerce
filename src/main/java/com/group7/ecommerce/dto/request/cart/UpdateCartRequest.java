package com.group7.ecommerce.dto.request.cart;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
public record UpdateCartRequest (
        @NotNull(message = "{validation.cart.cartItemId.required}")
        Long cartItemId,

        @NotNull(message = "{validation.cart.quantity.required}")
        @Min(value = 1, message = "{validation.cart.quantity.min}")
        @Max(value = 100, message = "{validation.cart.quantity.max}")
        Integer quantity
) {}
