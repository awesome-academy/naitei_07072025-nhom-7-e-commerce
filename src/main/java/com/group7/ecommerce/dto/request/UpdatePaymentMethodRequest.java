package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentMethodRequest {

    @NotNull(message = "Payment method is required")
    @Pattern(regexp = "COD|BANK_TRANSFER|VNPAY|MOMO",
            message = "Payment method must be COD, BANK_TRANSFER, VNPAY, or MOMO")
    private String paymentMethod;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
