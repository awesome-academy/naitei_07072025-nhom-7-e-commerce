package com.group7.ecommerce.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group7.ecommerce.dto.request.CategoryDto;
import com.group7.ecommerce.entity.Category;
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

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Long id, Model model) {
		Category category = categoryService.findById(id);

		CategoryDto dto = new CategoryDto();
		dto.setId(category.getId());
		dto.setName(category.getName());
		dto.setDescription(category.getDescription());
		if (category.getParent() != null) {
			dto.setParentId(category.getParent().getId());
		}

		model.addAttribute("categoryDto", dto);
		model.addAttribute("allCategories", categoryService.findAll());
		return "admin/categories/form";
	}

	@PostMapping("/update/{id}")
	public String updateCategory(@PathVariable Long id,
			@Valid @ModelAttribute("categoryDto") CategoryDto categoryDto,
			BindingResult result,
			RedirectAttributes redirectAttributes,
			Model model) {
		if (result.hasErrors()) {
			model.addAttribute("allCategories", categoryService.findAll());
			return "admin/categories/form";
		}

		categoryService.update(id, categoryDto);
		redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
		return "redirect:/admin/categories";
	}

	@PostMapping("/delete/{id}")
	public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			categoryService.deleteById(id);
			redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa danh mục này. Lỗi: " + e.getMessage());
		}
		return "redirect:/admin/categories";
	}
}
