package com.group7.ecommerce.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.group7.ecommerce.dto.request.UpdateOrderStatusDto;
import com.group7.ecommerce.dto.response.OrderDetailResp;
import com.group7.ecommerce.dto.response.OrderItemResp;
import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.entity.Order;
import com.group7.ecommerce.entity.OrderItem;
import com.group7.ecommerce.entity.Reason;
import com.group7.ecommerce.enums.OrderStatus;
import com.group7.ecommerce.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.group7.ecommerce.repository.OrderRepository;
import com.group7.ecommerce.repository.ReasonRepository;
import com.group7.ecommerce.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	private final ReasonRepository reasonRepository;

	private final OrderRepository orderRepository;

	public OrderServiceImpl(OrderRepository orderRepository, ReasonRepository reasonRepository) {
		this.orderRepository = orderRepository;
		this.reasonRepository = reasonRepository;
	}

	@Override
	public OrderDetailResp getOrderDetailById(Integer orderId) {
		Order order = orderRepository.findDetailsById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		return mapOrderToDetailDTO(order);
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

	@Override
	public OrderDetailResp updateOrderStatus(Integer orderId, UpdateOrderStatusDto request) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		OrderStatus newStatus = request.getNewStatus();
		order.setStatus(newStatus);

		if (newStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.REJECTED) {
			// Kiểm tra lý do
			if (request.getReasonId() != null) {
				Reason reason = reasonRepository.findById(request.getReasonId())
						.orElseThrow(() -> new ResourceNotFoundException("Reason not found with id: " + request.getReasonId()));
				order.setReason(reason);
			} else {
				order.setReason(null);
			}
			order.setReasonDetailed(request.getAdminNote());
		} else {
			order.setReason(null);
			order.setReasonDetailed(null);
		}

		Order updatedOrder = orderRepository.save(order);
		return mapOrderToDetailDTO(updatedOrder);
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

	private OrderDetailResp mapOrderToDetailDTO(Order order) {
		List<OrderItemResp> itemDTOs = order.getOrderItems().stream()
				.map(this::mapOrderItemToDTO)
				.collect(Collectors.toList());

		String reasonText = (order.getReason() != null) ? order.getReason().getDescription() : null;

		return new OrderDetailResp(
				order.getId(),
				order.getCreatedAt(),
				order.getStatus(),
				order.getPaymentMethod(),
				order.getTotalAmount(),
				order.getUser().getFullName(),
				order.getUser().getEmail(),
				order.getUser().getPhone(),
				order.getShipInfo().getAddress(),
				order.getShipInfo().getReciever(),
				reasonText,
				order.getReasonDetailed(),
				itemDTOs
				);
	}

	private OrderItemResp mapOrderItemToDTO(OrderItem orderItem) {
		return new OrderItemResp(
				orderItem.getProduct().getName(),
				orderItem.getQuantity(),
				orderItem.getPrice()
				);
	}
}
