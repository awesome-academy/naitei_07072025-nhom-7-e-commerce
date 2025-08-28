package com.group7.ecommerce.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Record DTO để chứa các tiêu chí filter và sắp xếp sản phẩm động
 */
public record ProductFilterDto(
    // Tìm kiếm theo tên sản phẩm (LIKE search)
    String name,
    
    // Tìm kiếm theo mô tả (LIKE search)
    String description,
    
    // Lọc theo khoảng giá bán
    BigDecimal minSellingPrice,
    BigDecimal maxSellingPrice,
    
    // Lọc theo khoảng giá nhập
    BigDecimal minImportPrice,
    BigDecimal maxImportPrice,
    
    // Lọc theo số lượng tồn kho
    Integer minStockQuantity,
    Integer maxStockQuantity,
    
    // Lọc theo category ID
    Long categoryId,
    
    // Lọc theo tên category (LIKE search)
    String categoryName,
    
    // Lọc theo trạng thái nổi bật
    Boolean isFeatured,
    
    // Lọc theo khoảng thời gian tạo
    LocalDateTime createdAfter,
    LocalDateTime createdBefore,
    
    // Lọc theo khoảng thời gian cập nhật
    LocalDateTime updatedAfter,
    LocalDateTime updatedBefore,
    
    // Tham số sắp xếp
    // Trường để sắp xếp: name, sellingPrice, importPrice, stockQuantity, createdAt, updatedAt
    String sortBy,
    
    // Hướng sắp xếp: asc (tăng dần) hoặc desc (giảm dần)
    String sortDirection
) {}
