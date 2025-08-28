package com.group7.ecommerce.utils.validator;

import com.group7.ecommerce.dto.request.cart.AddToCartRequest;
import com.group7.ecommerce.dto.request.cart.UpdateCartRequest;
import com.group7.ecommerce.entity.CartItem;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.repository.CartItemRepository;
import com.group7.ecommerce.utils.helper.ProductHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CartValidator {

    private final CartItemRepository cartItemRepository;
    private final MessageSource messageSource;
    private final ProductHelper productHelper;

    /**
     * Validate add to cart request
     */
    public ValidationResult validateAddToCart(AddToCartRequest request) {
        // Validate quantity
        if (request.quantity() <= 0) {
            return ValidationResult.error(getMessage("error.invalid.quantity"));
        }

        // Validate product exists and is available
        Product product = productHelper.findByIdAndIsDeletedFalseOrThrow(request.productId());

        // Check stock availability
        if (product.getStockQuantity() < request.quantity()) {
            return ValidationResult.error(getMessage("error.product.insufficient.stock", product.getStockQuantity()));
        }

        return ValidationResult.success();
    }

    /**
     * Validate update cart request
     */
    public ValidationResult validateUpdateCart(UpdateCartRequest request, User user) {
        // Validate cartItemId
        if (request.cartItemId() == null) {
            return ValidationResult.error(getMessage("error.cart.item.id.required"));
        }

        // Validate quantity
        if (request.quantity() <= 0) {
            return ValidationResult.error(getMessage("error.invalid.quantity"));
        }

        // Find cart item
        Optional<CartItem> cartItemOpt = cartItemRepository.findByIdAndCart_User(request.cartItemId(), user);
        if (cartItemOpt.isEmpty()) {
            return ValidationResult.error(getMessage("error.cart.item.not.found"));
        }

        CartItem cartItem = cartItemOpt.get();
        Product product = cartItem.getProduct();

        // Check if product is still available
        if (product.isDeleted()) {
            return ValidationResult.error(getMessage("error.product.not.available"));
        }

        return ValidationResult.success();
    }

    /**
     * Helper method to get localized message
     */
    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }

    /**
     * Validation result wrapper
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
