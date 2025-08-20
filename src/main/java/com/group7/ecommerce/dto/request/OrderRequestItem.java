package com.group7.ecommerce.dto.request;

import lombok.Data;

@Data
public class OrderRequestItem {
    private Long productId;
    private int quantity;
}
