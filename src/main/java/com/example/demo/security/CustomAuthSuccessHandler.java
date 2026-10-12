package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Get the logged-in user's email
        String email = authentication.getName();

        // Fetch the User object from DB
        User user = userService.getUserByEmail(email);

        // Save user in session
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        // Check the user's roles
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = "/myshop/"; // default for normal user

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/myshop/admin/dashboard";
                break; // stop after finding admin
            } else if (role.equals("ROLE_USER")) {
                redirectUrl = "/myshop/"; // normal user
            }
            // You can add more roles here if needed
        }

        // Redirect user based on role
        response.sendRedirect(redirectUrl);
    }
}
