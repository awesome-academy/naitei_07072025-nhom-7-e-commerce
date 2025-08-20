package com.group7.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.ecommerce.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	Optional<User> findByVerificationToken(String token);
	Optional<User> findByEmailOrUsername(String email, String username);
	boolean existsByUsername(String username);
}
