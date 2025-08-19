package com.group7.ecommerce.dto.request;

import lombok.Data;

@Data
public class ShipInfoDeletionRequest {
    private boolean confirmSoftDelete;
    private Integer newDefaultAddressId; // Optional: let user choose new default
}
