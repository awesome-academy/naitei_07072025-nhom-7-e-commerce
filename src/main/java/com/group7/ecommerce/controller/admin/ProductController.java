package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products/import")
    public ResponseEntity<?> importProducts(
            @RequestParam("excelFile") MultipartFile excelFile,
            @RequestParam("imageZip") MultipartFile imageZip) {
        productService.importFromExcelAndZip(excelFile, imageZip);
        return ResponseEntity.ok("Import thành công");
    }
}
