package com.group7.ecommerce.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.group7.ecommerce.dto.request.UpdateOrderStatusDto;
import com.group7.ecommerce.dto.response.OrderDetailResp;
import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.enums.OrderStatus;
import com.group7.ecommerce.repository.ReasonRepository;
import com.group7.ecommerce.service.OrderService;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/admin/orders")
public class ManageOrderController {

	private final OrderService orderService;
	private final ReasonRepository reasonRepository;

	public ManageOrderController(OrderService orderService, ReasonRepository reasonRepository) {
		this.orderService = orderService;
		this.reasonRepository = reasonRepository;
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

		model.addAttribute("allReasons", reasonRepository.findAll());
		return "admin/orders/index";
	}
	@GetMapping("/{id}")
	public String showOrderDetailPage(@PathVariable("id") Integer orderId, Model model) {
		OrderDetailResp order = orderService.getOrderDetailById(orderId);

		model.addAttribute("order", order);
		model.addAttribute("activePage", "orders");

		model.addAttribute("allReasons", reasonRepository.findAll());

		return "admin/orders/detail";
	}

	@PostMapping("/update-status/{id}")
	public String handleUpdateStatus(
			@PathVariable("id") Integer orderId,
			@ModelAttribute UpdateOrderStatusDto request) {

		orderService.updateOrderStatus(orderId, request);

		return "redirect:/admin/orders/" + orderId;
	}
}
