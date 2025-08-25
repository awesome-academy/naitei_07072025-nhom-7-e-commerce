package com.group7.ecommerce.dto.response;

import com.group7.ecommerce.entity.User;
import java.math.BigDecimal;

public record ShowProfileResponse(
        User user,
        Long totalUsers,
        Long totalProducts,
        long totalOrders,
        BigDecimal totalRevenue,
        Long pendingOrders
) {}