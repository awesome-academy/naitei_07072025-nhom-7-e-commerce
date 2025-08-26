package com.group7.ecommerce.controller.api;

import com.group7.ecommerce.dto.request.cart.AddToCartRequest;
import com.group7.ecommerce.dto.request.cart.UpdateCartRequest;
import com.group7.ecommerce.dto.response.cart.AddToCartResponse;
import com.group7.ecommerce.dto.response.cart.UpdateCartResponse;
import com.group7.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private MessageSource messageSource;

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
                    .message(getMessage("openapi.response.500"))
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<UpdateCartResponse> updateCartItem(
            @RequestBody @Valid UpdateCartRequest request,
            Authentication authentication) {

        try {

            UpdateCartResponse response = cartService.updateCartItem(authentication, request);

            if (!response.success()) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in updateCartItem controller: {}", e.getMessage());
            UpdateCartResponse errorResponse = UpdateCartResponse.builder()
                    .success(false)
                    .message(getMessage("openapi.response.500"))
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Helper method để lấy localized message
     */
    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }
}
