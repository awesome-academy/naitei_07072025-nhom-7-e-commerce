package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDto {
	private Long id;

	@NotBlank(message = "Tên danh mục không được để trống")
	private String name;

	private String description;

	private Long parentId;
}
