package com.group7.ecommerce.controller.api;

import com.group7.ecommerce.dto.request.ProductFilterDto;
import com.group7.ecommerce.dto.response.ApiResponse;
import com.group7.ecommerce.dto.response.ProductListItemResponse;
import com.group7.ecommerce.service.ProductService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
// @Tag(name = "Guest API", description = "API dành cho khách không đăng nhập")
@Slf4j
public class GuestController {

	private final ProductService productService;
	private final MessageSource messageSource;

	private String msg(String key) {
		return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
	}

	@GetMapping("/products")
	// @Operation(summary = "Lấy toàn bộ sản phẩm với filter động (có phân trang)",
	//     description = "Trả về danh sách sản phẩm còn hoạt động với khả năng filter theo nhiều tiêu chí. Hỗ trợ phân trang để tối ưu hiệu suất.")
	// @ApiResponses({
	//     @ApiResponse(responseCode = "200", description = "Lấy danh sách sản phẩm thành công"),
	//     @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ"),
	//     @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
	// })
	public ResponseEntity<ApiResponse<Page<ProductListItemResponse>>> getAllProducts(
			// Phân trang
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "12") int size,
			
			// Filter theo tên và mô tả
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String description,
			
			// Filter theo giá bán
			@RequestParam(required = false) BigDecimal minSellingPrice,
			@RequestParam(required = false) BigDecimal maxSellingPrice,
			
			// Filter theo giá nhập
			@RequestParam(required = false) BigDecimal minImportPrice,
			@RequestParam(required = false) BigDecimal maxImportPrice,
			
			// Filter theo số lượng tồn kho
			@RequestParam(required = false) Integer minStockQuantity,
			@RequestParam(required = false) Integer maxStockQuantity,
			
