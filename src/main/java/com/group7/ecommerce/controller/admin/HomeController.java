package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.dto.request.UpdateProfileRequest;
import com.group7.ecommerce.dto.request.ChangePasswordDto;
import com.group7.ecommerce.dto.response.JwtResponse;
import com.group7.ecommerce.dto.response.ShowProfileResponse;
import com.group7.ecommerce.dto.response.UpdateProfileResponse;
import com.group7.ecommerce.service.UserService;
import com.group7.ecommerce.utils.helper.ChangePasswordHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
    private final ChangePasswordHelper changePasswordHelper;

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

        try {
            ShowProfileResponse showProfileResponse = userService.showProfileAdmin(currentUser);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("profileUser", showProfileResponse);

            if (!model.containsAttribute("changePasswordDto")) {
                model.addAttribute("changePasswordDto", new ChangePasswordDto("", "", ""));
            }

            log.info("Admin profile loaded successfully for: {}", currentUser.getUsername());

        } catch (Exception e) {
            log.error("Error loading admin profile for username: {}", currentUser.getUsername(), e);
            model.addAttribute("error",
                    messageSource.getMessage("admin.profile.error.message", null, locale));
        }
        return "admin/info/index";
    }

    @GetMapping("/info/edit")
    public String showEditProfile(HttpServletRequest request, Model model, Locale locale) {
        log.info("Accessing admin edit profile page");

        JwtResponse currentUser = (JwtResponse) request.getAttribute("currentUser");

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
        } catch (Exception e) {
            log.error("Error loading admin edit profile for username: {}", currentUser.getUsername(), e);
            model.addAttribute("error",
                    messageSource.getMessage("admin.profile.error.message", null, locale));
        }
        return "admin/info/edit";
    }

    @PatchMapping("/info/update")
    public String updateProfile(@Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest request,
                                BindingResult bindingResult,
                                HttpServletRequest httpRequest,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                Locale locale) {
        log.info("Processing admin profile update");

        JwtResponse currentUser = (JwtResponse) httpRequest.getAttribute("currentUser");

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
            }

            log.warn("Profile update failed for user: {}", currentUser.getUsername());
            model.addAttribute("error", response.message());

            // Reload data for edit form
            ShowProfileResponse showProfileResponse = userService.showProfileAdmin(currentUser);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("profileUser", showProfileResponse);
            model.addAttribute("updateProfileRequest", request);

            return "admin/info/edit";

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

    // ========== CHỨC NĂNG ĐỔI MẬT KHẨU ==========
    @PatchMapping("/change-password")
    public String processChangePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto dto,
                                        BindingResult bindingResult,
                                        Model model,
                                        RedirectAttributes redirectAttributes,
                                        HttpServletRequest request,
                                        Locale locale) {

        JwtResponse currentUser = (JwtResponse) request.getAttribute("currentUser");
        String currentUserName = currentUser.getUsername();
        log.info("Processing password change for user: {}", currentUserName);

        // Check password match manually
        if (!dto.newPassword().equals(dto.confirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "password.mismatch",
                    "Passwords do not match");
        }

        // Validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Password change validation failed for user: {}", currentUserName);

            bindingResult.getAllErrors().forEach(error -> {
                log.debug("Validation error - Field: {}, Message: {}",
                        error instanceof FieldError ? ((FieldError) error).getField() : "global",
                        error.getDefaultMessage());
            });

            // Load profile data and return with errors
            changePasswordHelper.loadProfileDataWithErrors(request, model, locale, dto, null);
            return "admin/info/index";
        }

        try {
            userService.changePassword(currentUser.getEmail(), dto, locale);
            log.info("Password change successful for user: {}", currentUserName);

            String successMessage = messageSource.getMessage("auth.change.password.success", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

            return "redirect:/admin/info";

        } catch (Exception e) {
            log.error("Password change failed for user: {} - Error: {}", currentUserName, e.getMessage());

            String errorMessage;
            if (e.getMessage().contains("Current password is incorrect")) {
                errorMessage = "Current password is incorrect. Please try again.";
            } else {
                errorMessage = e.getMessage();
            }
            changePasswordHelper.loadProfileDataWithErrors(request, model, locale, dto, errorMessage);
            return "admin/info/index";
        }
    }

}
