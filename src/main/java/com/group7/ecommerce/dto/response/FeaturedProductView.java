package com.group7.ecommerce.dto.response;

import com.group7.ecommerce.entity.Product;

public interface FeaturedProductView {
    Product getProduct();
    Long getTotalSold();
}
