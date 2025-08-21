package com.group7.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ShipInfoDeletionInfo {
    private boolean canDelete;
    private boolean hasAffectedOrders;
    private int affectedOrderCount;
    private boolean willBeSoftDeleted;
    private List<ShipInfoResponse> remainingAddresses;
}
