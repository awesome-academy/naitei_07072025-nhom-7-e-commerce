package com.group7.ecommerce.controller.api;

import com.group7.ecommerce.dto.response.FeaturedProductResponse;
import com.group7.ecommerce.service.FeaturedProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
@Tag(name = "Guest API", description = "API dành cho khách không đăng nhập")
public class GuestController {

    private final FeaturedProductService featuredProductService;


    @GetMapping("/featured-products")
    @Operation(
            summary = "Lấy danh sách sản phẩm nổi bật",
            description = "Trả về danh sách sản phẩm nổi bật dựa trên số lượng đơn hàng đã bán ra. " +
                    "Chỉ tính các đơn hàng đã hoàn thành (DELIVERED)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách sản phẩm nổi bật thành công"),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<List<FeaturedProductResponse>> getFeaturedProducts(
            @Parameter(description = "Số trang (bắt đầu từ 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Kích thước trang (tối đa 50)")
            @RequestParam(defaultValue = "10") int size) {

        log.info("API getFeaturedProducts được gọi với page: {}, size: {}", page, size);

        if (page < 0) {
            log.warn("Tham số page không hợp lệ: {}", page);
            page = 0;
        }

        if (size <= 0 || size > 50) {
            log.warn("Tham số size không hợp lệ: {}", size);
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size);
        List<FeaturedProductResponse> featuredProducts = featuredProductService.getFeaturedProducts(pageable);

        log.info("Trả về {} sản phẩm nổi bật", featuredProducts.size());
        return ResponseEntity.ok(featuredProducts);
    }

    @GetMapping("/featured-products/top")
    @Operation(
            summary = "Lấy top sản phẩm nổi bật",
            description = "Trả về top N sản phẩm nổi bật dựa trên số lượng đơn hàng đã bán ra"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy top sản phẩm nổi bật thành công"),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<List<FeaturedProductResponse>> getTopFeaturedProducts(
            @Parameter(description = "Số lượng sản phẩm cần lấy (tối đa 20)")
            @RequestParam(defaultValue = "8") int limit) {

        log.info("API getTopFeaturedProducts được gọi với limit: {}", limit);

        // Validate parameter
        if (limit <= 0 || limit > 20) {
            log.warn("Tham số limit không hợp lệ: {}", limit);
            limit = 8;
        }

        List<FeaturedProductResponse> topFeaturedProducts = featuredProductService.getTopFeaturedProducts(limit);

        log.info("Trả về {} top sản phẩm nổi bật", topFeaturedProducts.size());
        return ResponseEntity.ok(topFeaturedProducts);
    }
}
