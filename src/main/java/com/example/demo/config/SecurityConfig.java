package com.example.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.security.CustomAuthSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    private final CustomAuthSuccessHandler successHandler;


    public SecurityConfig(CustomAuthSuccessHandler successHandler) {
        this.successHandler = successHandler; 
    }

   
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/myshop/", "/myshop/login", "/myshop/product",
                		  "/myshop/register",
                		  "/myshop/assign",
                		  "/myshop/searchProducts/**",
                		  "/myshop/addRegister/**", 
                		"/fontawesome/**",
                		"/bootstrap/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",
                        "/webjars/**").permitAll()
                .requestMatchers("/myshop/user/**").hasAuthority("ROLE_USER")
                .requestMatchers("/myshop/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/myshop/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/role-redirect", true)
                .failureUrl("/myshop/login?error")
                .successHandler(successHandler)

                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/myshop/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)  
                .permitAll()
            );
        return http.build();
    }
}