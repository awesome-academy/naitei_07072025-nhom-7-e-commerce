package com.group7.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.group7.ecommerce.dto.request.ProductSuggestionDto;
import com.group7.ecommerce.dto.request.SuggestionReviewDto;
import com.group7.ecommerce.dto.response.ProductSuggestionResp;
import com.group7.ecommerce.entity.User;

public interface ProductSuggestionService {
	void createSuggestion(ProductSuggestionDto request, User currentUser);

	Page<ProductSuggestionResp> getAllSuggestions(Pageable pageable);
	ProductSuggestionResp getSuggestionById(Integer id);
	void reviewSuggestion(Integer id, SuggestionReviewDto request);
}
