package com.group7.ecommerce.service;

import java.util.List;

import com.group7.ecommerce.dto.response.OrderSummaryResp;

public interface OrderService {
	List<OrderSummaryResp> getAllOrderSummaries();
}
