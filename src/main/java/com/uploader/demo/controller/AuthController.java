package com.uploader.demo.controller;

import com.uploader.demo.dto.AuthRequest;
import com.uploader.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // demo user
    private final String USERNAME = "admin";
    // encoded using https://bcrypt-generator.com/
    private final String PASSWORD = "$2a$12$W1VFzbVwBaO/s/Ls7at1QOvoQa/NJbJFD8P8iN7W0hC1BzYLVCFX6"; // admin123

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        if (USERNAME.equals(request.getUsername()) &&
                passwordEncoder.matches(request.getPassword(), PASSWORD)) {

            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(Map.of("token", token));
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }
}

