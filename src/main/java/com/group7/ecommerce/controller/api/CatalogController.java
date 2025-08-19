package com.group7.ecommerce.controller.api;

import com.group7.ecommerce.dto.response.ApiResponse;
import com.group7.ecommerce.dto.response.FeaturedProductResponse;
import com.group7.ecommerce.dto.response.ProductListItemResponse;
import com.group7.ecommerce.service.FeaturedProductService;
import com.group7.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@Tag(name = "Catalog API", description = "API hiển thị sản phẩm cho khách (public)")
public class CatalogController {
    
    private final FeaturedProductService featuredProductService;
    private final ProductService productService;
    private final MessageSource messageSource;
    
    
    @GetMapping("/featured-products")
    @Operation(
        summary = "openapi.guest.featured.summary",
        description = "openapi.guest.featured.description"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "openapi.response.200"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "openapi.response.400"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "openapi.response.500")
    })
    public ResponseEntity<ApiResponse<List<FeaturedProductResponse>>> getFeaturedProducts(
            @Parameter(description = "openapi.guest.featured.param.page")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "openapi.guest.featured.param.size")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("API getFeaturedProducts được gọi với page: {}, size: {}", page, size);
        
        if (page < 0) {
            log.warn("Tham số page không hợp lệ: {}", page);
            throw new IllegalArgumentException("error.param.page.invalid");
        }

        if (size <= 0 || size > 50) {
            log.warn("Tham số size không hợp lệ: {}", size);
            throw new IllegalArgumentException("error.param.size.invalid");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        List<FeaturedProductResponse> featuredProducts = featuredProductService.getFeaturedProducts(pageable);
        String msg = messageSource.getMessage("catalog.featured.success", null, LocaleContextHolder.getLocale());
        log.info("Trả về {} sản phẩm nổi bật", featuredProducts.size());
        return ResponseEntity.ok(ApiResponse.success(msg, featuredProducts));
    }
    
    @GetMapping("/featured-products/top")
    @Operation(
        summary = "openapi.guest.featured.top.summary",
        description = "openapi.guest.featured.top.description"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "openapi.response.200"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "openapi.response.400"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "openapi.response.500")
    })
    public ResponseEntity<ApiResponse<List<FeaturedProductResponse>>> getTopFeaturedProducts(
            @Parameter(description = "openapi.guest.featured.top.param.limit")
            @RequestParam(defaultValue = "8") int limit) {
        
        log.info("API getTopFeaturedProducts được gọi với limit: {}", limit);
        
        // Validate parameter
        if (limit <= 0 || limit > 20) {
            log.warn("Tham số limit không hợp lệ: {}", limit);
            throw new IllegalArgumentException("error.param.limit.invalid");
        }
        
        List<FeaturedProductResponse> topFeaturedProducts = featuredProductService.getTopFeaturedProducts(limit);
        String msg = messageSource.getMessage("catalog.featured.top.success", null, LocaleContextHolder.getLocale());
        log.info("Trả về {} top sản phẩm nổi bật", topFeaturedProducts.size());
        return ResponseEntity.ok(ApiResponse.success(msg, topFeaturedProducts));
    }

    @GetMapping("/products")
    @Operation(
        summary = "openapi.catalog.products.summary",
        description = "openapi.catalog.products.description"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "openapi.response.200"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "openapi.response.400"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "openapi.response.500")
    })
    public ResponseEntity<ApiResponse<Page<ProductListItemResponse>>> getAllProducts(
            @Parameter(description = "openapi.catalog.products.param.page")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "openapi.catalog.products.param.size")
            @RequestParam(defaultValue = "12") int size
    ) {
        if (page < 0) {
            throw new IllegalArgumentException("error.param.page.invalid");
        }
        if (size <= 0 || size > 50) {
            throw new IllegalArgumentException("error.param.size.invalid");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductListItemResponse> result = productService.getAllProducts(pageable);
        String msg = messageSource.getMessage("openapi.catalog.products.summary", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(ApiResponse.success(msg, result));
    }
}


