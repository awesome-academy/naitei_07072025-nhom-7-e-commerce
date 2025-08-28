package com.group7.ecommerce.controller.api;

import com.group7.ecommerce.dto.request.cart.AddToCartRequest;
import com.group7.ecommerce.dto.request.cart.DeleteCartItemRequest;
import com.group7.ecommerce.dto.request.cart.DeleteMultipleCartItemsRequest;
import com.group7.ecommerce.dto.request.cart.UpdateCartRequest;
import com.group7.ecommerce.dto.response.cart.AddToCartResponse;
import com.group7.ecommerce.dto.response.cart.DeleteCartItemResponse;
import com.group7.ecommerce.dto.response.cart.UpdateCartResponse;
import com.group7.ecommerce.dto.response.cart.ViewCartResponse;
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

    @GetMapping
    public ResponseEntity<ViewCartResponse> viewCart(Authentication authentication) {
        log.info("User {} requesting to view cart", authentication.getName());

        ViewCartResponse response = cartService.viewCart(authentication);

        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    // ==================== SOFT DELETE ENDPOINTS ====================

    /**
     * Xóa mềm một sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/soft/{cartItemId}")
    public ResponseEntity<DeleteCartItemResponse> softDeleteCartItem(
            Authentication authentication,
            @PathVariable Integer cartItemId) {

        log.info("User {} requesting to soft delete cart item {}", authentication.getName(), cartItemId);

        DeleteCartItemRequest request = new DeleteCartItemRequest(cartItemId);
        DeleteCartItemResponse response = cartService.softDeleteCartItem(authentication, request);

        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Xóa mềm nhiều sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/soft/multiple")
    public ResponseEntity<DeleteCartItemResponse> softDeleteMultipleCartItems(
            Authentication authentication,
            @Valid @RequestBody DeleteMultipleCartItemsRequest request) {

        log.info("User {} requesting to soft delete {} cart items",
                authentication.getName(), request.cartItemIds().size());

        DeleteCartItemResponse response = cartService.softDeleteMultipleCartItems(authentication, request);

        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== HARD DELETE ENDPOINTS ====================

    /**
     * Xóa cứng một sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/hard/{cartItemId}")
    public ResponseEntity<DeleteCartItemResponse> hardDeleteCartItem(
            Authentication authentication,
            @PathVariable Integer cartItemId) {

        log.info("User {} requesting to hard delete cart item {}", authentication.getName(), cartItemId);

        DeleteCartItemRequest request = new DeleteCartItemRequest(cartItemId);
        DeleteCartItemResponse response = cartService.hardDeleteCartItem(authentication, request);

        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Xóa cứng nhiều sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/hard/multiple")
    public ResponseEntity<DeleteCartItemResponse> hardDeleteMultipleCartItems(
            Authentication authentication,
            @Valid @RequestBody DeleteMultipleCartItemsRequest request) {

        log.info("User {} requesting to hard delete {} cart items",
                authentication.getName(), request.cartItemIds().size());

        DeleteCartItemResponse response = cartService.hardDeleteMultipleCartItems(authentication, request);

        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
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
