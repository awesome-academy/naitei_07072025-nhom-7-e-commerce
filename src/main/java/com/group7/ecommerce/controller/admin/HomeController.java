package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.dto.response.JwtResponse;
import com.group7.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final UserService userService;
    private final MessageSource messageSource;

    @GetMapping("/home")
    public String admin(HttpServletRequest request, Model model) {
        log.info("Accessing admin home page");

        JwtResponse currentUser = (JwtResponse) request.getAttribute("currentUser");
        String userName = (String) request.getAttribute("userName");

        // Thêm vào model
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userName", userName);

        return "admin/index";
    }
}
