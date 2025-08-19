package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.dto.request.ShipInfoDeletionRequest;
import com.group7.ecommerce.dto.request.ShipInfoRequest;
import com.group7.ecommerce.dto.response.ShipInfoDeletionInfo;
import com.group7.ecommerce.dto.response.ShipInfoResponse;
import com.group7.ecommerce.entity.Order;
import com.group7.ecommerce.entity.ShipInfo;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.repository.OrderRepository;
import com.group7.ecommerce.repository.ShipInfoRepository;
import com.group7.ecommerce.repository.UserRepository;
import com.group7.ecommerce.service.ShipInfoService;
import com.group7.ecommerce.utils.CustomUserDetails;
import com.group7.ecommerce.utils.helper.ShipInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShipInfoServiceImpl implements ShipInfoService {

    @Autowired
    private ShipInfoRepository shipInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipInfoHelper shipInfoHelper;

    @Autowired
    private MessageSource messageSource;

    /**
     * Lấy danh sách địa chỉ giao hàng của user
     */
    @Override
    public List<ShipInfoResponse> getShipInfoByUserId(Authentication authentication) {
        Integer userId = getCurrentUserId(authentication);
        List<ShipInfo> shipInfos = shipInfoRepository.findByUserIdOrderByIdDesc(userId);
        return shipInfos.stream()
                .map(ShipInfoResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Lấy địa chỉ giao hàng mặc định của user
     */
    public ShipInfoResponse getDefaultShipInfo(Authentication authentication) {
        Integer userId = getCurrentUserId(authentication);
        Optional<ShipInfo> defaultShipInfo = shipInfoRepository.findByUserIdAndIsDefaultTrue(userId);

        return defaultShipInfo
                .map(ShipInfoResponse::from)
                .orElse(null);
    }

    /**
     * Tạo mới thông tin giao hàng
     */
    @Override
    @Transactional(readOnly = false)
    public ShipInfoResponse createShipInfo(Authentication authentication, ShipInfoRequest request) {
        Integer userId = getCurrentUserId(authentication);
        User user = getUserById(userId);

        shipInfoHelper.handleDefaultAddressSetting(userId, request.getIsDefault());

        ShipInfo shipInfo = shipInfoHelper.buildShipInfoFromRequest(user, request);
        ShipInfo savedShipInfo = shipInfoRepository.save(shipInfo);

        return ShipInfoResponse.from(savedShipInfo);
    }

    /**
     * Cập nhật thông tin giao hàng
     */
    @Override
    @Transactional(readOnly = false)
    public ShipInfoResponse updateShipInfo(Authentication authentication, Integer shipInfoId, ShipInfoRequest request) {
        Integer userId = getCurrentUserId(authentication);
        ShipInfo shipInfo = getShipInfoByUserAndId(userId, shipInfoId);

        if (request.getIsDefault()) {
            shipInfoHelper.handleDefaultAddressSetting(userId, true, shipInfoId);
        }

        shipInfoHelper.updateShipInfoFromRequest(shipInfo, request);
        ShipInfo updatedShipInfo = shipInfoRepository.save(shipInfo);

        return ShipInfoResponse.from(updatedShipInfo);
    }

    /**
     * Xóa thông tin giao hàng
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteShipInfo(Authentication authentication, Integer shipInfoId) {
        Integer userId = getCurrentUserId(authentication);
        ShipInfo shipInfoToDelete = getShipInfoByUserAndId(userId, shipInfoId);

        List<Order> ordersUsingThisAddress = orderRepository.findByUserIdAndShipInfo(userId, shipInfoToDelete);

        if (!ordersUsingThisAddress.isEmpty()) {
            // Soft delete - có orders đang sử dụng
            performSoftDelete(shipInfoToDelete, userId, shipInfoId);
        } else {
            // Hard delete - không có orders nào sử dụng
            performHardDelete(shipInfoToDelete, userId, shipInfoId);
        }
    }

    /**
     * Kiểm tra xem có thể xóa địa chỉ không và trả về thông tin cần thiết
     */
    @Override
    public ShipInfoDeletionInfo checkDeletionEligibility(Authentication authentication, Integer shipInfoId) {
        Integer userId = getCurrentUserId(authentication);
        ShipInfo shipInfo = getShipInfoByUserAndId(userId, shipInfoId);

        List<Order> affectedOrders = orderRepository.findByUserIdAndShipInfo(userId, shipInfo);
        List<ShipInfo> remainingAddresses = shipInfoRepository.findByUserId(userId).stream()
                .filter(info -> info.getId() != shipInfoId && !info.getIsDeleted())
                .toList();

        return ShipInfoDeletionInfo.builder()
                .canDelete(true)
                .hasAffectedOrders(!affectedOrders.isEmpty())
                .affectedOrderCount(affectedOrders.size())
                .willBeSoftDeleted(!affectedOrders.isEmpty())
                .remainingAddresses(remainingAddresses.stream()
                        .map(ShipInfoResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Xóa địa chỉ với xác nhận từ user
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteShipInfoWithConfirmation(Authentication authentication,
                                               Integer shipInfoId,
                                               ShipInfoDeletionRequest confirmationRequest) {

        ShipInfoDeletionInfo deletionInfo = checkDeletionEligibility(authentication, shipInfoId);

        if (deletionInfo.isHasAffectedOrders() && !confirmationRequest.isConfirmSoftDelete()) {
            throw new RuntimeException(getMessage("shipinfo.error.soft.delete.not.confirmed"));
        }

        deleteShipInfo(authentication, shipInfoId);
    }

    /**
     * Thực hiện soft delete
     */
    private void performSoftDelete(ShipInfo shipInfoToDelete, Integer userId, Integer shipInfoId) {
        shipInfoToDelete.setIsDeleted(true);
        shipInfoToDelete.setDeletedAt(LocalDateTime.now());

        // Nếu là địa chỉ mặc định, cần reassign default cho địa chỉ khác
        if (shipInfoToDelete.getIsDefault()) {
            shipInfoHelper.reassignDefaultAddress(userId, shipInfoId);
        }

        shipInfoRepository.save(shipInfoToDelete);
    }

    /**
     * Thực hiện hard delete
     */
    private void performHardDelete(ShipInfo shipInfoToDelete, Integer userId, Integer shipInfoId) {
        // Nếu là địa chỉ mặc định, reassign trước khi xóa
        if (shipInfoToDelete.getIsDefault()) {
            List<ShipInfo> remainingAddresses = shipInfoRepository.findByUserId(userId).stream()
                    .filter(info -> info.getId() != shipInfoId)
                    .collect(Collectors.toList());

            shipInfoHelper.handleDefaultAddressReassignment(remainingAddresses);
        }

        shipInfoRepository.deleteById(shipInfoId);
    }

    /**
     * Lấy user theo ID với exception handling
     */
    private User getUserById(Integer userId) {
        return userRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException(getMessage("error.user.not.found")));
    }

    /**
     * Lấy ShipInfo theo userId và shipInfoId với exception handling
     */
    private ShipInfo getShipInfoByUserAndId(Integer userId, Integer shipInfoId) {
        return shipInfoRepository.findByUserIdAndId(userId, shipInfoId)
                .orElseThrow(() -> new RuntimeException(getMessage("shipinfo.error.not.found")));
    }

    /**
     * Lấy user ID từ authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        return ((CustomUserDetails) authentication.getPrincipal()).getId().intValue();
    }

    /**
     * Helper method để lấy localized message
     */
    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }
}
