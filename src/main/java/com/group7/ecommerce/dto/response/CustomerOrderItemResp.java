package com.group7.ecommerce.dto.response;

import java.math.BigDecimal;

public record CustomerOrderItemResp(
        String productName,
        String productImage,
        int quantity,
        BigDecimal price,
        BigDecimal totalPrice
) {}
