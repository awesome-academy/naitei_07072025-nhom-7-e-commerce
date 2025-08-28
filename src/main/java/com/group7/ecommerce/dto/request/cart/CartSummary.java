package com.group7.ecommerce.dto.request.cart;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record CartSummary (
        Integer totalItems,
        Integer totalQuantity,
        BigDecimal totalAmount,
        BigDecimal estimatedAmount
) {}
