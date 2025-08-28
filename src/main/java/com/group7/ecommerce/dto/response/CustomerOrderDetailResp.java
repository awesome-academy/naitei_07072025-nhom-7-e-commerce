package com.group7.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.group7.ecommerce.enums.OrderStatus;

public record CustomerOrderDetailResp(
        int id,
        LocalDateTime orderDate,
        OrderStatus status,
        String paymentMethod,
        BigDecimal totalAmount,
        
        // Thông tin giao hàng
        String receiverName,
        String receiverPhone,
        String shippingAddress,
        
        // Danh sách sản phẩm
        List<CustomerOrderItemResp> items
) {}
