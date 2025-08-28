package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.dto.request.cart.AddToCartRequest;
import com.group7.ecommerce.dto.request.cart.UpdateCartRequest;
import com.group7.ecommerce.dto.response.cart.AddToCartResponse;
import com.group7.ecommerce.dto.response.cart.UpdateCartResponse;
import com.group7.ecommerce.entity.Cart;
import com.group7.ecommerce.entity.CartItem;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.dto.response.cart.CartItemResponse;
import com.group7.ecommerce.dto.request.cart.CartSummary;
import com.group7.ecommerce.dto.response.cart.ViewCartResponse;
import com.group7.ecommerce.dto.request.cart.*;
import com.group7.ecommerce.dto.response.cart.*;
import com.group7.ecommerce.entity.*;
import com.group7.ecommerce.repository.CartItemRepository;
import com.group7.ecommerce.repository.CartRepository;
import com.group7.ecommerce.repository.ProductRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final ProductRepository productRepository;

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
            if (cartItem == null) {
                return UpdateCartResponse.builder()
                        .success(false)
                        .message(getMessage("error.cart.item.not.found"))
                        .build();
            }

            // Kiểm tra product vẫn còn available
            Product product = cartItem.getProduct();
            if (product.isDeleted()) {
                return UpdateCartResponse.builder()
                        .success(false)
                        .message(getMessage("error.product.not.available"))
                        .build();
            }

            // Kiểm tra stock availability (stock hiện tại + quantity đang trong cart)
            Integer availableStock = product.getStockQuantity() + cartItem.getQuantity();

            if (availableStock < request.quantity()) {
                return UpdateCartResponse.builder()
                        .success(false)
                        .message(getMessage("error.product.insufficient.stock", availableStock))
                        .build();
            }

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

    @Override
    @Transactional(readOnly = true)
    public ViewCartResponse viewCart(Authentication authentication) {

        Integer userId = userHelper.getCurrentUserId(authentication);
        User user = userHelper.findUserByIdOrThrow(userId.longValue());

        try {

            // Lấy danh sách cart items với product và images
            List<CartItem> cartItems = cartItemRepository.findCartItemsWithProductAndImagesByUser(user);

            if (cartItems.isEmpty()) {
                return ViewCartResponse.builder()
                        .success(true)
                        .message(getMessage("cart.empty"))
                        .cartItems(List.of())
                        .summary(CartSummary.builder()
                                .totalItems(0)
                                .totalQuantity(0)
                                .totalAmount(BigDecimal.ZERO)
                                .estimatedAmount(BigDecimal.ZERO)
                                .build())
                        .build();
            }

            List<CartItemResponse> cartItemResponses = cartItems.stream()
                    .map(this::convertToCartItemResponse)
                    .collect(Collectors.toList());

            // Tính toán summary
            CartSummary summary = calculateCartSummary(cartItems);

            log.info("Retrieved {} cart items for user {}", cartItems.size(), userId);

            return ViewCartResponse.builder()
                    .success(true)
                    .message(getMessage("cart.view.success"))
                    .cartItems(cartItemResponses)
                    .summary(summary)
                    .build();

        } catch (Exception e) {
            log.error("Error viewing cart: {}", e.getMessage());
            return ViewCartResponse.builder()
                    .success(false)
                    .message(getMessage("error.cart.view.failed", e.getMessage()))
                    .build();
        }
    }

    private CartItemResponse convertToCartItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();

        // Lấy primary image URL
        String primaryImageUrl = product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(product.getImages().isEmpty() ? null :
                        product.getImages().get(0).getImageUrl());

        // Tính total price cho item này
        BigDecimal totalPrice = product.getSellingPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .primaryImageUrl(primaryImageUrl)
                .quantity(cartItem.getQuantity())
                .unitPrice(product.getSellingPrice())
                .totalPrice(totalPrice)
                .availableStock(product.getStockQuantity())
                .build();
    }

    private CartSummary calculateCartSummary(List<CartItem> cartItems) {
        Integer totalItems = cartItems.size();

        Integer totalQuantity = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getSellingPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Estimated amount có thể tính toán discount, tax, shipping fee, etc.
        // Hiện tại chỉ bằng total amount
        BigDecimal estimatedAmount = totalAmount;

        return CartSummary.builder()
                .totalItems(totalItems)
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .estimatedAmount(estimatedAmount)
                .build();
    }

    // ==================== SOFT DELETE METHODS ====================

    @Override
    public DeleteCartItemResponse softDeleteCartItem(Authentication authentication, DeleteCartItemRequest request) {
        try {
            Integer userId = userHelper.getCurrentUserId(authentication);
            User user = userHelper.findUserByIdOrThrow(userId.longValue());

            // Validation
            if (request.cartItemId() == null) {
                return createFailedDeleteResponse(getMessage("error.cart.item.id.required"));
            }

            // Tìm cart item
            Optional<CartItem> cartItemOpt = cartItemRepository.findActiveCartItemByIdAndUser(request.cartItemId(), user);
            if (cartItemOpt.isEmpty()) {
                return createFailedDeleteResponse(getMessage("error.cart.item.not.found"));
            }

            CartItem cartItem = cartItemOpt.get();

            // Restore stock quantity khi xóa cart item
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + cartItem.getQuantity());
            productRepository.save(product);

            // Soft delete cart item
            cartItem.softDelete();
            cartItemRepository.save(cartItem);

            // Get updated cart summary
            CartSummary summary = getCartSummaryForUser(user);

            log.info("Soft deleted cart item {} for user {}", request.cartItemId(), userId);

            return DeleteCartItemResponse.builder()
                    .success(true)
                    .message(getMessage("cart.item.delete.success"))
                    .deletedCartItemIds(List.of(request.cartItemId()))
                    .failedCartItemIds(List.of())
                    .totalItemsInCart(summary.totalItems())
                    .summary(summary)
                    .build();

        } catch (Exception e) {
            log.error("Error soft deleting cart item: {}", e.getMessage());
            return createFailedDeleteResponse(getMessage("error.cart.item.delete.failed", e.getMessage()));
        }
    }

    @Override
    public DeleteCartItemResponse softDeleteMultipleCartItems(Authentication authentication, DeleteMultipleCartItemsRequest request) {
        try {
            Integer userId = userHelper.getCurrentUserId(authentication);
            User user = userHelper.findUserByIdOrThrow(userId.longValue());

            if (request.cartItemIds() == null || request.cartItemIds().isEmpty()) {
                return createFailedDeleteResponse(getMessage("error.cart.item.ids.required"));
            }

            // Tìm các cart items hợp lệ
            List<CartItem> cartItems = cartItemRepository.findActiveCartItemsByIdsAndUser(request.cartItemIds(), user);
            List<Integer> foundIds = cartItems.stream().map(CartItem::getId).collect(Collectors.toList());
            List<Integer> failedIds = request.cartItemIds().stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            if (cartItems.isEmpty()) {
                return createFailedDeleteResponse(getMessage("error.cart.items.not.found"));
            }

            // Restore stock quantities
            for (CartItem cartItem : cartItems) {
                Product product = cartItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() + cartItem.getQuantity());
                productRepository.save(product);

                // Soft delete
                cartItem.softDelete();
            }

            cartItemRepository.saveAll(cartItems);

            // Get updated cart summary
            CartSummary summary = getCartSummaryForUser(user);

            log.info("Soft deleted {} cart items for user {}. Failed: {}", foundIds.size(), userId, failedIds.size());

            return DeleteCartItemResponse.builder()
                    .success(true)
                    .message(getMessage("cart.items.delete.success", foundIds.size()))
                    .deletedCartItemIds(foundIds)
                    .failedCartItemIds(failedIds)
                    .totalItemsInCart(summary.totalItems())
                    .summary(summary)
                    .build();

        } catch (Exception e) {
            log.error("Error soft deleting multiple cart items: {}", e.getMessage());
            return createFailedDeleteResponse(getMessage("error.cart.items.delete.failed", e.getMessage()));
        }
    }

