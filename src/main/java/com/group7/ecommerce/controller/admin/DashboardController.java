package com.group7.ecommerce.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DashboardController {

	@GetMapping("/dashboard")
	public String showDashboard(Model model) {
		model.addAttribute("activePage", "dashboard");
		model.addAttribute("totalOrders", 150);
		model.addAttribute("totalProducts", 78);
		model.addAttribute("totalUsers", 124);
		return "admin/dashboard";
	}
}
