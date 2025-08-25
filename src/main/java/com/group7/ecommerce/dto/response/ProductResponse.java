package com.group7.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal importPrice,
        BigDecimal sellingPrice,
        int stockQuantity,
        String category,
        List<String> imageUrls,
        boolean isFeatured,
        boolean isDeleted,
        LocalDateTime createdAt
) { }
