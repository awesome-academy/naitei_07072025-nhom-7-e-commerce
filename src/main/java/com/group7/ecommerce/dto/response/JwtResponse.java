package com.group7.ecommerce.dto.response;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String username;
    private String fullName;

    public JwtResponse(String accessToken, String email, String username, String fullName) {
        this.token = accessToken;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
    }
}
