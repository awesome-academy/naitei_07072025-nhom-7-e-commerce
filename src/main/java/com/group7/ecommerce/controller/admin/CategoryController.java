package com.group7.ecommerce.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group7.ecommerce.dto.request.CategoryDto;
import com.group7.ecommerce.service.CategoryService;

import jakarta.validation.Valid;
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

	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("categoryDto", new CategoryDto());
		model.addAttribute("allCategories", categoryService.findAll());
		return "admin/categories/form";
	}

	@PostMapping("/save")
	public String saveCategory(@Valid @ModelAttribute("categoryDto") CategoryDto categoryDto,
			BindingResult result,
			RedirectAttributes redirectAttributes,
			Model model) {
		if (result.hasErrors()) {
			model.addAttribute("allCategories", categoryService.findAll());
			return "admin/categories/form";
		}

		categoryService.save(categoryDto);

		redirectAttributes.addFlashAttribute("successMessage", "Tạo danh mục thành công!");
		return "redirect:/admin/categories";
	}
}
