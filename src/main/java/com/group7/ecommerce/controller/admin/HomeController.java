package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.dto.response.JwtResponse;
import com.group7.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import com.group7.ecommerce.dto.response.ShowProfileResponse;
import com.group7.ecommerce.service.OrderService;
import com.group7.ecommerce.service.ShipInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final UserService userService;
    private final OrderService orderService;
    private final ShipInfoService shipInfoService;
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

    @GetMapping("/info")
    public String showProfile(HttpServletRequest request, Model model, Locale locale) {
        log.info("Accessing admin profile page");

        JwtResponse currentUser = (JwtResponse) request.getAttribute("currentUser");
        if (currentUser == null) {
            log.warn("Unauthorized access to admin profile");
            return "redirect:/auth/login";
        }

        try {
            ShowProfileResponse showProfileResponse = userService.showProfileAdmin(currentUser);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("profileUser", showProfileResponse);

            log.info("Admin profile loaded successfully for: {}", currentUser.getUsername());
            return "admin/info/index"; // Trả về template chính xác

        } catch (Exception e) {
            log.error("Error loading admin profile for username: {}", currentUser.getUsername(), e);
            model.addAttribute("error",
                    messageSource.getMessage("admin.profile.error.message", null, locale));
            return "admin/info/index";
        }
    }

}
