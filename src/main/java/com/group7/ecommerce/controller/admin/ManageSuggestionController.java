package com.group7.ecommerce.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group7.ecommerce.dto.request.SuggestionReviewDto;
import com.group7.ecommerce.dto.response.ProductSuggestionResp;
import com.group7.ecommerce.service.ProductSuggestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/suggestions")
@RequiredArgsConstructor
public class ManageSuggestionController {

	private final ProductSuggestionService suggestionService;

	@GetMapping
	public String listSuggestions(
			@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			Model model) {

		Page<ProductSuggestionResp> suggestionsPage = suggestionService.getAllSuggestions(pageable);
		model.addAttribute("suggestionsPage", suggestionsPage);
		return "admin/suggestions/index";
	}

	@GetMapping("/{id}")
	public String viewSuggestion(@PathVariable Integer id, Model model) {
		ProductSuggestionResp suggestion = suggestionService.getSuggestionById(id);
		model.addAttribute("suggestion", suggestion);

		model.addAttribute("reviewDto", new SuggestionReviewDto());

		return "admin/suggestions/detail";
	}

	@PostMapping("/review/{id}")
	public String reviewSuggestion(
			@PathVariable Integer id,
			@Valid @ModelAttribute("reviewDto") SuggestionReviewDto reviewDto,
			RedirectAttributes redirectAttributes) {

		suggestionService.reviewSuggestion(id, reviewDto);
		redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt gợi ý sản phẩm #" + id + " thành công!");

		return "redirect:/admin/suggestions";
	}
}
