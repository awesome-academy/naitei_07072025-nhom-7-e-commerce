package com.group7.ecommerce.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group7.ecommerce.dto.response.FeaturedProductView;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.enums.OrderStatus;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	/**
	 * Lấy danh sách sản phẩm nổi bật dựa trên tổng số lượng bán ra
	 * Chỉ tính các đơn hàng đã hoàn thành (DELIVERED)
	 */
	@Query("""
			SELECT p as product, COALESCE(SUM(oi.quantity), 0) as totalSold
			FROM Product p
			JOIN OrderItem oi ON p.id = oi.product.id
			JOIN Order o ON oi.order.id = o.id AND o.status = :status
			WHERE p.stockQuantity > 0
			GROUP BY p.id
			ORDER BY COALESCE(SUM(oi.quantity), 0) DESC, p.createdAt DESC
			""")
	List<FeaturedProductView> findFeaturedProductsByOrderQuantity(OrderStatus status, Pageable pageable);
	Optional<Product> findByIdAndIsDeletedFalse(Integer id);
	@Query("SELECT COUNT(p) FROM Product p WHERE p.category.id IN :categoryIds AND p.isDeleted = false")
	long countActiveProductsInCategoryIds(@Param("categoryIds") Set<Long> categoryIds);
}
