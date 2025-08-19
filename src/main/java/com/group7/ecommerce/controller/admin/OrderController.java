package com.group7.ecommerce.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.enums.OrderStatus;
import com.group7.ecommerce.service.OrderService;

@Controller
@RequestMapping("/admin")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping("/orders")
	public String index(
			@RequestParam(name = "customerName", required = false) String customerName,
			@RequestParam(name = "status", required = false) OrderStatus status,
			Model model) {
		List<OrderSummaryResp> orders = orderService.getAllOrderSummaries(customerName, status);

		model.addAttribute("orders", orders);
		model.addAttribute("activePage", "orders");

		model.addAttribute("currentCustomerName", customerName);
		model.addAttribute("currentStatus", status);

		model.addAttribute("allStatuses", OrderStatus.values());
		return "admin/orders/index";
	}
}
