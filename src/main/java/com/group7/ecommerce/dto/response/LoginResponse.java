package com.group7.ecommerce.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private LocalDateTime loginTime;

    public LoginResponse(String token, String username, String email, String fullName,
                         String phone, String role) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.loginTime = LocalDateTime.now();
    }
}
