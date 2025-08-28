package com.group7.ecommerce.controller.api;

import com.group7.ecommerce.dto.request.ProductFilterDto;
import com.group7.ecommerce.dto.response.ApiResponse;
import com.group7.ecommerce.dto.response.ProductDetailResponse;
import com.group7.ecommerce.dto.response.ProductListItemResponse;
import com.group7.ecommerce.service.ProductService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
public class GuestController {

	private final ProductService productService;

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
		if (page < 0) page = 0;
		if (size <= 0 || size > 50) size = 12;
		Pageable pageable = PageRequest.of(page, size);
		
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
		return ResponseEntity.ok(ApiResponse.success("Lấy danh sách sản phẩm thành công", result));
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
			// Logger có thể được thêm vào nếu cần
		}
		
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
	
	/**
     * Lấy chi tiết sản phẩm theo ID
     * @param id ID của sản phẩm
     * @return Chi tiết đầy đủ của sản phẩm bao gồm ảnh, category, reviews và thống kê đánh giá
     */
    @GetMapping("/products/{id}")
    // @Operation(summary = "Lấy chi tiết sản phẩm theo ID", 
    //     description = "Trả về đầy đủ thông tin sản phẩm bao gồm ảnh, category, reviews và thống kê đánh giá")
    // @ApiResponses({
    //     @ApiResponse(responseCode = "200", description = "Lấy chi tiết sản phẩm thành công"),
    //     @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm"),
    //     @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    // })
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetail(
            // @Parameter(description = "ID của sản phẩm", required = true)
            @PathVariable Long id) {
        ProductDetailResponse productDetail = productService.getProductDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết sản phẩm thành công", productDetail));
    }
}


