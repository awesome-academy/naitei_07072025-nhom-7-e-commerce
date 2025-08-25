package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.dto.request.UpdateProfileRequest;
import com.group7.ecommerce.dto.response.JwtResponse;
import com.group7.ecommerce.dto.response.ShowProfileResponse;
import com.group7.ecommerce.dto.response.UpdateProfileResponse;
import com.group7.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

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

    @GetMapping("/info/edit")
    public String showEditProfile(HttpServletRequest request, Model model, Locale locale) {
        log.info("Accessing admin edit profile page");

        JwtResponse currentUser = (JwtResponse) request.getAttribute("currentUser");
        if (currentUser == null) {
            log.warn("Unauthorized access to admin edit profile");
            return "redirect:/auth/login";
        }

        try {
            ShowProfileResponse showProfileResponse = userService.showProfileAdmin(currentUser);

            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(
                    showProfileResponse.user().getEmail(),
                    showProfileResponse.user().getFullName(),
                    showProfileResponse.user().getPhone(),
                    showProfileResponse.user().getAddress()
            );

            model.addAttribute("currentUser", currentUser);
            model.addAttribute("profileUser", showProfileResponse);
            model.addAttribute("updateProfileRequest", updateProfileRequest);

            log.info("Admin edit profile page loaded for: {}", currentUser.getUsername());
            return "admin/info/edit";

        } catch (Exception e) {
            log.error("Error loading admin edit profile for username: {}", currentUser.getUsername(), e);
            model.addAttribute("error",
                    messageSource.getMessage("admin.profile.error.message", null, locale));
            return "admin/info/edit";
        }
    }

    @PostMapping("/info/update")
    public String updateProfile(@Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest request,
                                BindingResult bindingResult,
                                HttpServletRequest httpRequest,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                Locale locale) {
        log.info("Processing admin profile update");

        JwtResponse currentUser = (JwtResponse) httpRequest.getAttribute("currentUser");
        if (currentUser == null) {
            log.warn("Unauthorized access to update admin profile");
            return "redirect:/auth/login";
        }

        // Nếu có lỗi validation
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in profile update for user: {}", currentUser.getUsername());

            try {
                ShowProfileResponse showProfileResponse = userService.showProfileAdmin(currentUser);
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("profileUser", showProfileResponse);
                model.addAttribute("updateProfileRequest", request);

                return "admin/info/edit";
            } catch (Exception e) {
                log.error("Error reloading profile data after validation error", e);
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("admin.profile.error.message", null, locale));
                return "redirect:/admin/info";
            }
        }

        try {
            UpdateProfileResponse response = userService.updateProfileAdmin(currentUser, request);

            if (response.success()) {
                log.info("Profile updated successfully for user: {}", currentUser.getUsername());
                redirectAttributes.addFlashAttribute("success", response.message());
                return "redirect:/admin/info";
            } else {
                log.warn("Profile update failed for user: {}", currentUser.getUsername());
                model.addAttribute("error", response.message());

                // Reload data for edit form
                ShowProfileResponse showProfileResponse = userService.showProfileAdmin(currentUser);
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("profileUser", showProfileResponse);
                model.addAttribute("updateProfileRequest", request);

                return "admin/info/edit";
            }

        } catch (Exception e) {
            log.error("Error updating admin profile for username: {}", currentUser.getUsername(), e);
            model.addAttribute("error",
                    messageSource.getMessage("admin.profile.update.error", null, locale));

            try {
                ShowProfileResponse showProfileResponse = userService.showProfileAdmin(currentUser);
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("profileUser", showProfileResponse);
                model.addAttribute("updateProfileRequest", request);

                return "admin/info/edit";
            } catch (Exception ex) {
                log.error("Error reloading profile data after update error", ex);
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("admin.profile.error.message", null, locale));
                return "redirect:/admin/info";
            }
        }
    }

}
