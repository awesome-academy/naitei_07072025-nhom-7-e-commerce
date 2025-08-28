package com.group7.ecommerce.controller.api;

import com.group7.ecommerce.dto.request.CreateOrderRequest;
import com.group7.ecommerce.dto.request.UpdatePaymentMethodRequest;
import com.group7.ecommerce.dto.response.ApiResponse;
import com.group7.ecommerce.dto.response.OrderDetailResp;
import com.group7.ecommerce.dto.response.OrderSummaryResp;
import com.group7.ecommerce.service.OrderService;
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

@RestController("apiOrderController")
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MessageSource messageSource;

    /**
     * Tạo đơn hàng từ danh sách sản phẩm cụ thể (mua ngay)
     */
    @PostMapping("/create-direct")
    public ResponseEntity<ApiResponse<OrderDetailResp>> createDirectOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        OrderDetailResp orderDetail = orderService.createDirectOrder(authentication, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(getMessage("order.create.success"), orderDetail));
    }

    /**
     * Helper method để lấy localized message
     */
    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }
}
