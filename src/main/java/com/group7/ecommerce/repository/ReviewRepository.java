package com.group7.ecommerce.repository;

import com.group7.ecommerce.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
    @Query("""
        SELECT r FROM Review r
        JOIN FETCH r.user u
        WHERE r.product.id = :productId
        ORDER BY r.createdAt DESC
        """)
    List<Review> findByProductIdWithUser(@Param("productId") Long productId);
    
    @Query("""
        SELECT 
            AVG(CAST(r.rating AS double)) as averageRating,
            COUNT(r) as totalReviews,
            SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END) as fiveStars,
            SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END) as fourStars,
            SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END) as threeStars,
            SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END) as twoStars,
            SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END) as oneStar
        FROM Review r
        WHERE r.product.id = :productId
        """)
    Object[] getReviewStatsByProductId(@Param("productId") Long productId);
}