// ==================== HARD DELETE METHODS ====================

    @Override
    public DeleteCartItemResponse hardDeleteCartItem(Authentication authentication, DeleteCartItemRequest request) {
        try {
            Integer userId = userHelper.getCurrentUserId(authentication);
            User user = userHelper.findUserByIdOrThrow(userId.longValue());

            if (request.cartItemId() == null) {
                return createFailedDeleteResponse(getMessage("error.cart.item.id.required"));
            }

            // Tìm cart item trước khi xóa để restore stock
            Optional<CartItem> cartItemOpt = cartItemRepository.findActiveCartItemByIdAndUser(request.cartItemId(), user);
            if (cartItemOpt.isEmpty()) {
                return createFailedDeleteResponse(getMessage("error.cart.item.not.found"));
            }

            CartItem cartItem = cartItemOpt.get();

            // Restore stock quantity
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + cartItem.getQuantity());
            productRepository.save(product);

            // Hard delete
            int deletedCount = cartItemRepository.hardDeleteCartItemByIdAndUser(request.cartItemId(), user);

            if (deletedCount == 0) {
                return createFailedDeleteResponse(getMessage("error.cart.item.not.found"));
            }

            // Get updated cart summary
            CartSummary summary = getCartSummaryForUser(user);

            log.info("Hard deleted cart item {} for user {}", request.cartItemId(), userId);

            return DeleteCartItemResponse.builder()
                    .success(true)
                    .message(getMessage("cart.item.delete.success"))
                    .deletedCartItemIds(List.of(request.cartItemId()))
                    .failedCartItemIds(List.of())
                    .totalItemsInCart(summary.totalItems())
                    .summary(summary)
                    .build();

        } catch (Exception e) {
            log.error("Error hard deleting cart item: {}", e.getMessage());
            return createFailedDeleteResponse(getMessage("error.cart.item.delete.failed", e.getMessage()));
        }
    }

    @Override
    public DeleteCartItemResponse hardDeleteMultipleCartItems(Authentication authentication, DeleteMultipleCartItemsRequest request) {
        try {
            Integer userId = userHelper.getCurrentUserId(authentication);
            User user = userHelper.findUserByIdOrThrow(userId.longValue());

            if (request.cartItemIds() == null || request.cartItemIds().isEmpty()) {
                return createFailedDeleteResponse(getMessage("error.cart.item.ids.required"));
            }

            // Tìm cart items trước khi xóa để restore stock
            List<CartItem> cartItems = cartItemRepository.findActiveCartItemsByIdsAndUser(request.cartItemIds(), user);

            if (cartItems.isEmpty()) {
                return createFailedDeleteResponse(getMessage("error.cart.items.not.found"));
            }

            // Restore stock quantities
            for (CartItem cartItem : cartItems) {
                Product product = cartItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() + cartItem.getQuantity());
                productRepository.save(product);
            }

            // Hard delete
            List<Integer> cartItemIds = cartItems.stream().map(CartItem::getId).collect(Collectors.toList());
            int deletedCount = cartItemRepository.hardDeleteCartItemsByIdsAndUser(cartItemIds, user);

            List<Integer> failedIds = request.cartItemIds().stream()
                    .filter(id -> !cartItemIds.contains(id))
                    .collect(Collectors.toList());

            // Get updated cart summary
            CartSummary summary = getCartSummaryForUser(user);

            log.info("Hard deleted {} cart items for user {}. Failed: {}", deletedCount, userId, failedIds.size());

            return DeleteCartItemResponse.builder()
                    .success(true)
                    .message(getMessage("cart.items.delete.success", deletedCount))
                    .deletedCartItemIds(cartItemIds)
                    .failedCartItemIds(failedIds)
                    .totalItemsInCart(summary.totalItems())
                    .summary(summary)
                    .build();

        } catch (Exception e) {
            log.error("Error hard deleting multiple cart items: {}", e.getMessage());
            return createFailedDeleteResponse(getMessage("error.cart.items.delete.failed", e.getMessage()));
        }
    }

// ==================== HELPER METHODS ====================

    private DeleteCartItemResponse createFailedDeleteResponse(String message) {
        return DeleteCartItemResponse.builder()
                .success(false)
                .message(message)
                .deletedCartItemIds(List.of())
                .failedCartItemIds(List.of())
                .build();
    }

    private CartSummary getCartSummaryForUser(User user) {
        List<CartItem> activeCartItems = cartItemRepository.findCartItemsWithProductAndImagesByUser(user);
        return calculateCartSummary(activeCartItems);
    }

}
