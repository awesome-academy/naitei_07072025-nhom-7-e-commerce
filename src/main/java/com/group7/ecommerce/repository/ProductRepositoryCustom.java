package com.group7.ecommerce.repository;

import com.group7.ecommerce.dto.request.ProductFilterDto;
import com.group7.ecommerce.dto.response.ProductListItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductListItemResponse> findProductsWithFilter(ProductFilterDto filterDto, Pageable pageable);
}
