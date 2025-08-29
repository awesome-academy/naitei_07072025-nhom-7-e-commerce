package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.dto.request.ProductUpdateDto;
import com.group7.ecommerce.dto.response.CategoryResponse;
import com.group7.ecommerce.dto.response.ProductResponse;
import com.group7.ecommerce.service.CategoryService;
import com.group7.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
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

    @GetMapping("/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        ProductResponse product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/products/detail";
    }

    @GetMapping("update/{id}")
    public String updateProduct(@PathVariable Long id, Model model) {
        ProductResponse product = productService.getProductById(id);
        List<CategoryResponse> categories = categoryService.getAllCategories();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "admin/products/update";
    }

    @PatchMapping("update/info/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute @Valid ProductUpdateDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        List<CategoryResponse> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        if (bindingResult.hasErrors()) {
            Map<String, String> fieldErrors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            System.out.println(fieldErrors);
            model.addAttribute("fieldErrors", fieldErrors);
            ProductResponse product = productService.getProductById(id);
            model.addAttribute("product", product);
            return "admin/products/update";
        }
        productService.updateProduct(id, dto);
        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        return "redirect:/admin/products/" + id;
    }

    @PatchMapping(value = "update/img/{id}")
    public String updateImageProduct(
            @PathVariable Long id,
            @RequestParam(value = "images") MultipartFile[] images,
            RedirectAttributes redirectAttributes) {
        try {
            productService.updateImageProduct(id, images);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/products/update/" + id;
        }
        return "redirect:/admin/products/" + id;
    }
}
