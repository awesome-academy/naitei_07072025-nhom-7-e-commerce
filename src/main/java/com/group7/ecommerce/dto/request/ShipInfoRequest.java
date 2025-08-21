package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ShipInfoRequest {

    @NotBlank(message = "{ship.receiver.notblank}")
    @Size(max = 50, message = "{ship.receiver.size}")
    private String receiver;

    @NotBlank(message = "{phone.notblank}")
    @Pattern(regexp = "^0[0-9]{9}$", message = "{phone.invalid}")
    private String phone;

    @NotBlank(message = "{ship.address.notblank}")
    @Size(max = 100, message = "{ship.address.size}")
    private String address;

    private Boolean isDefault = false;
}
