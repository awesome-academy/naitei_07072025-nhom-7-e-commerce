package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductDto(
        @NotBlank(message = "{product.name.notblank}")
        String name,

        String description,

        @NotNull(message = "{product.importPrice.notnull}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{product.importPrice.min}")
        BigDecimal importPrice,

        @NotNull(message = "{product.sellingPrice.notnull}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{product.sellingPrice.min}")
        BigDecimal sellingPrice,

        @Min(value = 0, message = "{product.stockQuantity.min}")
        int stockQuantity,

        @NotNull(message = "{product.categoryId.notnull}")
        Long categoryId,

        @NotEmpty(message = "{product.imageUrls.notempty}")
        List<String> imageUrls,

        boolean isFeatured,
        boolean isDeleted
) { }

