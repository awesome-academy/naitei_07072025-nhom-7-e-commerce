package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.dto.response.FeaturedProductResponse;
import com.group7.ecommerce.dto.response.FeaturedProductView;
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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeaturedProductServiceImpl implements FeaturedProductService {
    
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    
    @Override
    public List<FeaturedProductResponse> getFeaturedProducts(Pageable pageable) {
        log.info("Fetching featured products with pagination: {}", pageable);
        
        List<FeaturedProductView> results = productRepository.findFeaturedProductsByOrderQuantity(
            OrderStatus.DELIVERED, pageable);
        
        return mapToFeaturedProductResponse(results);
    }
    
    @Override
    public List<FeaturedProductResponse> getTopFeaturedProducts(int limit) {
        log.info("Fetching top {} featured products", limit);
        
        Pageable pageable = PageRequest.of(0, limit);
        List<FeaturedProductView> results = productRepository.findFeaturedProductsByOrderQuantity(
            OrderStatus.DELIVERED, pageable);
        
        return mapToFeaturedProductResponse(results);
    }
    
    private List<FeaturedProductResponse> mapToFeaturedProductResponse(List<FeaturedProductView> results) {
        // Gom tất cả productIds
        List<Product> products = results.stream().map(FeaturedProductView::getProduct).toList();
        List<Long> productIds = products.stream().map(Product::getId).toList();

        // Batch fetch tất cả images liên quan chỉ với 1 query
        List<ProductImage> allImages = productImageRepository.findByProductIdIn(productIds);

        // Map productId -> ảnh chính
        Map<Long, ProductImage> productIdToPrimary = allImages.stream()
                .filter(ProductImage::isPrimary)
                .collect(Collectors.toMap(img -> img.getProduct().getId(), Function.identity(), (a, b) -> a));

        // Map productId -> ảnh đầu tiên theo createdAt (fallback)
        Map<Long, ProductImage> productIdToFirst = allImages.stream()
                .collect(Collectors.groupingBy(img -> img.getProduct().getId(),
                        Collectors.collectingAndThen(
                                Collectors.minBy(java.util.Comparator.comparing(ProductImage::getCreatedAt,
                                        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()))),
                                opt -> opt.orElse(null))));

        return results.stream().map(result -> {
            Product product = result.getProduct();
            long totalSold = result.getTotalSold() == null ? 0L : result.getTotalSold();

            String imageUrl = null;
            ProductImage primary = productIdToPrimary.get(product.getId());
            if (primary != null) {
                imageUrl = primary.getImageUrl();
            } else {
                ProductImage first = productIdToFirst.get(product.getId());
                if (first != null) {
                    imageUrl = first.getImageUrl();
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
                    totalSold
            );
        }).toList();
    }
}
