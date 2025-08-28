package com.group7.ecommerce.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Ship info ID is required")
    private Integer shipInfoId;

    @Pattern(regexp = "COD|BANK_TRANSFER|VNPAY|MOMO",
            message = "Payment method must be COD, BANK_TRANSFER, VNPAY, or MOMO")
    private String paymentMethod = "COD";

    // Danh sách sản phẩm (dùng cho mua ngay) - sử dụng OrderItemRequest để tương thích với validation
    @Valid
    private List<OrderRequestItem> items;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private boolean fromCart = true;
}
