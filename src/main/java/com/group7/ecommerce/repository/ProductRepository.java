package com.group7.ecommerce.repository;

import com.group7.ecommerce.entity.Product;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"images"})
    List<Product> findAll();
}
