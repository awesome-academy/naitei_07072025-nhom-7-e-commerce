package com.group7.ecommerce.service;

import java.util.List;

import com.group7.ecommerce.dto.response.OrderDetailResp;
import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.enums.OrderStatus;

public interface OrderService {
	List<OrderSummaryResp> getAllOrderSummaries(String customerName, OrderStatus status);

	OrderDetailResp getOrderDetailById(Integer orderId);
}
