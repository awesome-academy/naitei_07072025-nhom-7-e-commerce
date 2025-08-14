package com.group7.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.group7.ecommerce.enums.OrderStatus;

public record OrderSummaryResp(
		int id,
		String customerName,
		OrderStatus status,
		String paymentMethod,
		LocalDateTime orderDate,
		BigDecimal totalAmount
		) {}
