package com.group7.ecommerce.dto.response;

import java.math.BigDecimal;

public record ProductListItemResponse(
	Long id,
	String name,
	String description,
	BigDecimal sellingPrice,
	String imageUrl,
	String categoryName,
	Integer stockQuantity
) {}


