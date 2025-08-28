package com.group7.ecommerce.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group7.ecommerce.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	@GetMapping
	public String listCategories(Model model) {
		model.addAttribute("categoryTree", categoryService.findAllAsTree());
		return "admin/categories/index";
	}
}
