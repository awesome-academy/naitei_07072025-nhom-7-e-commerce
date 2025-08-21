package com.group7.ecommerce.entity;

import com.group7.ecommerce.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "{user.username.notblank}")
    @Size(min = 2, max = 100, message = "{user.username.size}")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "{user.password.notblank}")
    @Size(min = 6, message = "{user.password.size}")
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.invalid}")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "{user.fullName.notblank}")
    @Size(min = 2, max = 100, message = "{user.fullName.size}")
    private String fullName;

    @NotBlank(message = "{phone.notblank}")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "{phone.invalid}")
    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isActive = false;

    @Column(name = "activation_token", length = 255)
    private String activationToken;

    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private boolean emailVerified = false;
    private String verificationToken;
}
