package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductSuggestionDto {

	@NotBlank(message = "{suggestion.productName.notBlank}")
	@Size(max = 255, message = "{suggestion.productName.size}")
	private String productName;

	@Size(max = 1000, message = "{suggestion.description.size}")
	private String description;

	@Size(max = 100, message = "{suggestion.category.size}")
	private String suggestedCategory;
}
