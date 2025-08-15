package com.group7.ecommerce.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.service.OrderService;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping
	public String index(Model model) {
		List<OrderSummaryResp> orders = orderService.getAllOrderSummaries();
		model.addAttribute("orders", orders);
		model.addAttribute("activePage", "orders");
		return "admin/orders/index";
	}
}
