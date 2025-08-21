package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductSuggestionDto {

	@NotBlank(message = "Tên sản phẩm không được để trống")
	@Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự")
	private String productName;

	@Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
	private String description;

	@Size(max = 100, message = "Tên danh mục gợi ý không được vượt quá 100 ký tự")
	private String suggestedCategory;
}
