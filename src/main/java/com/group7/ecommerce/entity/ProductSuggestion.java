package com.group7.ecommerce.entity;

import java.time.LocalDateTime;

import com.group7.ecommerce.enums.ProductSuggestionStatus;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product_suggestions")
public class ProductSuggestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "product_name", nullable = false, length = 255)
	private String productName;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "suggested_category", length = 100)
	private String suggestedCategory;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 20, nullable = false)
	private ProductSuggestionStatus status = ProductSuggestionStatus.PENDING;

	@Column(name = "rejection_reason")
	private String rejectionReason;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
