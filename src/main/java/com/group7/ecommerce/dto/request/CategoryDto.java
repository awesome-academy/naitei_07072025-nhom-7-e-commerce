package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDto {
	private Long id;

	@NotBlank(message = "{category.name.notBlank}")
	private String name;

	private String description;

	private Long parentId;
}
