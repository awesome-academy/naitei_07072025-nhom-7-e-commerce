package com.group7.ecommerce.utils.helper;

import com.group7.ecommerce.dto.request.ShipInfoRequest;
import com.group7.ecommerce.entity.Order;
import com.group7.ecommerce.entity.ShipInfo;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.repository.OrderRepository;
import com.group7.ecommerce.repository.ShipInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShipInfoHelper {

    private final ShipInfoRepository shipInfoRepository;
    private final MessageSource messageSource;
    private final OrderRepository orderRepository;

    public void setIsDefaultWithCustomQuery(Integer userId, ShipInfoRequest request) {
        if (!request.getIsDefault()) {
            return;
        }

        try {
            shipInfoRepository.updateAllToNotDefault(userId);

            String message = messageSource.getMessage(
                    "shipinfo.default.update.all.success",
                    new Object[]{userId},
                    LocaleContextHolder.getLocale()
            );
            log.info(message);

        } catch (Exception e) {
            String errorMessage = messageSource.getMessage(
                    "shipinfo.default.update.error",
                    new Object[]{userId, e.getMessage()},
                    LocaleContextHolder.getLocale()
            );
            log.error(errorMessage);

            String exceptionMessage = messageSource.getMessage(
                    "shipinfo.default.update.failed",
                    null,
                    LocaleContextHolder.getLocale()
            );
            throw new RuntimeException(exceptionMessage, e);
        }
    }

    /**
     * Xử lý việc set default address khi tạo mới hoặc cập nhật
     */
    public void handleDefaultAddressSetting(Integer userId, boolean isDefault) {
        handleDefaultAddressSetting(userId, isDefault, null);
    }

    /**
     * Xử lý việc set default address với exclude ID (dành cho update)
     */
    public void handleDefaultAddressSetting(Integer userId, boolean isDefault, Integer excludeId) {
        if (isDefault) {
            // Unset tất cả default addresses khác của user này
            Optional<ShipInfo> currentDefault = shipInfoRepository.findByUserIdAndIsDefaultTrue(userId);

            if (currentDefault.isPresent() &&
                    (excludeId == null || !currentDefault.get().getId().equals(excludeId))) {
                currentDefault.get().setIsDefault(false);
                shipInfoRepository.save(currentDefault.get());
            }
        }
    }

    /**
     * Build ShipInfo entity từ request
     */
    public ShipInfo buildShipInfoFromRequest(User user, ShipInfoRequest request) {
        ShipInfo shipInfo = new ShipInfo();
        shipInfo.setUser(user);
        shipInfo.setReceiver(request.getReceiver());
        shipInfo.setPhone(request.getPhone());
        shipInfo.setAddress(request.getAddress());
        shipInfo.setIsDefault(request.getIsDefault());
        return shipInfo;
    }

    /**
     * Update ShipInfo entity từ request
     */
    public void updateShipInfoFromRequest(ShipInfo shipInfo, ShipInfoRequest request) {
        shipInfo.setReceiver(request.getReceiver());
        shipInfo.setPhone(request.getPhone());
        shipInfo.setAddress(request.getAddress());
        shipInfo.setIsDefault(request.getIsDefault());
    }

    /**
     * Reassign địa chỉ mặc định khi soft delete hoặc xóa địa chỉ default
     */
    public void reassignDefaultAddress(Integer userId, Integer excludeShipInfoId) {
        Optional<ShipInfo> newDefaultCandidate = shipInfoRepository.findByUserId(userId).stream()
                .filter(info -> info.getId() != excludeShipInfoId && !info.getIsDeleted())
                .findFirst();

        newDefaultCandidate.ifPresent(shipInfo -> {
            shipInfo.setIsDefault(true);
            shipInfoRepository.save(shipInfo);
        });
    }

    /**
     * Xử lý reassign default address từ danh sách có sẵn (dành cho hard delete)
     */
    public void handleDefaultAddressReassignment(List<ShipInfo> remainingAddresses) {
        if (!remainingAddresses.isEmpty()) {
            ShipInfo newDefault = remainingAddresses.getFirst();
            newDefault.setIsDefault(true);
            shipInfoRepository.save(newDefault);
        }
    }

    /**
     * Tìm địa chỉ thay thế từ danh sách có sẵn
     */
    public ShipInfo findReplacementShipInfo(List<ShipInfo> availableShipInfos) {
        return availableShipInfos.stream()
                .filter(ShipInfo::getIsDefault)
                .findFirst()
                .orElseGet(() -> availableShipInfos.isEmpty() ? null : availableShipInfos.get(0));
    }

    /**
     * Cập nhật shipInfo cho list orders (có thể tối ưu batch update sau)
     */
    public void updateOrdersShipInfo(List<Order> orders, ShipInfo replacementShipInfo) {
        if (replacementShipInfo != null && !orders.isEmpty()) {
            orders.forEach(order -> order.setShipInfo(replacementShipInfo));
            orderRepository.saveAll(orders);
        }
    }

    /**
     * Kiểm tra xem user có địa chỉ nào khác không (ngoại trừ địa chỉ được exclude)
     */
    public boolean hasOtherAddresses(Integer userId, Integer excludeId) {
        return shipInfoRepository.findByUserId(userId).stream()
                .anyMatch(info -> info.getId() != excludeId && !info.getIsDeleted());
    }
}
