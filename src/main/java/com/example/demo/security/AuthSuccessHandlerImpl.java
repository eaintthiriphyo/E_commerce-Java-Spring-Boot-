package com.example.demo.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.service.UserService;

import java.io.IOException;

@Component
public class AuthSuccessHandlerImpl implements AuthenticationSuccessHandler {

	 @Autowired
	    private UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        String email = authentication.getName();
        com.example.demo.entity.User user = userService.getUserByEmail(email);

        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            response.sendRedirect("/myshop/admin/dashboard");
        } else {
            response.sendRedirect("/myshop/");
        }
    }
}
