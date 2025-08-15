package com.group7.ecommerce.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.entity.Order;
import com.group7.ecommerce.repository.OrderRepository;
import com.group7.ecommerce.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;

	public OrderServiceImpl(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public List<OrderSummaryResp> getAllOrderSummaries() {
		List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

		return orders.stream()
				.map(this::mapOrderToSummaryDTO)
				.collect(Collectors.toList());
	}
	private OrderSummaryResp mapOrderToSummaryDTO(Order order) {
		return new OrderSummaryResp(
				order.getId(),
				order.getUser().getFullName(),
				order.getStatus(),
				order.getPaymentMethod(),
				order.getCreatedAt(),
				order.getTotalAmount()
				);
	}
}
