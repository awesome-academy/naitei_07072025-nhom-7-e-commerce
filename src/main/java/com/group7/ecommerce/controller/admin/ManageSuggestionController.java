package com.group7.ecommerce.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group7.ecommerce.dto.response.ProductSuggestionResp;
import com.group7.ecommerce.service.ProductSuggestionService;

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

		return "admin/suggestions/detail";
	}
}
