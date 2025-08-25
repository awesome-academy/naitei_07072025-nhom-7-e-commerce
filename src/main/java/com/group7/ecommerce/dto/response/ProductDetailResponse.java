package com.group7.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductDetailResponse(
        Long id,
        String name,
        String description,
        BigDecimal importPrice,
        BigDecimal sellingPrice,
        Integer stockQuantity,
        boolean isFeatured,
        boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        CategoryInfo category,
        List<ProductImageInfo> images,
        List<ReviewInfo> reviews,
        ReviewStats reviewStats
) {
    public record CategoryInfo(
            Long id,
            String name,
            String description
    ) {}
    
    public record ProductImageInfo(
            Long id,
            String imageUrl,
            boolean isPrimary
    ) {}
    
    public record ReviewInfo(
            Integer id,
            String userName,
            Integer rating,
            String comment,
            LocalDateTime createdAt
    ) {}
    
    public record ReviewStats(
            Double averageRating,
            Integer totalReviews,
            Integer fiveStars,
            Integer fourStars,
            Integer threeStars,
            Integer twoStars,
            Integer oneStar
    ) {}
}
