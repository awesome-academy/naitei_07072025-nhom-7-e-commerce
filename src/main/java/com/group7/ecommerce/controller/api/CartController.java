package com.group7.ecommerce.controller.api;

import com.group7.ecommerce.dto.request.cart.AddToCartRequest;
import com.group7.ecommerce.dto.response.cart.AddToCartResponse;
import com.group7.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<AddToCartResponse> addToCart(
            @RequestBody @Valid AddToCartRequest request,
            Authentication authentication) {

        try {

            AddToCartResponse response = cartService.addToCart(authentication, request);

            if (!response.success()) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in addToCart controller: {}", e.getMessage());
            AddToCartResponse errorResponse = AddToCartResponse.builder()
                    .success(false)
                    .message("Internal server error")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
