package com.business.expensetracker.controller;

import com.business.expensetracker.config.JwtUtil;
import com.business.expensetracker.entity.User;
import com.business.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        String username = request.get("username") == null ? "" : request.get("username").trim();
        String password = request.get("password");
        if (!username.matches("[A-Za-z0-9_.-]{3,50}")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username must be 3-50 characters and use only letters, numbers, ., _, or -"));
        }
        if (password == null || password.length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 8 characters"));
        }
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username is already in use"));
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Account created. Please log in."));
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(username);

        // Use explicit Set-Cookie header to specify SameSite; add Secure in non-local environments
        boolean isLocal = true; // naive default; adjust if needed based on env
        String cookie = "token=" + jwt + "; HttpOnly; Path=/; SameSite=Lax" + (isLocal ? "" : "; Secure");
        response.addHeader("Set-Cookie", cookie);

        return Map.of("message", "Login successful");
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletResponse response) {
        String cookie = "token=; HttpOnly; Path=/; Max-Age=0; SameSite=Lax";
        response.addHeader("Set-Cookie", cookie);
        return Map.of("message", "Logged out successfully");
    }
}
