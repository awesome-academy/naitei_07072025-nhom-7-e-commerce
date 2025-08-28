package com.group7.ecommerce.repository;

import com.group7.ecommerce.entity.Cart;
import com.group7.ecommerce.entity.CartItem;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    long countByCart(Cart cart);
    Optional<CartItem> findByIdAndCart_User(Long cartItemId, User user);


    // Delete methods
    @Query("SELECT ci FROM CartItem ci WHERE ci.id IN :cartItemIds AND ci.cart.user = :user AND ci.isDeleted = false")
    List<CartItem> findActiveCartItemsByIdsAndUser(@Param("cartItemIds") List<Integer> cartItemIds, @Param("user") User user);

    @Query("SELECT ci FROM CartItem ci WHERE ci.id = :cartItemId AND ci.cart.user = :user AND ci.isDeleted = false")
    Optional<CartItem> findActiveCartItemByIdAndUser(@Param("cartItemId") Integer cartItemId, @Param("user") User user);

    // Hard delete methods
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id IN :cartItemIds AND ci.cart.user = :user")
    int hardDeleteCartItemsByIdsAndUser(@Param("cartItemIds") List<Integer> cartItemIds, @Param("user") User user);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id = :cartItemId AND ci.cart.user = :user")
    int hardDeleteCartItemByIdAndUser(@Param("cartItemId") Integer cartItemId, @Param("user") User user);

    @Query("""
            SELECT ci FROM CartItem ci
            JOIN FETCH ci.product p
            LEFT JOIN FETCH p.images pi
            WHERE ci.cart.user = :user AND p.isDeleted = false
            ORDER BY ci.createdAt DESC
            """)
    List<CartItem> findCartItemsWithProductAndImagesByUser(@Param("user") User user);
}
