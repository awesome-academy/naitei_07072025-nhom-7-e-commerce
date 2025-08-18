package com.group7.ecommerce.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.group7.ecommerce.dto.response.OrderDetailResp;
import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.enums.OrderStatus;
import com.group7.ecommerce.service.OrderService;

@Controller
@RequestMapping("/admin/orders")
public class ManageOrderController {

	private final OrderService orderService;

	public ManageOrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping()
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

	@GetMapping("/{id}")
	public String showOrderDetailPage(@PathVariable("id") Integer orderId, Model model) {
		OrderDetailResp order = orderService.getOrderDetailById(orderId);

		model.addAttribute("order", order);
		model.addAttribute("activePage", "orders");

		return "admin/orders/detail";
	}
}
