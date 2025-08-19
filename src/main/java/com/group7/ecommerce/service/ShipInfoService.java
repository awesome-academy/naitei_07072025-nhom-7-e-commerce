package com.group7.ecommerce.service;

import com.group7.ecommerce.dto.request.ShipInfoDeletionRequest;
import com.group7.ecommerce.dto.request.ShipInfoRequest;
import com.group7.ecommerce.dto.response.ShipInfoDeletionInfo;
import com.group7.ecommerce.dto.response.ShipInfoResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ShipInfoService {

    /**
     * Lấy danh sách địa chỉ giao hàng của user
     */
    List<ShipInfoResponse> getShipInfoByUserId(Authentication authentication);

    /**
     * Lấy địa chỉ giao hàng mặc định của user
     */
    ShipInfoResponse getDefaultShipInfo(Authentication authentication);

    /**
     * Tạo mới thông tin giao hàng
     */
    ShipInfoResponse createShipInfo(Authentication authentication, ShipInfoRequest request);

    /**
     * Cập nhật thông tin giao hàng
     */
    ShipInfoResponse updateShipInfo(Authentication authentication, Integer shipInfoId, ShipInfoRequest request);

    /**
     * Xóa thông tin giao hàng
     */
    void deleteShipInfo(Authentication authentication, Integer shipInfoId);

    ShipInfoDeletionInfo checkDeletionEligibility(Authentication authentication, Integer shipInfoId);

    void deleteShipInfoWithConfirmation(Authentication authentication,
                                        Integer shipInfoId,
                                        ShipInfoDeletionRequest confirmationRequest);
}
