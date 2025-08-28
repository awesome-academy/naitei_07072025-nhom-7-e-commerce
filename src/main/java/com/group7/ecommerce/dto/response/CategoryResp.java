package com.group7.ecommerce.dto.response;

import java.util.List;

public record CategoryResp (
		Long id,
		String name,
		String description,
		List<CategoryResp> children
		) {}
