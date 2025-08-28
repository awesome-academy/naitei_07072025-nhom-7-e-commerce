package com.group7.ecommerce.utils.helper;

import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.repository.ProductRepository;
import com.group7.ecommerce.utils.validator.CartValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductHelper {

    private final ProductRepository productRepository;
    private final MessageSource messageSource;

    /**
     * Đếm tổng số sản phẩm
     */
    public long getTotalProducts() {
        try {
            return productRepository.count();
        } catch (Exception e) {
            log.error("Error getting total products count", e);
            return 0L;
        }
    }

    public Product findByIdAndIsDeletedFalseOrThrow(Integer productId) {
        Product product = productRepository.findById(productId.longValue())
                .orElseThrow(() -> new RuntimeException(
                        getMessage("error.product.not.found", productId)));

        if (product.isDeleted()) {
            throw new RuntimeException(getMessage("error.product.not.available"));
        }

        return product;
    }

    /**
     * Helper method to get localized message
     */
    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }
}
