package com.group7.ecommerce.repository;

import com.group7.ecommerce.dto.request.ProductFilterDto;
import com.group7.ecommerce.entity.Product;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Specifications cho Product entity sử dụng Spring Data JPA
 * Giúp xây dựng các điều kiện filter động một cách dễ bảo trì
 */
public class ProductSpecification {

    /**
     * Tạo specification cho việc filter sản phẩm
     */
    public static Specification<Product> withFilter(ProductFilterDto filterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Điều kiện cơ bản: không bị xóa
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            
            // Filter theo tên sản phẩm
            if (filterDto.name() != null && !filterDto.name().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), 
                    "%" + filterDto.name().toLowerCase() + "%"
                ));
            }
            
            // Filter theo mô tả
            if (filterDto.description() != null && !filterDto.description().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), 
                    "%" + filterDto.description().toLowerCase() + "%"
                ));
            }
            
            // Filter theo khoảng giá bán
            if (filterDto.minSellingPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("sellingPrice"), filterDto.minSellingPrice()
                ));
            }
            if (filterDto.maxSellingPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("sellingPrice"), filterDto.maxSellingPrice()
                ));
            }
            
            // Filter theo khoảng giá nhập
            if (filterDto.minImportPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("importPrice"), filterDto.minImportPrice()
                ));
            }
            if (filterDto.maxImportPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("importPrice"), filterDto.maxImportPrice()
                ));
            }
            
            // Filter theo khoảng số lượng tồn kho
            if (filterDto.minStockQuantity() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("stockQuantity"), filterDto.minStockQuantity()
                ));
            }
            if (filterDto.maxStockQuantity() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("stockQuantity"), filterDto.maxStockQuantity()
                ));
            }
            
            // Filter theo category ID
            if (filterDto.categoryId() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("category").get("id"), filterDto.categoryId()
                ));
            }
            
            // Filter theo tên category
            if (filterDto.categoryName() != null && !filterDto.categoryName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("category").get("name")), 
                    "%" + filterDto.categoryName().toLowerCase() + "%"
                ));
            }
            
            // Filter theo trạng thái nổi bật
            if (filterDto.isFeatured() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("isFeatured"), filterDto.isFeatured()
                ));
            }
            
            // Filter theo thời gian tạo
            if (filterDto.getParsedCreatedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"), filterDto.getParsedCreatedAfter()
                ));
            }
            if (filterDto.getParsedCreatedBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"), filterDto.getParsedCreatedBefore()
                ));
            }
            
            // Filter theo thời gian cập nhật
            if (filterDto.getParsedUpdatedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("updatedAt"), filterDto.getParsedUpdatedAfter()
                ));
            }
            if (filterDto.getParsedUpdatedBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("updatedAt"), filterDto.getParsedUpdatedBefore()
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Tạo specification cho việc sắp xếp
     */
    public static Specification<Product> withSort(ProductFilterDto filterDto) {
        return (root, query, criteriaBuilder) -> {
            // Chỉ áp dụng sắp xếp cho query chính, không phải count query
            if (query.getResultType().equals(Product.class)) {
                String sortBy = filterDto.getValidatedSortBy();
                String sortDirection = filterDto.getValidatedSortDirection();
                
                Expression<?> sortExpression = switch (sortBy) {
                    case "name" -> root.get("name");
                    case "sellingPrice" -> root.get("sellingPrice");
                    case "importPrice" -> root.get("importPrice");
                    case "stockQuantity" -> root.get("stockQuantity");
                    case "createdAt" -> root.get("createdAt");
                    case "updatedAt" -> root.get("updatedAt");
                    case "categoryName" -> root.get("category").get("name");
                    default -> root.get("createdAt");
                };
                
                Order order = "asc".equalsIgnoreCase(sortDirection) 
                    ? criteriaBuilder.asc(sortExpression) 
                    : criteriaBuilder.desc(sortExpression);
                
                query.orderBy(order);
            }
            
            return null; // Không thêm điều kiện WHERE
        };
    }
}
