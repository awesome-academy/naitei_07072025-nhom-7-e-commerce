package com.group7.ecommerce.service;

import com.group7.ecommerce.dto.request.ProductSuggestionDto;
import com.group7.ecommerce.entity.User;

public interface ProductSuggestionService {
	void createSuggestion(ProductSuggestionDto request, User currentUser);
}
