package com.group7.ecommerce.repository;

import com.group7.ecommerce.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    
    /**
     * Lấy hình ảnh chính của sản phẩm
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isPrimary = true")
    Optional<ProductImage> findPrimaryImageByProductId(@Param("productId") Long productId);
    
    /**
     * Lấy hình ảnh đầu tiên của sản phẩm (nếu không có hình chính)
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId ORDER BY pi.isPrimary DESC, pi.id ASC")
    Optional<ProductImage> findFirstImageByProductId(@Param("productId") Long productId);
}
