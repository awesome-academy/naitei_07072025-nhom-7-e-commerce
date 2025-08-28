package com.group7.ecommerce.repository;

import com.group7.ecommerce.dto.request.ProductFilterDto;
import com.group7.ecommerce.dto.response.ProductListItemProjection;
import com.group7.ecommerce.dto.response.FeaturedProductView;
import com.group7.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.group7.ecommerce.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, 
                                         JpaSpecificationExecutor<Product> {

	@Query(value = """
		SELECT p.id as id,
		       p.name as name,
		       p.description as description,
		       p.sellingPrice as sellingPrice,
		       pi.imageUrl as imageUrl,
		       p.category.name as categoryName,
		       p.stockQuantity as stockQuantity
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
	Page<ProductListItemProjection> findAllActiveProducts(Pageable pageable);
	

    
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
