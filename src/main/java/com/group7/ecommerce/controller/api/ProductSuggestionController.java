package com.group7.ecommerce.controller.api;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.ecommerce.dto.request.ProductSuggestionDto;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.repository.UserRepository;
import com.group7.ecommerce.service.ProductSuggestionService;
import com.group7.ecommerce.utils.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
public class ProductSuggestionController {

	private final ProductSuggestionService suggestionService;
	private final UserRepository userRepository;
	private final MessageSource messageSource;

	@PostMapping
	public ResponseEntity<String> submitSuggestion(
			@Valid @RequestBody ProductSuggestionDto request,
			@AuthenticationPrincipal CustomUserDetails currentUserDetails,
			Locale locale) {
		if (currentUserDetails == null) {
			String message = messageSource.getMessage("suggestion.unauthorized", null, locale);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
		}

		User currentUser = userRepository.findById(currentUserDetails.getId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		suggestionService.createSuggestion(request, currentUser);

		String successMessage = messageSource.getMessage("suggestion.success", null, locale);
		return ResponseEntity.status(HttpStatus.CREATED).body(successMessage);
	}
}
