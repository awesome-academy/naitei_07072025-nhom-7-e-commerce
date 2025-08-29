package com.group7.ecommerce.utils.helper;

import com.group7.ecommerce.dto.request.ChangePasswordDto;
import com.group7.ecommerce.dto.response.JwtResponse;
import com.group7.ecommerce.dto.response.ShowProfileResponse;
import com.group7.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordHelper {

    private final UserService userService;
    private final MessageSource messageSource;

    // Helper method to load profile data when there are errors
    public void loadProfileDataWithErrors(HttpServletRequest request, Model model, Locale locale,
                                             ChangePasswordDto dto, String errorMessage) {
        JwtResponse currentUser = (JwtResponse) request.getAttribute("currentUser");

        try {
            ShowProfileResponse showProfileResponse = userService.showProfileAdmin(currentUser);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("profileUser", showProfileResponse);
            model.addAttribute("changePasswordDto", dto);

            if (errorMessage != null) {
                model.addAttribute("errorMessage", errorMessage);
            }

            log.info("Admin profile loaded with errors for: {}", currentUser.getUsername());

        } catch (Exception e) {
            log.error("Error loading admin profile for username: {}", currentUser.getUsername(), e);
            model.addAttribute("error",
                    messageSource.getMessage("admin.profile.error.message", null, locale));
        }

    }
}
