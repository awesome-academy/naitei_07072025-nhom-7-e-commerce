package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.dto.request.ProductUpdateDto;
import com.group7.ecommerce.dto.response.ProductResponse;
import com.group7.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/import")
    public String importProducts() {
        return "/admin/products/import";
    }

    @PostMapping("/import")
    public String importProducts(
            @RequestParam("excelFile") MultipartFile excelFile,
            @RequestParam("imageZip") MultipartFile imageZip,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (excelFile.isEmpty() || imageZip.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn đủ file Excel và Zip ảnh!");
                return "redirect:/admin/products/import";
            }
            productService.importFromExcelAndZip(excelFile, imageZip);

            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products/import";
    }

    @GetMapping
    public String getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            Model model
    ) {
        List<Integer> validSizes = List.of(10, 25, 50, 100);

        if (!validSizes.contains(size)) {
            size = 10;
        }

        Page<ProductResponse> products;

        if (sortField == null || sortField.isBlank()) {
            products = productService.getAllPaged(page, size);
        } else {
            products = productService.getAllPagedAndSorted(page, size, sortField, sortDirection);
        }

        model.addAttribute("products", products.getContent());
        model.addAttribute("currentPage", products.getNumber());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        return "admin/products/index";
    }

    @PutMapping(value = "/info/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute @Valid ProductUpdateDto dto) {
        productService.updateProduct(id, dto);
        return "Cập nhật thành công";
    }

    @PutMapping(value = "/img/{id}")
    public String updateImageProduct(
            @PathVariable Long id,
            @RequestParam(value = "images") MultipartFile[] images) {
        productService.updateImageProduct(id, images);
        return "Cập nhật ảnh sản phẩm thành công";
    }
}
