package com.group7.ecommerce.repository;

import com.group7.ecommerce.dto.response.ProductListItemResponse;
import com.group7.ecommerce.dto.response.FeaturedProductView;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query(value = """
		SELECT new com.group7.ecommerce.dto.response.ProductListItemResponse(
			p.id,
			p.name,
			p.description,
			p.sellingPrice,
			pi.imageUrl,
			CASE WHEN p.category IS NULL THEN NULL ELSE p.category.name END,
			p.stockQuantity
		)
		FROM Product p
		LEFT JOIN ProductImage pi ON pi.product = p AND pi.isPrimary = true
		WHERE p.isDeleted = false
		ORDER BY p.createdAt DESC
		""",
		countQuery = """
		SELECT COUNT(p)
		FROM Product p
		WHERE p.isDeleted = false
		""")
	Page<ProductListItemResponse> findAllActiveProducts(Pageable pageable);
    
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
}
