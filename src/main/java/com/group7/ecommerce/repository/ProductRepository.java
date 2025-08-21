package com.group7.ecommerce.repository;

import com.group7.ecommerce.dto.request.ProductFilterDto;
import com.group7.ecommerce.dto.response.ProductListItemResponse;
import com.group7.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

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
	
	@Query("""
		SELECT p FROM Product p
		LEFT JOIN FETCH p.category c
		WHERE p.id = :id AND p.isDeleted = false
		""")
	Optional<Product> findActiveProductByIdWithCategory(@Param("id") Long id);

}