			// Filter theo category
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) String categoryName,
			
			// Filter theo trạng thái nổi bật
			@RequestParam(required = false) Boolean isFeatured,
			
			// Filter theo thời gian tạo (format: yyyy-MM-dd'T'HH:mm:ss)
			@RequestParam(required = false) String createdAfter,
			@RequestParam(required = false) String createdBefore,
			
			// Filter theo thời gian cập nhật (format: yyyy-MM-dd'T'HH:mm:ss)
			@RequestParam(required = false) String updatedAfter,
			@RequestParam(required = false) String updatedBefore,
			
			// Tham số sắp xếp
			@RequestParam(required = false, defaultValue = "createdAt") String sortBy,
			@RequestParam(required = false, defaultValue = "desc") String sortDirection
	) {
		// Validate và chuẩn hóa tham số phân trang
		if (page < 0) {
			throw new IllegalArgumentException(msg("error.pagination.page_negative"));
		}
		if (size <= 0 || size > 50) {
			throw new IllegalArgumentException(msg("error.pagination.size_out_of_range"));
		}
		Pageable pageable = PageRequest.of(page, size);
		
		// Validate các tham số số học cần thiết
		// Giá bán không âm
		if (minSellingPrice != null && minSellingPrice.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException(msg("error.sellingPrice.min_negative"));
		}
		if (maxSellingPrice != null && maxSellingPrice.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException(msg("error.sellingPrice.max_negative"));
		}
		// Giá nhập không âm
		if (minImportPrice != null && minImportPrice.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException(msg("error.importPrice.min_negative"));
		}
		if (maxImportPrice != null && maxImportPrice.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException(msg("error.importPrice.max_negative"));
		}
		// Tồn kho không âm
		if (minStockQuantity != null && minStockQuantity < 0) {
			throw new IllegalArgumentException(msg("error.stockQuantity.min_negative"));
		}
		if (maxStockQuantity != null && maxStockQuantity < 0) {
			throw new IllegalArgumentException(msg("error.stockQuantity.max_negative"));
		}
		// Kiểm tra cặp min <= max
		if (minSellingPrice != null && maxSellingPrice != null && minSellingPrice.compareTo(maxSellingPrice) > 0) {
			throw new IllegalArgumentException(msg("error.sellingPrice.min_gt_max"));
		}
		if (minImportPrice != null && maxImportPrice != null && minImportPrice.compareTo(maxImportPrice) > 0) {
			throw new IllegalArgumentException(msg("error.importPrice.min_gt_max"));
		}
		if (minStockQuantity != null && maxStockQuantity != null && minStockQuantity > maxStockQuantity) {
			throw new IllegalArgumentException(msg("error.stockQuantity.min_gt_max"));
		}
		// CategoryId phải dương nếu được truyền
		if (categoryId != null && categoryId <= 0) {
			throw new IllegalArgumentException(msg("error.category.id_invalid"));
		}
		
		// Tạo ProductFilterDto từ các tham số
		ProductFilterDto filterDto = createFilterDto(
			name, description, minSellingPrice, maxSellingPrice,
			minImportPrice, maxImportPrice, minStockQuantity, maxStockQuantity,
			categoryId, categoryName, isFeatured,
			createdAfter, createdBefore, updatedAfter, updatedBefore,
			sortBy, sortDirection
		);
		
		// Gọi service với filter
		Page<ProductListItemResponse> result = productService.getAllProducts(filterDto, pageable);
		String successMessage = messageSource.getMessage("success.product.list.fetch", null, LocaleContextHolder.getLocale());
		return ResponseEntity.ok(ApiResponse.success(successMessage, result));
	}
	
	/**
	 * Tạo ProductFilterDto từ các tham số request
	 */
	private ProductFilterDto createFilterDto(
			String name, String description, BigDecimal minSellingPrice, BigDecimal maxSellingPrice,
			BigDecimal minImportPrice, BigDecimal maxImportPrice, Integer minStockQuantity, Integer maxStockQuantity,
			Long categoryId, String categoryName, Boolean isFeatured,
			String createdAfter, String createdBefore, String updatedAfter, String updatedBefore,
			String sortBy, String sortDirection
	) {
		// Parse thời gian
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		
		LocalDateTime parsedCreatedAfter = null;
		LocalDateTime parsedCreatedBefore = null;
		LocalDateTime parsedUpdatedAfter = null;
		LocalDateTime parsedUpdatedBefore = null;
		
		try {
			if (createdAfter != null && !createdAfter.trim().isEmpty()) {
				parsedCreatedAfter = LocalDateTime.parse(createdAfter, formatter);
			}
			if (createdBefore != null && !createdBefore.trim().isEmpty()) {
				parsedCreatedBefore = LocalDateTime.parse(createdBefore, formatter);
			}
			if (updatedAfter != null && !updatedAfter.trim().isEmpty()) {
				parsedUpdatedAfter = LocalDateTime.parse(updatedAfter, formatter);
			}
			if (updatedBefore != null && !updatedBefore.trim().isEmpty()) {
				parsedUpdatedBefore = LocalDateTime.parse(updatedBefore, formatter);
			}
		} catch (Exception e) {
			// Log lỗi parse thời gian, nhưng không throw exception để không ảnh hưởng đến filter khác
			log.warn("Invalid datetime format for filters: createdAfter={}, createdBefore={}, updatedAfter={}, updatedBefore={}",
					createdAfter, createdBefore, updatedAfter, updatedBefore);
		}
		// Không validate khoảng thời gian to/from theo yêu cầu: chỉ log nếu parse lỗi
		
		// Validate và chuẩn hóa tham số sắp xếp
		String validSortBy = validateSortBy(sortBy);
		String validSortDirection = validateSortDirection(sortDirection);
		
		// Tạo record với constructor
		return new ProductFilterDto(
			name, description, minSellingPrice, maxSellingPrice,
			minImportPrice, maxImportPrice, minStockQuantity, maxStockQuantity,
			categoryId, categoryName, isFeatured,
			parsedCreatedAfter, parsedCreatedBefore, parsedUpdatedAfter, parsedUpdatedBefore,
			validSortBy, validSortDirection
		);
	}
	
	/**
	 * Validate trường sắp xếp
	 */
	private String validateSortBy(String sortBy) {
		if (sortBy == null || sortBy.trim().isEmpty()) {
			return "createdAt";
		}
		
		// Danh sách các trường được phép sắp xếp
		return switch (sortBy.toLowerCase()) {
			case "name" -> "name";
			case "sellingprice", "selling_price" -> "sellingPrice";
			case "importprice", "import_price" -> "importPrice";
			case "stockquantity", "stock_quantity" -> "stockQuantity";
			case "createdat", "created_at" -> "createdAt";
			case "updatedat", "updated_at" -> "updatedAt";
			default -> "createdAt"; // Mặc định nếu trường không hợp lệ
		};
	}
	
	/**
	 * Validate hướng sắp xếp
	 */
	private String validateSortDirection(String sortDirection) {
		if (sortDirection == null || sortDirection.trim().isEmpty()) {
			return "desc";
		}
		
		return switch (sortDirection.toLowerCase()) {
			case "asc", "ascending" -> "asc";
			case "desc", "descending" -> "desc";
			default -> "desc"; // Mặc định nếu hướng không hợp lệ
		};
	}
}


