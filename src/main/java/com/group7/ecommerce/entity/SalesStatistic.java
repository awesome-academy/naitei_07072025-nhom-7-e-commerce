package com.group7.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sales_statistics")
public class SalesStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(name = "total_sales", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSales;

    @Column(name = "total_profit", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalProfit;

    @Column(name = "total_orders", nullable = false)
    private int totalOrders;

    @ManyToOne
    @JoinColumn(name = "best_selling_product_id")
    private Product bestSellingProduct;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
