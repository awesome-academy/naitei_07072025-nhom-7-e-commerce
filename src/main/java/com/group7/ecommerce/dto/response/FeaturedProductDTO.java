package com.group7.ecommerce.dto.response;

import com.group7.ecommerce.entity.Product;

public record FeaturedProductDTO(Product product, long totalSold) {}
