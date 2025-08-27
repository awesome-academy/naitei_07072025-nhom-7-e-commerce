package com.group7.ecommerce.utils;

import org.springframework.stereotype.Component;

import com.group7.ecommerce.dto.response.ProductSuggestionResp;
import com.group7.ecommerce.entity.ProductSuggestion;

@Component
public class ProductSuggestionConverter {
	public ProductSuggestionResp toDto(ProductSuggestion suggestion) {
		if (suggestion == null) {
			return null;
		}

		return new ProductSuggestionResp(
				suggestion.getId(),
				suggestion.getProductName(),
				suggestion.getDescription(),
				suggestion.getSuggestedCategory(),
				suggestion.getStatus(),
				suggestion.getRejectionReason(),
				suggestion.getCreatedAt(),
				suggestion.getUser().getUsername()
				);
	}
}
