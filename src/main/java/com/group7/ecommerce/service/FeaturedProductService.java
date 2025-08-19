package com.group7.ecommerce.service;

import com.group7.ecommerce.dto.response.FeaturedProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeaturedProductService {
    
    /**
     * Lấy danh sách sản phẩm nổi bật dựa trên số lượng đơn hàng
     */
    List<FeaturedProductResponse> getFeaturedProducts(Pageable pageable);
    
    /**
     * Lấy top N sản phẩm nổi bật
     */
    List<FeaturedProductResponse> getTopFeaturedProducts(int limit);
}
