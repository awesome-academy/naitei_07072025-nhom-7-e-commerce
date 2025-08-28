package com.group7.ecommerce.controller.api;

import com.group7.ecommerce.dto.request.OrderRequestItem;
import com.group7.ecommerce.dto.response.CustomerOrderDetailResp;
import com.group7.ecommerce.entity.Order;
import com.group7.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.constraints.Positive;

@RestController("apiOrderController")
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "API quản lý đơn hàng")
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    @Operation(
        summary = "Tạo đơn hàng mới",
        description = "Tạo một đơn hàng mới cho khách hàng"
    )
    public Order createOrder(
            @Parameter(description = "ID của khách hàng", required = true)
            @RequestParam Long userId,
            @Parameter(description = "ID thông tin giao hàng", required = true)
            @RequestParam int shipInfoId,
            @Parameter(description = "Phương thức thanh toán", required = true)
            @RequestParam String paymentMethod,
            @Parameter(description = "Danh sách sản phẩm đặt hàng", required = true)
            @RequestBody List<OrderRequestItem> items) {
        return orderService.createOrder(userId, shipInfoId, paymentMethod, items);
    }

    @GetMapping("/user/{orderId}")
    @Operation(
        summary = "Xem chi tiết đơn hàng cho khách hàng",
        description = "Lấy thông tin chi tiết đơn hàng của khách hàng bao gồm thông tin giao hàng, sản phẩm và trạng thái"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lấy thông tin đơn hàng thành công",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomerOrderDetailResp.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Không tìm thấy đơn hàng",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.group7.ecommerce.dto.response.ApiResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Không có quyền truy cập đơn hàng này",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.group7.ecommerce.dto.response.ApiResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Lỗi hệ thống",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.group7.ecommerce.dto.response.ApiResponse.class)
            )
        )
    })
    public ResponseEntity<CustomerOrderDetailResp> getCustomerOrderDetail(
            @Parameter(description = "ID của khách hàng", required = true)
            @RequestParam @Positive Long userId,
            
            @Parameter(description = "ID của đơn hàng", required = true)
            @PathVariable @Positive Integer orderId) {
        
        CustomerOrderDetailResp orderDetail = orderService.getCustomerOrderDetail(userId, orderId);
        return ResponseEntity.ok(orderDetail);
    }
}
