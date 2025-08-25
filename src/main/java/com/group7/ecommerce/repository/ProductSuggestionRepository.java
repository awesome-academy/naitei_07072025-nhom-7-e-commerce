package com.group7.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.ecommerce.entity.ProductSuggestion;

public interface ProductSuggestionRepository extends JpaRepository<ProductSuggestion, Integer> {
}
