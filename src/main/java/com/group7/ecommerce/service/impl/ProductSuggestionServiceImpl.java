package com.group7.ecommerce.service.impl;


import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group7.ecommerce.dto.request.ProductSuggestionDto;
import com.group7.ecommerce.dto.response.ProductSuggestionResp;
import com.group7.ecommerce.entity.ProductSuggestion;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.exception.ResourceNotFoundException;
import com.group7.ecommerce.repository.ProductSuggestionRepository;
import com.group7.ecommerce.service.ProductSuggestionService;
import com.group7.ecommerce.utils.ProductSuggestionConverter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductSuggestionServiceImpl implements ProductSuggestionService {

	private final ProductSuggestionRepository suggestionRepository;
	private final ProductSuggestionConverter suggestionConverter;
	private final MessageSource messageSource;

	@Override
	public void createSuggestion(ProductSuggestionDto request, User currentUser) {
		ProductSuggestion suggestion = new ProductSuggestion();
		suggestion.setProductName(request.getProductName());
		suggestion.setDescription(request.getDescription());
		suggestion.setSuggestedCategory(request.getSuggestedCategory());
		suggestion.setUser(currentUser);

		suggestionRepository.save(suggestion);

	}

	@Override
	public Page<ProductSuggestionResp> getAllSuggestions(Pageable pageable) {
		return suggestionRepository.findAll(pageable).map(suggestionConverter::toDto);
	}

	@Override
	public ProductSuggestionResp getSuggestionById(Integer id) {
		return suggestionRepository.findById(id)
				.map(suggestionConverter::toDto)
				.orElseThrow(() -> {
					String message = messageSource.getMessage("exception.suggestion.notFound", new Object[]{id}, LocaleContextHolder.getLocale());
					return new ResourceNotFoundException(message);
				});
	}
}
