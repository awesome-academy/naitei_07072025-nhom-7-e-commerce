package com.group7.ecommerce.dto.response;

import java.time.LocalDateTime;

public record CustomerResponse (
    long id,
    String fullName,
    String username,
    String email,
    String phone,
    boolean emailVerified,
    LocalDateTime createdAt
) { }
