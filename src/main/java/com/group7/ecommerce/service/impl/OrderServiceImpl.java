package com.group7.ecommerce.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.group7.ecommerce.dto.request.CreateOrderRequest;
import com.group7.ecommerce.dto.request.OrderRequestItem;
import com.group7.ecommerce.dto.request.UpdatePaymentMethodRequest;
import com.group7.ecommerce.entity.*;
import com.group7.ecommerce.enums.OrderStatus;
import com.group7.ecommerce.repository.*;
import com.group7.ecommerce.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.group7.ecommerce.dto.request.UpdateOrderStatusDto;
import com.group7.ecommerce.dto.response.OrderDetailResp;
import com.group7.ecommerce.dto.response.OrderItemResp;
import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.exception.ResourceNotFoundException;
import com.group7.ecommerce.repository.OrderRepository;
import com.group7.ecommerce.repository.ReasonRepository;
import com.group7.ecommerce.service.OrderService;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

	private final ReasonRepository reasonRepository;

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductRepository productRepository;
	private final ShipInfoRepository shipInfoRepository;
	private final UserRepository userRepository;
	private final MessageSource messageSource;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;

	@Override
	public OrderDetailResp getOrderDetailById(Integer orderId) {
		Order order = orderRepository.findDetailsById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		return mapOrderToDetailDTO(order);
	}

	@Override
	public Page<OrderSummaryResp> findOrderSummaries(String customerName, OrderStatus status, Pageable pageable) {

		Specification<Order> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

		if (StringUtils.hasText(customerName)) {
			spec = spec.and(customerNameContains(customerName));
		}

		if (status != null) {
			spec = spec.and(hasStatus(status));
		}

		Page<Order> ordersPage = orderRepository.findAll(spec, pageable);

		return ordersPage.map(this::mapOrderToSummaryDTO);
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

	@Transactional(readOnly = false)
	@Override
	public Order createOrder(Long userId,
							 int shipInfoId,
							 String paymentMethod,
							 List<OrderRequestItem> items) {
		// 1. Validate User
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException(getMessage("error.user.not.found")));

		// 2. Validate ShipInfo
		ShipInfo shipInfo = shipInfoRepository.findById(shipInfoId)
				.orElseThrow(() -> new RuntimeException(getMessage("error.shipping.info.invalid")));
		if (shipInfo.getUser().getId() != userId) {
			throw new RuntimeException(getMessage("error.shipping.info.not.belong"));
		}

		// 3. Tính tổng tiền + check tồn kho
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (OrderRequestItem item : items) {
			Product product = productRepository.findById(item.getProductId())
					.orElseThrow(() -> new RuntimeException(
							getMessage("error.product.not.found", item.getProductId().toString())));

			if (product.isDeleted() || product.getStockQuantity() < item.getQuantity()) {
				throw new RuntimeException(
						getMessage("error.product.unavailable.or.not.enough.stock", product.getName()));
			}

			BigDecimal subtotal = product.getSellingPrice()
					.multiply(BigDecimal.valueOf(item.getQuantity()));
			totalAmount = totalAmount.add(subtotal);
		}

		// 4. Tạo Order
		Order order = new Order();
		order.setUser(user);
		order.setShipInfo(shipInfo);
		order.setPaymentMethod(paymentMethod);
		order.setStatus(OrderStatus.PENDING); // mặc định "Chờ xác nhận"
		order.setTotalAmount(totalAmount);
		order.setCreatedAt(LocalDateTime.now());
		order.setUpdatedAt(LocalDateTime.now());
		order = orderRepository.save(order);

		// 5. Tạo Order_Items và cập nhật stock

		List<OrderItem> savedItems = new ArrayList<>();
		for (OrderRequestItem item : items) {
			Product product = productRepository.findById(item.getProductId())
					.orElseThrow(() -> new RuntimeException(
							getMessage("error.product.not.found", item.getProductId().toString())));

			// Kiểm tra lại stock trước khi trừ (double check)
			if (product.getStockQuantity() < item.getQuantity()) {
				throw new RuntimeException(
						getMessage("error.product.not.enough.stock", product.getName()));
			}

			// Trừ stock
			product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
			productRepository.save(product);

			// Lưu OrderItem
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setQuantity(item.getQuantity());
			orderItem.setPrice(product.getSellingPrice());
			orderItem.setCreatedAt(LocalDateTime.now());

			orderItemRepository.save(orderItem);
			savedItems.add(orderItemRepository.save(orderItem));
		}

		order.setOrderItems(savedItems);
		return order;
	}


	/**
	 * Helper method to get localized message
	 */
	private String getMessage(String key, Object... args) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(key, args, key, locale);
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
				order.getShipInfo().getReceiver(),
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

	@Transactional(readOnly = false)
	@Override
	public OrderDetailResp createDirectOrder(Authentication authentication, CreateOrderRequest request) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		// Kiểm tra danh sách sản phẩm
		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new RuntimeException(getMessage("error.product.list.required"));
		}

		// Chuyển đổi OrderItemRequest thành OrderRequestItem
		List<OrderRequestItem> orderItems = request.getItems().stream()
				.map(item -> {
					OrderRequestItem requestItem = new OrderRequestItem();
					requestItem.setProductId(item.getProductId());
					requestItem.setQuantity(item.getQuantity());
					return requestItem;
				})
				.collect(Collectors.toList());

		// Tạo đơn hàng
		Order order = createOrder(userDetails.getId(), request.getShipInfoId(), request.getPaymentMethod(), orderItems);

		// Thêm notes nếu có
		if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
			order.setReasonDetailed(request.getNotes());
			orderRepository.save(order);
		}

		log.info("Direct order created successfully for user {} with order ID {}", userDetails.getId(), order.getId());

		return mapOrderToDetailDTO(order);
	}

	@Transactional(readOnly = false)
	@Override
	public OrderDetailResp createOrderFromCart(Authentication authentication, CreateOrderRequest request) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		// Tìm giỏ hàng của user
		Cart cart = cartRepository.findByUserId(userDetails.getId())
				.orElseThrow(() -> new RuntimeException(getMessage("error.cart.not.found")));

		// Lấy danh sách sản phẩm trong giỏ hàng
		List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
		if (cartItems.isEmpty()) {
			throw new RuntimeException(getMessage("error.cart.empty"));
		}

		// Chuyển đổi CartItem thành OrderRequestItem
		List<OrderRequestItem> orderItems = cartItems.stream()
				.map(cartItem -> {
					OrderRequestItem item = new OrderRequestItem();
					item.setProductId(cartItem.getProduct().getId());
					item.setQuantity(cartItem.getQuantity());
					return item;
				})
				.collect(Collectors.toList());

		// Tạo đơn hàng
		Order order = createOrder(userDetails.getId(), request.getShipInfoId(), request.getPaymentMethod(), orderItems);

		// Thêm notes nếu có
		if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
			order.setReasonDetailed(request.getNotes());
			orderRepository.save(order);
		}

		// Xóa giỏ hàng sau khi tạo đơn thành công
		cartItemRepository.deleteAll(cartItems);

		log.info("Order created from cart successfully for user {} with order ID {}", userDetails.getId(), order.getId());

		return mapOrderToDetailDTO(order);
	}

	@Override
	public List<String> getAvailablePaymentMethods() {
		return Arrays.asList("COD", "BANK_TRANSFER", "VNPAY", "MOMO");
	}

	@Override
	public OrderDetailResp getOrderDetail(Authentication authentication, int orderId) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		// Kiểm tra quyền sở hữu
		if (order.getUser().getId()!= userDetails.getId()) {
			throw new RuntimeException(getMessage("error.order.access.denied"));
		}

		return mapOrderToDetailDTO(order);
	}

	@Override
	public List<OrderSummaryResp> getUserOrders(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userDetails.getId());

		return orders.stream()
				.map(this::mapOrderToSummaryDTO)
				.collect(Collectors.toList());
	}

}
