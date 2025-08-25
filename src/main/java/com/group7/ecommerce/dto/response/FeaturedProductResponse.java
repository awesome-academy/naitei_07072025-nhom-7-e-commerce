package com.group7.ecommerce.dto.response;

import java.math.BigDecimal;

public record FeaturedProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal sellingPrice,
    String imageUrl,
    String categoryName,
    Integer stockQuantity,
    Long totalOrderQuantity // Tổng số lượng đã bán
) {}
