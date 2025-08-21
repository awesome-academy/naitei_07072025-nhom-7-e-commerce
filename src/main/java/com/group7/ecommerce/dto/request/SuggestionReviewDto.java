package com.group7.ecommerce.dto.request;

import com.group7.ecommerce.enums.ProductSuggestionStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SuggestionReviewDto {

	@NotNull(message = "Trạng thái không được để trống")
	private ProductSuggestionStatus status;

	private String rejectionReason;
}
