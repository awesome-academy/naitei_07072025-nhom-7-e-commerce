package com.group7.ecommerce.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Record DTO để chứa các tiêu chí filter và sắp xếp sản phẩm động
 */
public record ProductFilterDto(
    // Tìm kiếm theo tên sản phẩm (LIKE search)
    String name,
    
    // Tìm kiếm theo mô tả (LIKE search)
    String description,
    
    // Lọc theo khoảng giá bán
    @Min(value = 0, message = "Giá bán tối thiểu phải >= 0")
    BigDecimal minSellingPrice,
    
    @Min(value = 0, message = "Giá bán tối đa phải >= 0")
    BigDecimal maxSellingPrice,
    
    // Lọc theo khoảng giá nhập
    @Min(value = 0, message = "Giá nhập tối thiểu phải >= 0")
    BigDecimal minImportPrice,
    
    @Min(value = 0, message = "Giá nhập tối đa phải >= 0")
    BigDecimal maxImportPrice,
    
    // Lọc theo số lượng tồn kho
    @Min(value = 0, message = "Số lượng tồn kho tối thiểu phải >= 0")
    Integer minStockQuantity,
    
    @Min(value = 0, message = "Số lượng tồn kho tối đa phải >= 0")
    Integer maxStockQuantity,
    
    // Lọc theo category ID
    @Min(value = 1, message = "ID category phải > 0")
    Long categoryId,
    
    // Lọc theo tên category (LIKE search)
    String categoryName,
    
    // Lọc theo trạng thái nổi bật
    Boolean isFeatured,
    
    // Lọc theo khoảng thời gian tạo (string format cho binding)
    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", 
             message = "Định dạng thời gian không hợp lệ. Sử dụng: yyyy-MM-ddTHH:mm:ss")
    String createdAfter,
    
    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", 
             message = "Định dạng thời gian không hợp lệ. Sử dụng: yyyy-MM-ddTHH:mm:ss")
    String createdBefore,
    
    // Lọc theo khoảng thời gian cập nhật (string format cho binding)
    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", 
             message = "Định dạng thời gian không hợp lệ. Sử dụng: yyyy-MM-ddTHH:mm:ss")
    String updatedAfter,
    
    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", 
             message = "Định dạng thời gian không hợp lệ. Sử dụng: yyyy-MM-ddTHH:mm:ss")
    String updatedBefore,
    
    // Tham số sắp xếp
    // Trường để sắp xếp: name, sellingPrice, importPrice, stockQuantity, createdAt, updatedAt
    String sortBy,
    
    // Hướng sắp xếp: asc (tăng dần) hoặc desc (giảm dần)
    String sortDirection
) {
    /**
     * Constructor với giá trị mặc định
     */
    public ProductFilterDto {
        // Set default values if null
        sortBy = sortBy != null ? sortBy : "createdAt";
        sortDirection = sortDirection != null ? sortDirection : "desc";
    }
    
    /**
     * Chuyển đổi datetime strings thành LocalDateTime objects
     */
    public LocalDateTime getParsedCreatedAfter() {
        return parseDateTime(createdAfter);
    }
    
    public LocalDateTime getParsedCreatedBefore() {
        return parseDateTime(createdBefore);
    }
    
    public LocalDateTime getParsedUpdatedAfter() {
        return parseDateTime(updatedAfter);
    }
    
    public LocalDateTime getParsedUpdatedBefore() {
        return parseDateTime(updatedBefore);
    }
    
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Validate và normalize sortBy field
     */
    public String getValidatedSortBy() {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "createdAt";
        }

        return switch (sortBy.toLowerCase()) {
            case "name" -> "name";
            case "sellingprice", "selling_price" -> "sellingPrice";
            case "importprice", "import_price" -> "importPrice";
            case "stockquantity", "stock_quantity" -> "stockQuantity";
            case "createdat", "created_at" -> "createdAt";
            case "updatedat", "updated_at" -> "updatedAt";
            default -> "createdAt";
        };
    }

    /**
     * Validate và normalize sortDirection field
     */
    public String getValidatedSortDirection() {
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            return "desc";
        }

        return switch (sortDirection.toLowerCase()) {
            case "asc", "ascending" -> "asc";
            case "desc", "descending" -> "desc";
            default -> "desc";
        };
    }
}
