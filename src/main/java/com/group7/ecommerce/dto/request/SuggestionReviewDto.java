package com.group7.ecommerce.dto.request;

import com.group7.ecommerce.enums.ProductSuggestionStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SuggestionReviewDto {

	@NotNull(message = "{suggestion.status.notNull}")
	private ProductSuggestionStatus status;

	private String rejectionReason;
}
