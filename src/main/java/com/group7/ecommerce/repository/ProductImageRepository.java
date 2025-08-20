package com.group7.ecommerce.repository;

import com.group7.ecommerce.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    void deleteAllByProductId(Long productId);
    List<ProductImage> findByProductId(Long productId);
}
