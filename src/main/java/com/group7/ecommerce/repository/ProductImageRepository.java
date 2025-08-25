package com.group7.ecommerce.repository;

import com.group7.ecommerce.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    void deleteAllByProductId(Long productId);
    List<ProductImage> findByProductId(Long productId);

    List<ProductImage> findByProductIdIn(List<Long> productIds);

    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);

    Optional<ProductImage> findFirstByProductIdOrderByCreatedAtAsc(Long productId);
}
