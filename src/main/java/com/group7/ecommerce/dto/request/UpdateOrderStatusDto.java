package com.group7.ecommerce.dto.request;

import com.group7.ecommerce.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusDto {

	@NotNull(message = "Trạng thái mới không được để trống")
	private OrderStatus newStatus;

	private Integer reasonId;

	private String adminNote;
}
