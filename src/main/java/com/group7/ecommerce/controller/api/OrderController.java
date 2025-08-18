package com.group7.ecommerce.controller.api;

import com.group7.ecommerce.dto.request.OrderRequestItem;
import com.group7.ecommerce.entity.Order;
import com.group7.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("apiOrderController")
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public Order createOrder(
            @RequestParam Long userId,
            @RequestParam int shipInfoId,
            @RequestParam String paymentMethod,
            @RequestBody List<OrderRequestItem> items) {
        return orderService.createOrder(userId, shipInfoId, paymentMethod, items);
    }
}
