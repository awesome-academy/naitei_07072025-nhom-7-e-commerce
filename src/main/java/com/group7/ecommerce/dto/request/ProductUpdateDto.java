package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductUpdateDto(
        @NotBlank(message = "Tên sản phẩm không được để trống")
        String name,

        String description,

        @NotNull(message = "Giá nhập không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá nhập phải > 0")
        BigDecimal importPrice,

        @NotNull(message = "Giá bán không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải > 0")
        BigDecimal sellingPrice,

        @NotNull(message = "Số lượng tồn không được để trống")
        @Min(value = 0, message = "Số lượng phải >= 0")
        Integer stockQuantity,

        @NotNull(message = "Danh mục không được để trống")
        Long categoryId,

        Boolean isFeatured,
        Boolean isDeleted
) { }
