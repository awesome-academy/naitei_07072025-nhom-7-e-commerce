package com.group7.ecommerce.repository;

import java.util.Optional;

import com.group7.ecommerce.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.ecommerce.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	Optional<User> findByVerificationToken(String token);
	Optional<User> findByEmailOrUsername(String email, String username);
	boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    Page<User> findAllByRoleName(@Param("role") Role role, Pageable pageable);
}
