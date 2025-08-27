package com.group7.ecommerce.repository;

import com.group7.ecommerce.entity.Cart;
import com.group7.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserId(Integer userId);
}
