package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.dto.request.LoginDto;
import com.group7.ecommerce.dto.request.UserRegistrationDto;
import com.group7.ecommerce.dto.request.VerifyOtpDto;
import com.group7.ecommerce.dto.response.JwtResponse;
import com.group7.ecommerce.dto.response.UserSessionInfo;
import com.group7.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller("adminAuthController")
@RequestMapping("/auth/admin")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final MessageSource messageSource;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("admin", new UserRegistrationDto());
        return "register/index";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("admin") UserRegistrationDto dto,
                                  BindingResult bindingResult,
                                  @RequestParam("retypePassword") String retypePassword,
                                  Model model,
                                  RedirectAttributes redirectAttributes,
                                  Locale locale) {

        log.info("Processing admin registration for email: {}", dto.getEmail());

        // Validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Registration validation failed for email: {}", dto.getEmail());
            return "register/index";
        }

        // Password confirmation check
        if (!dto.getPassword().equals(retypePassword)) {
            String passwordError = messageSource.getMessage("admin.register.password.mismatch", null, locale);
            model.addAttribute("passwordError", passwordError);
            log.warn("Password mismatch for email: {}", dto.getEmail());
            return "register/index";
        }

        try {
            // Register user
            String result = userService.registerUser(dto);
            log.info("Registration successful for email: {}", dto.getEmail());

            // Redirect to OTP verification page
            redirectAttributes.addFlashAttribute("email", dto.getEmail());
            String successMessage = messageSource.getMessage("admin.register.success", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

            return "redirect:/auth/admin/verify-otp";

        } catch (Exception e) {
            log.error("Registration failed for email: {}", dto.getEmail(), e);
            String errorMessage = messageSource.getMessage("admin.register.failed",
                    new Object[]{e.getMessage()}, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "register/index";
        }
    }

    @GetMapping("/verify-otp")
    public String showOtpVerificationForm(@RequestParam(required = false) String email,
                                          Model model,
                                          RedirectAttributes redirectAttributes,
                                          Locale locale) {

        // Check if email is provided (from registration or direct access)
        if (email == null || email.isEmpty()) {
            // Try to get from flash attributes
            if (!model.containsAttribute("email")) {
                String errorMessage = messageSource.getMessage("admin.verify.register.required", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/auth/admin/register";
            }
        } else {
            model.addAttribute("email", email);
        }

        return "register/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            Model model,
                            RedirectAttributes redirectAttributes,
                            Locale locale) {

        log.info("Processing OTP verification for email: {}", email);

        try {
            VerifyOtpDto verifyDto = new VerifyOtpDto();
            verifyDto.setEmail(email);
            verifyDto.setOtp(otp);

            String result = userService.verifyOtp(verifyDto);
            log.info("OTP verification successful for email: {}", email);

            String successMessage = messageSource.getMessage("admin.verify.success", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/auth/admin/login";

        } catch (Exception e) {
            log.error("OTP verification failed for email: {}", email, e);

            model.addAttribute("email", email);
            String errorMessage = messageSource.getMessage("admin.verify.failed",
                    new Object[]{e.getMessage()}, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "register/verify-otp";
        }
    }

    @PostMapping("/resend-otp")
    @ResponseBody
    public String resendOtp(@RequestParam("email") String email, Locale locale) {

        log.info("Processing OTP resend for email: {}", email);

        try {
            String result = userService.resendOtp(email);
            log.info("OTP resend successful for email: {}", email);
            return messageSource.getMessage("admin.resend.success", null, locale);

        } catch (Exception e) {
            log.error("OTP resend failed for email: {}", email, e);
            String errorMessage = messageSource.getMessage("admin.resend.failed",
                    new Object[]{e.getMessage()}, locale);
            throw new RuntimeException(errorMessage);
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login/index";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginDto") LoginDto loginDto,
                               BindingResult bindingResult,
                               Locale locale,
                               Model model,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {

        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            return "login/index";
        }

        try {
            JwtResponse jwtResponse = userService.authenticateUser(loginDto);
            String successMessage = messageSource.getMessage("auth.login.success", null, locale);

            // Lưu JWT token và thông tin user vào session
            UserSessionInfo userSessionInfo = new UserSessionInfo(jwtResponse);
            HttpSession session = request.getSession();
            session.setAttribute("userSessionInfo", userSessionInfo);

            // Thêm success message
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

            return "redirect:/admin/home";

        } catch (AuthenticationException e) {
            // Xử lý lỗi authentication
            String errorMessage = messageSource.getMessage("auth.login.error",
                    new Object[]{e.getMessage()}, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "login/index";

        } catch (Exception e) {
            // Xử lý các lỗi khác
            String errorMessage = messageSource.getMessage("auth.login.general.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "login/index";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes, Locale locale) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            log.info("Admin session invalidated");
        }

        String logoutMessage = messageSource.getMessage("auth.logout.success", null, locale);
        redirectAttributes.addFlashAttribute("successMessage", logoutMessage);

        return "redirect:/auth/admin/login";
    }
}
