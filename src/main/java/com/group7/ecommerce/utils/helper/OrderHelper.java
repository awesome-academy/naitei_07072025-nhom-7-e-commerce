package com.group7.ecommerce.utils.helper;

import com.group7.ecommerce.enums.OrderStatus;
import com.group7.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderHelper {

    private final OrderRepository orderRepository;

    /**
     * Đếm tổng số đơn hàng
     */
    public long getTotalOrders() {
        try {
            return orderRepository.count();
        } catch (Exception e) {
            log.error("Error getting total orders count", e);
            return 0L;
        }
    }

    /**
     * Tính tổng doanh thu
     */
    public BigDecimal getTotalRevenue() {
        try {
            BigDecimal revenue = orderRepository.sumTotalAmountByStatus(OrderStatus.DELIVERED);
            return revenue != null ? revenue : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("Error calculating total revenue", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Đếm số đơn hàng đang chờ xử lý
     */
    public long countPendingOrders() {
        try {
            return orderRepository.countByStatus(OrderStatus.PENDING);
        } catch (Exception e) {
            log.error("Error counting pending orders", e);
            return 0L;
        }
    }
}
