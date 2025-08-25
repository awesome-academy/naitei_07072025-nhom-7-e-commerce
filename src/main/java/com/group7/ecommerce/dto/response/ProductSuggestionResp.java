package com.group7.ecommerce.dto.response;

import java.time.LocalDateTime;

import com.group7.ecommerce.enums.ProductSuggestionStatus;

public record ProductSuggestionResp(
		Integer id,
		String productName,
		String description,
		String suggestedCategory,
		ProductSuggestionStatus status,
		String rejectionReason,
		LocalDateTime createdAt,
		String submittedByUsername
		) {}
