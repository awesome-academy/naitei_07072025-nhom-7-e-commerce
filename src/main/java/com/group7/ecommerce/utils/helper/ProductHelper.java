package com.group7.ecommerce.utils.helper;

import com.group7.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductHelper {

    private final ProductRepository productRepository;

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
}
