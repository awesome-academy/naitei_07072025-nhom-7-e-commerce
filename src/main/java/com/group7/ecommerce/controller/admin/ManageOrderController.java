package com.group7.ecommerce.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.group7.ecommerce.dto.request.UpdateOrderStatusDto;
import com.group7.ecommerce.dto.response.OrderDetailResp;
import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.enums.OrderStatus;
import com.group7.ecommerce.repository.ReasonRepository;
import com.group7.ecommerce.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class ManageOrderController {

	private final OrderService orderService;
	private final ReasonRepository reasonRepository;

	@GetMapping()
	public String index(
			@PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
			@RequestParam(name = "customerName", required = false) String customerName,
			@RequestParam(name = "status", required = false) OrderStatus status,
			Model model) {
		Page<OrderSummaryResp> ordersPage = orderService.findOrderSummaries(customerName, status, pageable);

		model.addAttribute("ordersPage", ordersPage);
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
