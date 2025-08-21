package com.group7.ecommerce.repository;

import com.group7.ecommerce.dto.request.ProductFilterDto;
import com.group7.ecommerce.dto.response.ProductListItemResponse;
import com.group7.ecommerce.entity.Category;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.entity.ProductImage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ProductListItemResponse> findProductsWithFilter(ProductFilterDto filterDto, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        // Query cho dữ liệu
        CriteriaQuery<ProductListItemResponse> query = cb.createQuery(ProductListItemResponse.class);
        Root<Product> productRoot = query.from(Product.class);
        
        // LEFT JOIN với Category
        Join<Product, Category> categoryJoin = productRoot.join("category", JoinType.LEFT);
        
        // Subquery để lấy primary image
        Subquery<String> imageSubquery = query.subquery(String.class);
        Root<ProductImage> imageRoot = imageSubquery.from(ProductImage.class);
        imageSubquery.select(imageRoot.get("imageUrl"));
        imageSubquery.where(
            cb.and(
                cb.equal(imageRoot.get("product").get("id"), productRoot.get("id")),
                cb.equal(imageRoot.get("isPrimary"), true)
            )
        );
        
        // Tạo selection cho ProductListItemResponse  
        query.select(cb.construct(ProductListItemResponse.class,
            productRoot.get("id"),
            productRoot.get("name"),
            productRoot.get("description"),
            productRoot.get("sellingPrice"),
            cb.coalesce(imageSubquery.getSelection(), cb.nullLiteral(String.class)),
            cb.coalesce(categoryJoin.get("name"), cb.nullLiteral(String.class)),
            productRoot.get("stockQuantity")
        ));
        
        // Xây dựng WHERE clause
        List<Predicate> predicates = buildPredicates(cb, productRoot, categoryJoin, filterDto);
        
        // Thêm điều kiện không bị xóa
        predicates.add(cb.equal(productRoot.get("isDeleted"), false));
        
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        
        // Áp dụng sắp xếp động
        query.orderBy(buildOrderBy(cb, productRoot, categoryJoin, filterDto));
        
        // Thực hiện query với phân trang
        TypedQuery<ProductListItemResponse> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        
        List<ProductListItemResponse> results = typedQuery.getResultList();
        
        // Query để đếm tổng số
        long total = countProductsWithFilter(filterDto);
        
        return new PageImpl<>(results, pageable, total);
    }
    
    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Product> productRoot, 
                                          Join<Product, Category> categoryJoin,
                                          ProductFilterDto filterDto) {
        List<Predicate> predicates = new ArrayList<>();
        
        // Lọc theo tên sản phẩm
        if (filterDto.name() != null && !filterDto.name().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(productRoot.get("name")), 
                "%" + filterDto.name().toLowerCase() + "%"));
        }
        
        // Lọc theo mô tả
        if (filterDto.description() != null && !filterDto.description().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(productRoot.get("description")), 
                "%" + filterDto.description().toLowerCase() + "%"));
        }
        
        // Lọc theo khoảng giá bán
        if (filterDto.minSellingPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(productRoot.get("sellingPrice"), filterDto.minSellingPrice()));
        }
        if (filterDto.maxSellingPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(productRoot.get("sellingPrice"), filterDto.maxSellingPrice()));
        }
        
        // Lọc theo khoảng giá nhập
        if (filterDto.minImportPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(productRoot.get("importPrice"), filterDto.minImportPrice()));
        }
        if (filterDto.maxImportPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(productRoot.get("importPrice"), filterDto.maxImportPrice()));
        }
        
        // Lọc theo số lượng tồn kho
        if (filterDto.minStockQuantity() != null) {
            predicates.add(cb.greaterThanOrEqualTo(productRoot.get("stockQuantity"), filterDto.minStockQuantity()));
        }
        if (filterDto.maxStockQuantity() != null) {
            predicates.add(cb.lessThanOrEqualTo(productRoot.get("stockQuantity"), filterDto.maxStockQuantity()));
        }
        
        // Lọc theo category ID
        if (filterDto.categoryId() != null) {
            predicates.add(cb.equal(categoryJoin.get("id"), filterDto.categoryId()));
        }
        
        // Lọc theo tên category
        if (filterDto.categoryName() != null && !filterDto.categoryName().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(categoryJoin.get("name")), 
                "%" + filterDto.categoryName().toLowerCase() + "%"));
        }
        
        // Lọc theo trạng thái nổi bật
        if (filterDto.isFeatured() != null) {
            predicates.add(cb.equal(productRoot.get("isFeatured"), filterDto.isFeatured()));
        }
        
        // Lọc theo thời gian tạo
        if (filterDto.createdAfter() != null) {
            predicates.add(cb.greaterThanOrEqualTo(productRoot.get("createdAt"), filterDto.createdAfter()));
        }
        if (filterDto.createdBefore() != null) {
            predicates.add(cb.lessThanOrEqualTo(productRoot.get("createdAt"), filterDto.createdBefore()));
        }
        
        // Lọc theo thời gian cập nhật
        if (filterDto.updatedAfter() != null) {
            predicates.add(cb.greaterThanOrEqualTo(productRoot.get("updatedAt"), filterDto.updatedAfter()));
        }
        if (filterDto.updatedBefore() != null) {
            predicates.add(cb.lessThanOrEqualTo(productRoot.get("updatedAt"), filterDto.updatedBefore()));
        }
        
        return predicates;
    }
    
    private long countProductsWithFilter(ProductFilterDto filterDto) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> productRoot = countQuery.from(Product.class);
        
        // LEFT JOIN với Category cho count
        Join<Product, Category> categoryJoin = productRoot.join("category", JoinType.LEFT);
        
        countQuery.select(cb.count(productRoot));
        
        // Xây dựng WHERE clause
        List<Predicate> predicates = buildPredicates(cb, productRoot, categoryJoin, filterDto);
        predicates.add(cb.equal(productRoot.get("isDeleted"), false));
        
        countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        
        return entityManager.createQuery(countQuery).getSingleResult();
    }
    
    /**
     * Xây dựng Order By clause dựa trên tham số sắp xếp
     */
    private Order buildOrderBy(CriteriaBuilder cb, Root<Product> productRoot, 
                              Join<Product, Category> categoryJoin, ProductFilterDto filterDto) {
        String sortBy = filterDto.sortBy();
        String sortDirection = filterDto.sortDirection();
        
        // Mặc định nếu không có tham số
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "createdAt";
        }
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            sortDirection = "desc";
        }
        
        // Xác định field để sắp xếp
        Expression<?> sortExpression = switch (sortBy) {
            case "name" -> productRoot.get("name");
            case "sellingPrice" -> productRoot.get("sellingPrice");
            case "importPrice" -> productRoot.get("importPrice");
            case "stockQuantity" -> productRoot.get("stockQuantity");
            case "createdAt" -> productRoot.get("createdAt");
            case "updatedAt" -> productRoot.get("updatedAt");
            case "categoryName" -> categoryJoin.get("name");
            default -> productRoot.get("createdAt"); // Fallback
        };
        
        // Xác định hướng sắp xếp
        return "asc".equalsIgnoreCase(sortDirection) 
            ? cb.asc(sortExpression) 
            : cb.desc(sortExpression);
    }
}
