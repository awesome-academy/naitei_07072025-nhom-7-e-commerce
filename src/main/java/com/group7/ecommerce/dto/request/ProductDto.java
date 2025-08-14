package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá nhập không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá nhập phải > 0")
    private BigDecimal importPrice;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải > 0")
    private BigDecimal sellingPrice;

    @Min(value = 0, message = "Số lượng phải >= 0")
    private int stockQuantity;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @NotEmpty(message = "Phải có ít nhất 1 ảnh")
    private List<String> imageUrls;

    private boolean isFeatured;
    private boolean isDeleted;
}
