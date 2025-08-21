package com.group7.ecommerce.controller.api;

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

	@PostMapping
	public ResponseEntity<String> submitSuggestion(
			@Valid @RequestBody ProductSuggestionDto request,
			@AuthenticationPrincipal CustomUserDetails currentUserDetails) {

		if (currentUserDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn cần đăng nhập để thực hiện chức năng này.");
		}

		User currentUser = userRepository.findById(currentUserDetails.getId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		suggestionService.createSuggestion(request, currentUser);
		return ResponseEntity.status(HttpStatus.CREATED).body("Gửi đề xuất sản phẩm thành công. Vui lòng chờ admin xét duyệt.");
	}
}
