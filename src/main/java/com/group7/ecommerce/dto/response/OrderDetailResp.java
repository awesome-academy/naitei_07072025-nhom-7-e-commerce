package com.group7.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.group7.ecommerce.enums.OrderStatus;

public record OrderDetailResp(

		int id,
		LocalDateTime orderDate,
		OrderStatus status,
		String paymentMethod,
		BigDecimal totalAmount,

		String customerName,
		String customerEmail,
		String customerPhone,
		String shippingAddress,
		String shippingReceiver,

		String cancelReason,
		String adminNote,

		List<OrderItemResp> items
		) {}
