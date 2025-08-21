package com.group7.ecommerce.dto.response;

import com.group7.ecommerce.entity.ShipInfo;

public record ShipInfoResponse(
        Integer id,
        String receiver,
        String phone,
        String address,
        Boolean isDefault
) {
    public static ShipInfoResponse from(ShipInfo shipInfo) {
        return new ShipInfoResponse(
                shipInfo.getId(),
                shipInfo.getReceiver(),
                shipInfo.getPhone(),
                shipInfo.getAddress(),
                shipInfo.getIsDefault()
        );
    }
}
