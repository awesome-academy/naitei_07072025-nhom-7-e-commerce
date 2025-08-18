package com.group7.ecommerce.dto.response;

import java.math.BigDecimal;


public record OrderItemResp(
		String productName,
		int quantity,
		BigDecimal price
		) {}
