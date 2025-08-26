package com.group7.ecommerce.repository;

import com.group7.ecommerce.entity.Cart;
import com.group7.ecommerce.entity.CartItem;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    long countByCart(Cart cart);
    Optional<CartItem> findByIdAndCart_User(Long cartItemId, User user);

    @Query("""
            SELECT ci FROM CartItem ci
            JOIN FETCH ci.product p
            LEFT JOIN FETCH p.images pi
            WHERE ci.cart.user = :user AND p.isDeleted = false
            ORDER BY ci.createdAt DESC
            """)
    List<CartItem> findCartItemsWithProductAndImagesByUser(@Param("user") User user);
}
