package com.group7.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionInfo {
    private String jwtToken;
    private String tokenType;
    private String email;
    private String username;
    private String fullName;

    // Constructor tiện lợi từ JwtResponse
    public UserSessionInfo(JwtResponse jwtResponse) {
        this.jwtToken = jwtResponse.getToken();
        this.tokenType = jwtResponse.getType();
        this.email = jwtResponse.getEmail();
        this.username = jwtResponse.getUsername();
        this.fullName = jwtResponse.getFullName();
    }
}