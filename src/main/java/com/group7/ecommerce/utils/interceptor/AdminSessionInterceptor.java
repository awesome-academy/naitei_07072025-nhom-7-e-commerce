package com.group7.ecommerce.utils.interceptor;

import com.group7.ecommerce.dto.response.JwtResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class AdminSessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();
        if (uri.startsWith("/css") || uri.startsWith("/js") ||
                uri.startsWith("/images") || uri.startsWith("/auth") ||
                uri.startsWith("fonts") || uri.startsWith("/sql") || uri.startsWith("/webfonts")) {
            return true;
        }

        // check all session requests /admin/*
        if (uri.startsWith("/admin")) {
            HttpSession session = request.getSession(false);

            if (session != null) {
                JwtResponse jwtResponse = (JwtResponse) session.getAttribute("jwtResponse");

                if (jwtResponse != null) {
                    request.setAttribute("currentUser", jwtResponse);

                    log.debug("User {} authenticated for {}", jwtResponse.getUsername(), uri);
                    return true;
                }
            }

            // Không có session hợp lệ -> redirect login
            log.warn("Unauthorized access to {}, redirecting to login", uri);
            response.sendRedirect("/auth/admin/login");
            return false;
        }

        return true;
    }
}
