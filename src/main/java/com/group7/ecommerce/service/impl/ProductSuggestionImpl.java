package com.group7.ecommerce.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.group7.ecommerce.dto.request.ProductSuggestionDto;
import com.group7.ecommerce.dto.request.SuggestionReviewDto;
import com.group7.ecommerce.dto.response.ProductSuggestionResp;
import com.group7.ecommerce.entity.ProductSuggestion;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.enums.ProductSuggestionStatus;
import com.group7.ecommerce.exception.ResourceNotFoundException;
import com.group7.ecommerce.repository.ProductSuggestionRepository;
import com.group7.ecommerce.service.ProductSuggestionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSuggestionImpl implements ProductSuggestionService {

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

	@Override
	public Page<ProductSuggestionResp> getAllSuggestions(Pageable pageable) {
		return suggestionRepository.findAll(pageable).map(this::convertToDto);
	}

	@Override
	public ProductSuggestionResp getSuggestionById(Integer id) {
		return suggestionRepository.findById(id)
				.map(this::convertToDto)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề xuất với ID: " + id));
	}

	@Override
	public void reviewSuggestion(Integer id, SuggestionReviewDto request) {
		ProductSuggestion suggestion = suggestionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề xuất với ID: " + id));

		if (request.getStatus() == ProductSuggestionStatus.REJECTED &&
				(request.getRejectionReason() == null || request.getRejectionReason().isBlank())) {
			throw new IllegalArgumentException("Cần có lý do khi từ chối đề xuất.");
		}

		suggestion.setStatus(request.getStatus());
		suggestion.setRejectionReason(
				request.getStatus() == ProductSuggestionStatus.REJECTED ? request.getRejectionReason() : null
				);

		suggestionRepository.save(suggestion);
	}

	private ProductSuggestionResp convertToDto(ProductSuggestion suggestion) {
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
