package com.group7.ecommerce.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group7.ecommerce.dto.request.ProductSuggestionDto;
import com.group7.ecommerce.entity.ProductSuggestion;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.repository.ProductSuggestionRepository;
import com.group7.ecommerce.service.ProductSuggestionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductSuggestionServiceImpl implements ProductSuggestionService {

	private final ProductSuggestionRepository suggestionRepository;

	@Override
	public void createSuggestion(ProductSuggestionDto request, User currentUser) {
		ProductSuggestion suggestion = new ProductSuggestion();
		suggestion.setProductName(request.getProductName());
		suggestion.setDescription(request.getDescription());
		suggestion.setSuggestedCategory(request.getSuggestedCategory());
		suggestion.setUser(currentUser);

		suggestionRepository.save(suggestion);

	}
}
