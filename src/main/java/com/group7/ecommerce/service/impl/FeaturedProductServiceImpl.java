package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.dto.response.FeaturedProductResponse;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.entity.ProductImage;
import com.group7.ecommerce.enums.OrderStatus;
import com.group7.ecommerce.repository.ProductRepository;
import com.group7.ecommerce.repository.ProductImageRepository;
import com.group7.ecommerce.service.FeaturedProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeaturedProductServiceImpl implements FeaturedProductService {
    
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    
    @Override
    public List<FeaturedProductResponse> getFeaturedProducts(Pageable pageable) {
        log.info("Lấy danh sách sản phẩm nổi bật với phân trang: {}", pageable);
        
        List<Object[]> results = productRepository.findFeaturedProductsByOrderQuantity(
            OrderStatus.DELIVERED, pageable);
        
        return mapToFeaturedProductResponse(results);
    }
    
    @Override
    public List<FeaturedProductResponse> getTopFeaturedProducts(int limit) {
        log.info("Lấy top {} sản phẩm nổi bật", limit);
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = productRepository.findFeaturedProductsByOrderQuantity(
            OrderStatus.DELIVERED, pageable);
        
        return mapToFeaturedProductResponse(results);
    }
    
    private List<FeaturedProductResponse> mapToFeaturedProductResponse(List<Object[]> results) {
        return results.stream()
            .map(this::convertToFeaturedProductResponse)
            .toList();
    }
    
    private FeaturedProductResponse convertToFeaturedProductResponse(Object[] result) {
        Product product = (Product) result[0];
        Number totalSold = (Number) result[1];
        
        // Lấy hình ảnh chính của sản phẩm
        String imageUrl = null;
        ProductImage primaryImage = productImageRepository.findPrimaryImageByProductId(product.getId()).orElse(null);
        if (primaryImage != null) {
            imageUrl = primaryImage.getImageUrl();
        } else {
            ProductImage firstImage = productImageRepository.findFirstImageByProductId(product.getId()).orElse(null);
            if (firstImage != null) {
                imageUrl = firstImage.getImageUrl();
            }
        }
        
        return new FeaturedProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getSellingPrice(),
            imageUrl,
            product.getCategory() != null ? product.getCategory().getName() : null,
            product.getStockQuantity(),
            totalSold != null ? totalSold.longValue() : 0L
        );
    }
}
