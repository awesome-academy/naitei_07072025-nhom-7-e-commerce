package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.dto.request.ProductUpdateDto;
import com.group7.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;
    private final MessageSource messageSource;

    @GetMapping("/import")
    public String importProducts() {
        return "/admin/products/import";
    }

    @PostMapping("/import")
    public String importProducts(
            @RequestParam("excelFile") MultipartFile excelFile,
            @RequestParam("imageZip") MultipartFile imageZip,
            RedirectAttributes redirectAttributes,
            Locale locale
    ) {
        try {
            if (excelFile.isEmpty() || imageZip.isEmpty()) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("product.import.error.missing", null, locale));
                return "redirect:/admin/products/import";
            }

            productService.importFromExcelAndZip(excelFile, imageZip);

            redirectAttributes.addFlashAttribute("success",
                    messageSource.getMessage("product.import.success", null, locale));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("product.import.error.general", new Object[]{e.getMessage()}, locale));
        }

        return "redirect:/admin/products/import";
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
