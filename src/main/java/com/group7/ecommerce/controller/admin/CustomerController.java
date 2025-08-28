package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.dto.response.CustomerResponse;
import com.group7.ecommerce.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public String getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        List<Integer> validSizes = List.of(10, 25, 50, 100);

        if (!validSizes.contains(size)) {
            size = 10;
        }

        Page<CustomerResponse> customers = customerService.getAllPaged(page, size);

        model.addAttribute("customers", customers.getContent());
        model.addAttribute("currentPage", customers.getNumber());
        model.addAttribute("totalPages", customers.getTotalPages());
        model.addAttribute("size", size);

        return "admin/customers/index";
    }
}
