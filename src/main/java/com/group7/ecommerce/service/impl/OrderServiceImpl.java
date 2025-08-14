package com.group7.ecommerce.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.entity.Order;
import com.group7.ecommerce.enums.OrderStatus;
import com.group7.ecommerce.repository.OrderRepository;
import com.group7.ecommerce.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;

	public OrderServiceImpl(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public List<OrderSummaryResp> getAllOrderSummaries(String customerName, OrderStatus status) {

		Specification<Order> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

		if (StringUtils.hasText(customerName)) {
			spec = spec.and(customerNameContains(customerName));
		}

		if (status != null) {
			spec = spec.and(hasStatus(status));
		}

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		List<Order> orders = orderRepository.findAll(spec, sort);

		return orders.stream()
				.map(this::mapOrderToSummaryDTO)
				.collect(Collectors.toList());
	}

	private static Specification<Order> customerNameContains(String customerName) {
		return (root, query, criteriaBuilder) -> {
			return criteriaBuilder.like(root.get("user").get("fullName"), "%" + customerName + "%");
		};
	}

	private static Specification<Order> hasStatus(OrderStatus status) {
		return (root, query, criteriaBuilder) -> {
			return criteriaBuilder.equal(root.get("status"), status);
		};
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
