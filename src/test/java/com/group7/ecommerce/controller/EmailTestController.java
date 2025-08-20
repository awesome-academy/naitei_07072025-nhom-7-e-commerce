package com.group7.ecommerce.controller;

import com.group7.ecommerce.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/simple-email")
    public ResponseEntity<?> testSimpleEmail(@RequestParam String email) {
        try {
            emailService.sendTestEmail(email);
            return ResponseEntity.ok("Email sent to: " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/otp-debug")
    public ResponseEntity<?> testOtpDebug(@RequestParam String email) {
        try {
            String testOtp = "123456";
            emailService.sendOtpEmailWithDebug(email, testOtp);
            return ResponseEntity.ok("Debug OTP sent to: " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
