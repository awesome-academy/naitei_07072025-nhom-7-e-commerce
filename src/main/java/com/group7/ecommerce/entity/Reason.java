package com.group7.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reasons")
public class Reason {

    public enum ReasonType { cancel, reject }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false, columnDefinition = "ENUM('cancel','reject')")
    private ReasonType reasonType;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "required_detail", nullable = false)
    private boolean requiredDetail = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
