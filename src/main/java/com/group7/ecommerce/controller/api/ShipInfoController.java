package com.group7.ecommerce.controller.api;

import com.group7.ecommerce.dto.request.ShipInfoRequest;
import com.group7.ecommerce.dto.response.ApiResponse;
import com.group7.ecommerce.dto.response.ShipInfoResponse;
import com.group7.ecommerce.service.ShipInfoService;
import com.group7.ecommerce.utils.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/ship-info")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ShipInfoController {

    private final ShipInfoService shipInfoService;
    private final MessageSource messageSource;

    /**
     * Lấy danh sách địa chỉ giao hàng của user hiện tại
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShipInfoResponse>>> getShipInfoList(Authentication authentication) {
        List<ShipInfoResponse> shipInfos = shipInfoService.getShipInfoByUserId(authentication);

        return ResponseEntity.ok(ApiResponse.success(
                getMessage("shipinfo.get.list.success"), shipInfos));
    }

    /**
     * Lấy địa chỉ giao hàng mặc định
     */
    @GetMapping("/default")
    public ResponseEntity<ApiResponse<ShipInfoResponse>> getDefaultShipInfo(Authentication authentication) {
        ShipInfoResponse defaultShipInfo = shipInfoService.getDefaultShipInfo(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                getMessage("shipinfo.get.default.success"), defaultShipInfo));
    }

    /**
     * Tạo mới thông tin giao hàng
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ShipInfoResponse>> createShipInfo(
            @Valid @RequestBody ShipInfoRequest request,
            Authentication authentication) {
        ShipInfoResponse shipInfo = shipInfoService.createShipInfo(authentication, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(getMessage("shipinfo.create.success"), shipInfo));
    }

    /**
     * Cập nhật thông tin giao hàng
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipInfoResponse>> updateShipInfo(
            @PathVariable Integer id,
            @Valid @RequestBody ShipInfoRequest request,
            Authentication authentication) {
        ShipInfoResponse shipInfo = shipInfoService.updateShipInfo(authentication, id, request);
        return ResponseEntity.ok(ApiResponse.success(
                getMessage("shipinfo.update.success"), shipInfo));
    }

    /**
     * Xóa thông tin giao hàng
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShipInfo(
            @PathVariable Integer id,
            Authentication authentication) {
        shipInfoService.deleteShipInfo(authentication, id);

        return ResponseEntity.ok(ApiResponse.success(
                getMessage("shipinfo.delete.success"), null));
    }

    /**
     * Helper method để lấy localized message
     */
    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }
}
