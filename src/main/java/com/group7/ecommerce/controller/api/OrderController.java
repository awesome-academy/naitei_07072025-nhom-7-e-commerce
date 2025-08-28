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
     //     * Tạo đơn hàng từ giỏ hàng
     //     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderDetailResp>> createOrderFromCart(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        OrderDetailResp orderDetail = orderService.createOrderFromCart(authentication, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(getMessage("order.create.success"), orderDetail));
    }

    /**
     * Lấy thông tin chi tiết đơn hàng
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResp>> getOrderDetail(
            @PathVariable int orderId,
            Authentication authentication) {
        OrderDetailResp orderDetail = orderService.getOrderDetail(authentication, orderId);
        return ResponseEntity.ok(ApiResponse.success(
                getMessage("order.get.success"), orderDetail));
    }

    /**
     * Lấy danh sách đơn hàng của user
     */
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderSummaryResp>>> getMyOrders(
            Authentication authentication) {
        List<OrderSummaryResp> orders = orderService.getUserOrders(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                getMessage("orders.get.success"), orders));
    }

    /**
     * Lấy danh sách phương thức thanh toán có sẵn
     */
    @GetMapping("/payment-methods")
    public ResponseEntity<ApiResponse<List<String>>> getPaymentMethods() {
        List<String> paymentMethods = orderService.getAvailablePaymentMethods();
        return ResponseEntity.ok(ApiResponse.success(
                getMessage("payment.methods.success"), paymentMethods));
    }



    /**
     * Helper method để lấy localized message
     */
    private String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }
}
