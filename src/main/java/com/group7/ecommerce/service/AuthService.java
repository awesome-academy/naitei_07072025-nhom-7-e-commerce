package com.group7.ecommerce.service;

import com.group7.ecommerce.dto.request.LoginDto;
import com.group7.ecommerce.dto.response.JwtResponse;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.repository.UserRepository;
import com.group7.ecommerce.utils.CustomUserDetails;
import com.group7.ecommerce.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginDto loginDto) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmailOrUsername(),
                        loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Get user info
        Optional<User> userOpt = userRepository.findByEmailOrUsername(
                loginDto.getEmailOrUsername(), loginDto.getEmailOrUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return new JwtResponse(jwt, user.getEmail(), user.getUsername(), user.getFullName());
        }

        throw new RuntimeException("User not found after authentication");
    }
}
