package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.dto.request.cart.AddToCartRequest;
import com.group7.ecommerce.dto.request.cart.UpdateCartRequest;
import com.group7.ecommerce.dto.response.cart.AddToCartResponse;
import com.group7.ecommerce.dto.response.cart.UpdateCartResponse;
import com.group7.ecommerce.entity.Cart;
import com.group7.ecommerce.entity.CartItem;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.repository.CartItemRepository;
import com.group7.ecommerce.repository.CartRepository;
import com.group7.ecommerce.service.CartService;
import com.group7.ecommerce.utils.helper.ProductHelper;
import com.group7.ecommerce.utils.helper.UserHelper;
import com.group7.ecommerce.utils.validator.CartValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserHelper userHelper;
    private final MessageSource messageSource;
    private final CartValidator cartValidator;
    private final ProductHelper productHelper;

    @Override
    @Transactional(readOnly = false)
    public AddToCartResponse addToCart(Authentication authentication, AddToCartRequest request) {

        Integer userId = userHelper.getCurrentUserId(authentication);
        // Validate user exists and is active
        User user = userHelper.findUserByIdOrThrow(userId.longValue());
        try {

            // Validate request using validator
            CartValidator.ValidationResult validationResult = cartValidator.validateAddToCart(request);
            if (!validationResult.isValid()) {
                return AddToCartResponse.builder()
                        .success(false)
                        .message(validationResult.getErrorMessage())
                        .build();
            }

            // Get product (already validated in validator)
            Product product = productHelper.findByIdAndIsDeletedFalseOrThrow(request.productId());

            // Get or create cart for user
            Cart cart = getOrCreateCart(user);

            // Check if product already exists in cart
            Optional<CartItem> existingCartItem = cartItemRepository
                    .findByCartAndProduct(cart, product);

            CartItem cartItem;
            if (existingCartItem.isPresent()) {
                // Product already exists in cart - update quantity
                cartItem = existingCartItem.get();
                int newQuantity = cartItem.getQuantity() + request.quantity();
                if(newQuantity > product.getStockQuantity()) {
                    return AddToCartResponse.builder()
                            .success(false)
                            .message(getMessage("error.product.insufficient.stock", product.getStockQuantity()))
                            .build();
                }
                // Update existing cart item quantity
                cartItem.setQuantity(newQuantity);

                log.info("Updated quantity for product {} in cart for user {}. New quantity: {}",
                        request.productId(), userId, newQuantity);
            } else {
                // Create new cart item
                cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setQuantity(request.quantity());

                log.info("Added new product {} to cart for user {}", request.productId(), userId);

            }
            CartItem savedCartItem = cartItemRepository.save(cartItem);

            // Get total items in cart
            long totalItems = cartItemRepository.countByCart(cart);

            log.info("Product {} added to cart for user {}", request.productId(), userId);

            return AddToCartResponse.builder()
                    .success(true)
                    .message(getMessage("product.import.success"))
                    .cartItemId(savedCartItem.getId())
                    .totalItemsInCart((int) totalItems)
                    .build();

        } catch (Exception e) {
            log.error("Error adding product to cart for user {}: {}", userId, e.getMessage());
            return AddToCartResponse.builder()
                    .success(false)
                    .message(getMessage("error.product.add.carts",e.getMessage()))
                    .build();
        }
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Helper method để lấy localized message
     */
    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }

    @Override
    @Transactional(readOnly = false)
    public UpdateCartResponse updateCartItem(Authentication authentication, UpdateCartRequest request) {

        Integer userId = userHelper.getCurrentUserId(authentication);
        // Validate user exists and is active
        User user = userHelper.findUserByIdOrThrow(userId.longValue());

        try {
            // Validate request using validator
            CartValidator.ValidationResult validationResult = cartValidator.validateUpdateCart(request, user);
            if (!validationResult.isValid()) {
                return UpdateCartResponse.builder()
                        .success(false)
                        .message(validationResult.getErrorMessage())
                        .build();
            }

            // Tìm cart item theo ID và user
            CartItem cartItem = cartItemRepository.findByIdAndCart_User(request.cartItemId(), user)
                    .orElseThrow();

            // Cập nhật quantity
            cartItem.setQuantity(request.quantity());
            CartItem savedCartItem = cartItemRepository.save(cartItem);

            // Get total items in cart
            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() ->
                            new RuntimeException(getMessage("error.cart.not.found", user.getId())));
            long totalItems = cartItemRepository.countByCart(cart);

            log.info("Updated cart item {} for user {}. New quantity: {}",
                    cartItem.getId(), userId, request.quantity());

            return UpdateCartResponse.builder()
                    .success(true)
                    .message(getMessage("cart.update.success"))
                    .cartItemId(savedCartItem.getId())
                    .totalItemsInCart((int) totalItems)
                    .build();

        } catch (Exception e) {
            log.error("Error updating cart item for user {}: {}", userId, e.getMessage());
            return UpdateCartResponse.builder()
                    .success(false)
                    .message(getMessage("error.cart.update.failed", e.getMessage()))
                    .build();
        }
    }
}
