package com.group7.ecommerce.dto.response;

import java.math.BigDecimal;

/**
 * Projection interface cho ProductListItem
 * Sử dụng Spring Data JPA Projections để mapping trực tiếp từ query
 * Giúp code ngắn gọn hơn so với SELECT new ...
 */
public interface ProductListItemProjection {
    
    Long getId();
    
    String getName();
    
    String getDescription();
    
    BigDecimal getSellingPrice();
    
    String getImageUrl();
    
    String getCategoryName();
    
    Integer getStockQuantity();
    
    /**
     * Map sang ProductListItemResponse
     */
    default ProductListItemResponse toProductListItemResponse() {
        return new ProductListItemResponse(
            getId(),
            getName(),
            getDescription(),
            getSellingPrice(),
            getImageUrl(),
            getCategoryName(),
            getStockQuantity()
        );
    }
}
