package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.User;
import com.example.demo.reposistory.UserReposistory;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadTestUser(UserReposistory userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.findByEmail("user@test.com") == null) {
                User user = new User();
                user.setName("testuser");
                user.setEmail("user@test.com");
                user.setPassword(passwordEncoder.encode("12345")); // 🔑 Must encode
                user.setRole("ROLE_USER");
                userRepository.save(user);
            }

            if (userRepository.findByEmail("admin@test.com") == null) {
                User admin = new User();
                admin.setName("admin");
                admin.setEmail("admin@test.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // 🔑 Must encode
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
            }
        };
    }
}

