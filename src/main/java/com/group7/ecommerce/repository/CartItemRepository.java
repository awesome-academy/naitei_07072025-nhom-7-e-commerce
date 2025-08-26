package com.group7.ecommerce.repository;

import com.group7.ecommerce.entity.Cart;
import com.group7.ecommerce.entity.CartItem;
import com.group7.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    List<CartItem> findByCart(Cart cart);
    long countByCart(Cart cart);

}
