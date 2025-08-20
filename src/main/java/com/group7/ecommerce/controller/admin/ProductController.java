package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.dto.request.ProductUpdateDto;
import com.group7.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/import")
    public ResponseEntity<?> importProducts(
            @RequestParam("excelFile") MultipartFile excelFile,
            @RequestParam("imageZip") MultipartFile imageZip) {
        productService.importFromExcelAndZip(excelFile, imageZip);
        return ResponseEntity.ok("Import thành công");
    }

    @PutMapping(value = "/info/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @ModelAttribute @Valid ProductUpdateDto dto) {
        productService.updateProduct(id, dto);
        return ResponseEntity.ok("Cập nhật sản phẩm thành công");
    }

    @PutMapping(value = "/img/{id}")
    public ResponseEntity<?> updateImageProduct(
            @PathVariable Long id,
            @RequestParam(value = "images") MultipartFile[] images) {
        productService.updateImageProduct(id, images);
        return ResponseEntity.ok("Cập nhật ảnh sản phẩm thành công");
    }
}
